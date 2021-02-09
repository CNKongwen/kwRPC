package com.kongwen.rpc.exception;

import com.kongwen.rpc.enumeration.RpcError;

/**
 * Rpc调用异常
 *
 * @Author: WenGang
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }

}
