package com.kongwen.rpc.util;


import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.entity.RpcResponse;
import com.kongwen.rpc.enumeration.ResponseType;
import com.kongwen.rpc.enumeration.RpcError;
import com.kongwen.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测Rpc请求与响应信息
 *
 * @Author: WenGang
 */
public class RpcMessageChecker {
    public static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker() {
    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        //rpcResponse为空说明调用服务失败
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        //rpcRequest和rpcResponse的ID不匹配
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        //rpc响应状态码非SUCCESS时也返回异常
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseType.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
