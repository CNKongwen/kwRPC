package com.kongwen.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性Hash负载均衡器
 * @Author: WenGang
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public Instance select(List<Instance> instances, String address) {
        //获取serviceName
        String serviceName = instances.get(0).getServiceName();
        //先查找本地缓存中是否已有一致性哈希选择器
        ConsistentHashSelector selector = selectors.get(serviceName);
        //若没有或者注册中心的相应的服务列表已经发生变化，则新构筑一个一致性哈希选择器
        if (selector == null || !selector.getRealNodes().equals(instances)) {
            selectors.put(serviceName, new ConsistentHashSelector(instances));
            selector = selectors.get(serviceName);
        }
        //根据客户端的ip地址做hash值打到地址环上，顺时针找第一个节点来进行服务调用
        return selector.selectInstance(address);
    }

    private static final class ConsistentHashSelector {
        //该一致性哈希选择器对应的服务名称
        private final String serviceName;
        //存储虚拟节点hash值->实际节点的映射
        private final TreeMap<Integer, Instance> virtualNodes;
        //每个实际节点对应的虚拟节点数量
        private final int replicaNumber;
        //该一致性哈希选择器对应的实际节点列表
        private final List<Instance> realNodes;

        ConsistentHashSelector(List<Instance> instances) {
            this.serviceName = instances.get(0).getServiceName();
            this.realNodes = instances;
            this.replicaNumber = 20;
            this.virtualNodes = new TreeMap<>();
            //为每个实际节点生成虚拟节点
            for (Instance instance : instances) {
                for (int i = 0; i < replicaNumber; i++) {
                    //给虚拟节点拼字符串键用于hash取值，形式为"ip:port&&VNi"其中i为虚拟节点编号
                    String virtualNodeKey = instance.getIp() + ":" + instance.getPort() + "&&VN" + i;
                    int hash = getHash(virtualNodeKey);
                    //将hash-instance映射放入TreeMap中
                    virtualNodes.put(hash, instance);
                }
            }
        }

        /**
         * 根据客户端Ip地址选择服务实例
         * @param address
         * @return
         */
        public Instance selectInstance(String address) {
            int hash = getHash(address);
            //获取第一个hash大于等于address hash值的虚拟节点，即地址环上顺时针找到的第一个节点
            Instance res = virtualNodes.ceilingEntry(hash).getValue();
            //若没有就返回第一个节点
            if (res == null) return virtualNodes.firstEntry().getValue();
            return res;
        }

        public List<Instance> getRealNodes() {
            return realNodes;
        }
    }

    /**
     * FNV1_32_HASH算法求hash值
     */
    private static int getHash(String s) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < s.length(); i++)
            hash = (hash ^ s.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
