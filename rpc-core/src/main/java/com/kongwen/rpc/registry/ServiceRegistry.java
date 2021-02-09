package com.kongwen.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 远程服务注册接口
 * @Author: WenGang
 */
public interface ServiceRegistry {

    /**
     * @param serviceName:需要注册的服务名
     * @param inetSocketAddress:提供该服务的服务端地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
