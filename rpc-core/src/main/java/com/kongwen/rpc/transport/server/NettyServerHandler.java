package com.kongwen.rpc.transport.server;

import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.entity.RpcResponse;
import com.kongwen.rpc.factory.SingletonFactory;
import com.kongwen.rpc.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty服务端用于处理RpcRequest的处理器
 * @Author: WenGang
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            if (rpcRequest.getHeartBeat()) {
                logger.info("收到客户端心跳包");
                return;
            }
            logger.info("服务端收到服务请求: {}", rpcRequest);
            //由requestHandler通过反射完成方法调用并得到结果
            Object result = requestHandler.handle(rpcRequest);
            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                ctx.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
            } else {
                logger.error("通道不可写");
            }
        } finally {
            //因为使用的是自己编写的编解码器，手动释放
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    //心跳机制
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
