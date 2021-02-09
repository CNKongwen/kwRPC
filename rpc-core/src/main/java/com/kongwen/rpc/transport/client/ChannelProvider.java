package com.kongwen.rpc.transport.client;

import com.kongwen.rpc.codec.CommonDecoder;
import com.kongwen.rpc.codec.CommonEncoder;
import com.kongwen.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 构建Channel并对外提供Channel
 * @Author: WenGang
 */
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();
    /**
     * 初始化bootstrap和eventLoopGroup
     * @return
     */
    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //设置最大的连接等待时间，超时则连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //开启TCP连接的心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //禁用TCP的Nagle算法
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    /**
     * 根据给定的服务端地址和序列化器构建并提供Channel对象
     * @param inetSocketAddress
     * @param serializer
     * @return
     */
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //添加编码器
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        //添加心跳监测
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        //添加解码器
                        .addLast(new CommonDecoder())
                        //添加客户端处理器
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            logger.error("客户端连接服务端时发现错误", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    /**
     * 尝试与服务端建立连接并返回Channel
     * @param bootstrap
     * @return
     */
    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("客户端与服务端连接成功");
                completableFuture.complete(channelFuture.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

}
