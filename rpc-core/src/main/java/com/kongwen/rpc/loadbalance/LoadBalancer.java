package com.kongwen.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡器接口
 * @Author: WenGang
 */
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
