package com.kongwen.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 远程服务发现接口
 * @Author: WenGang
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体所在地址
     * @param serviceName 服务名称
     * @return 服务实体所在地址
     */
    InetSocketAddress lookupService(String serviceName);

}
