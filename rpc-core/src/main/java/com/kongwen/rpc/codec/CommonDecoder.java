package com.kongwen.rpc.codec;

import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.entity.RpcResponse;
import com.kongwen.rpc.enumeration.PackageType;
import com.kongwen.rpc.enumeration.RpcError;
import com.kongwen.rpc.exception.RpcException;
import com.kongwen.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器，用于解码自定义数据包内容，并拦截非法数据包
 * 数据包：魔数 + 请求/响应标识码 + 序列化方式标识码 + 数据长度 + RpcRequest/RpcResponse
 *
 * @Author: WenGang
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABB;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //获取魔数
        int magicNumber = byteBuf.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magicNumber);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //获取请求/响应标识码
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.RPC_REQUEST_PACK.getTypeCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RPC_RESPONSE_PACK.getTypeCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //获取序列化方式标识码
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        //获取数据长度
        int length = byteBuf.readInt();
        //获取请求或响应的字节数组，并反序列化
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        list.add(obj);
    }
}
