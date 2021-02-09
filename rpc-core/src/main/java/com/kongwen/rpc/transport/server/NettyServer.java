package com.kongwen.rpc.transport.server;

import com.kongwen.rpc.codec.CommonDecoder;
import com.kongwen.rpc.codec.CommonEncoder;
import com.kongwen.rpc.hook.ShutdownHook;
import com.kongwen.rpc.provider.ServiceProviderImpl;
import com.kongwen.rpc.registry.NacosServiceRegistry;
import com.kongwen.rpc.serializer.CommonSerializer;
import com.kongwen.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Netty方式构建服务端
 *
 * @Author: WenGang
 */
public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }

    @Override
    public void start() {
        //添加钩子
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .option(ChannelOption.SO_BACKLOG, 256)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                //心跳监测
                                ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                        //加入编码器
                                        .addLast(new CommonEncoder(serializer))
                                        //加入解码器
                                        .addLast(new CommonDecoder())
                                        //加入服务端处理器
                                        .addLast(new NettyServerHandler());
                            }
                        });
                ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
    }
}
