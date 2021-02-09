package com.kongwen.test;

import com.kongwen.rpc.util.ReflectUtil;

/**
 * @Author: WenGang
 */
public class Test {
    public static void main(String[] args) {
        String mainClassName = ReflectUtil.getStackTrace();
        System.out.println(mainClassName);
    }
}
