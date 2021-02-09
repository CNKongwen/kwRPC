package com.kongwen.rpc.hook;

import com.kongwen.rpc.factory.ThreadPoolFactory;
import com.kongwen.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关闭服务端的钩子
 * @Author: WenGang
 */
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);


    private static final ShutdownHook shutdownHook = new ShutdownHook();


    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }


    /**
     * 注销服务端注册的所有服务
     */
    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
