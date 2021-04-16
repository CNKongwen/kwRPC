package com.kongwen.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: WenGang
 */
public class RoundLoadByWeightBalancer implements LoadBalancer {
    //用于记录各个服务与其对应的原子序列整数
    private final Map<String, AtomicInteger> sequences = new ConcurrentHashMap<>();
    @Override
    public Instance select(List<Instance> instances, String address) {
        String serviceName = instances.get(0).getServiceName();
        //存储instance-weight映射
        final LinkedHashMap<Instance, IntegerWrapper> instance2WeightMap = new LinkedHashMap<>();
        int len = instances.size();
        int maxWeight = 0;
        int minWeight = Integer.MAX_VALUE;
        int weightSum = 0;
        for (int i = 0; i < len; i++) {
            int curWeight = (int)instances.get(i).getWeight();
            maxWeight = Math.max(curWeight, maxWeight);
            minWeight = Math.min(curWeight, minWeight);
            if (curWeight > 0) {
                weightSum += curWeight;
                instance2WeightMap.put(instances.get(i), new IntegerWrapper(curWeight));
            }
        }
        //根据serviceName获取对应的原子序列整数
        AtomicInteger sequence = sequences.get(serviceName);
        if (sequence == null) {
            sequences.put(serviceName, new AtomicInteger(0));
            sequence = sequences.get(serviceName);
        }
        //自增得到本次负载均衡所使用的序列号
        int curSequence = sequence.getAndIncrement();
        if (maxWeight > 0 && minWeight < maxWeight) {
            int mod = curSequence % weightSum;
            for (int i = 0; i < maxWeight; i++) {
                for (Map.Entry<Instance, IntegerWrapper> entry : instance2WeightMap.entrySet()) {
                    final Instance instance = entry.getKey();
                    final IntegerWrapper weight = entry.getValue();
                    if (mod == 0 && weight.getValue() > 0) return instance;
                    if (weight.getValue() > 0) {
                        mod--;
                        weight.decrement();
                    }
                }
            }
        }
        //若权重相同，则直接对长度取模进行普通的轮询负载均衡
        return instances.get(curSequence % len);
    }

    private static final class IntegerWrapper {
        private int value;

        public IntegerWrapper(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void decrement() {
            this.value--;
        }
    }
}
