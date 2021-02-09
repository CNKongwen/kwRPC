package com.kongwen.rpc.provider;

/**
 * 服务端本地的服务注册表
 * @Author: WenGang
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);

}
