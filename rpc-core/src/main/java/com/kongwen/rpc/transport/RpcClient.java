package com.kongwen.rpc.transport;

import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.serializer.CommonSerializer;

/**
 * Rpc客户端接口
 * @Author: WenGang
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);
}
