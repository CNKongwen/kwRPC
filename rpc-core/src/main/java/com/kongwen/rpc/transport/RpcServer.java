package com.kongwen.rpc.transport;

import com.kongwen.rpc.serializer.CommonSerializer;

/**
 * Rpc服务端接口
 * @Author: WenGang
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    <T> void publishService(T service, String serviceName);

}
