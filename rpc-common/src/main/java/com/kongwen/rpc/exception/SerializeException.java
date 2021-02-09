package com.kongwen.rpc.exception;

/**
 * 序列化异常
 *
 * @Author: WenGang
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg) {
        super(msg);
    }
}
