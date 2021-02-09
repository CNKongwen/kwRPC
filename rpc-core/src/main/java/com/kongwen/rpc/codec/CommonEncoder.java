package com.kongwen.rpc.codec;

import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.enumeration.PackageType;
import com.kongwen.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器
 * @Author: WenGang
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABB;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //写入魔数
        byteBuf.writeInt(MAGIC_NUMBER);
        //写入请求/响应标识码
        if (o instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.RPC_REQUEST_PACK.getTypeCode());
        } else {
            byteBuf.writeInt(PackageType.RPC_RESPONSE_PACK.getTypeCode());
        }
        //写入序列化标识码
        byteBuf.writeInt(serializer.getCode());
        //序列化请求/响应
        byte[] bytes = serializer.serialize(o);
        //写入序列化后的字节数组长度
        byteBuf.writeInt(bytes.length);
        //写入序列化后的字节数组
        byteBuf.writeBytes(bytes);
    }
}
