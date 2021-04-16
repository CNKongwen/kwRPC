package com.kongwen.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 基于权重的负载均衡
 * @Author: WenGang
 */
public class RandomLoadByWeightBalancer implements LoadBalancer {
    private final Random random = new Random();
    @Override
    public Instance select(List<Instance> instances, String address) {
        int len = instances.size();
        int totalWeight = 0;
        boolean sameWeight = true;
        for (int i = 0; i < len; i++) {
            int curWeight = (int)instances.get(i).getWeight();
            totalWeight += curWeight;
            if (sameWeight &&  i > 0 && curWeight != (int)instances.get(i - 1).getWeight()) sameWeight = false;
        }
        if (!sameWeight && totalWeight > 0) {
            int offset = random.nextInt(totalWeight);
            for (int i = 0; i < len; i++) {
                offset -= (int)instances.get(i).getWeight();
                if (offset < 0) return instances.get(i);
            }
        }
        //若所有服务实例权重一致，则直接随机返回一个实例
        return instances.get(random.nextInt(len));
    }
}
