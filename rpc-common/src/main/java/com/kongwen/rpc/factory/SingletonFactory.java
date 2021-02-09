package com.kongwen.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 *
 * @Author: WenGang
 */
public class SingletonFactory {
    private static Map<Class, Object> objMap = new HashMap<>();

    private SingletonFactory() {}

    /**
     *返回实例
     */
    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    objMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
