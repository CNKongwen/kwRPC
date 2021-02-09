package com.kongwen.rpc.handler;

import com.kongwen.rpc.entity.RpcRequest;
import com.kongwen.rpc.entity.RpcResponse;
import com.kongwen.rpc.enumeration.ResponseType;
import com.kongwen.rpc.provider.ServiceProvider;
import com.kongwen.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行真正的服务调用处理器
 * @Author: WenGang
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    /**
     * 根据请求的具体信息，根据本地服务注册表找到服务实例，调用下一方法执行服务
     * @param rpcRequest
     * @return
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = RequestHandler.serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 根据请求的具体信息和本地服务实例，调用方法并返回结果
     * @param rpcRequest
     * @param service
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseType.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }


}
