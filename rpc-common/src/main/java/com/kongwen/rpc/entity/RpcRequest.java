package com.kongwen.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 封装的Rpc请求信息
 *
 * @Author: WenGang
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 请求调用的接口名称
     */
    private String interfaceName;
    /**
     * 请求调用方法的名称
     */
    private String methodName;
    /**
     * 请求调用方法的参数列表
     */
    private Object[] parameters;
    /**
     * 请求调用方法的参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
}
