package com.kongwen.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮转选择
 * @Author: WenGang
 */
public class RoundLoadBalancer implements LoadBalancer{

    //上一次选择的坐标
    private int index = -1;

    @Override
    public Instance select(List<Instance> instances) {
        index++;
        index %= instances.size();
        return instances.get(index);
    }
}
