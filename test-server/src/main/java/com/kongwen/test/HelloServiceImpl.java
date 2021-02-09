package com.kongwen.test;

import com.kong.rpc.HelloObject;
import com.kong.rpc.HelloService;
import com.kongwen.rpc.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: WenGang
 */
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "这是ServiceImpl方法";
    }
}
