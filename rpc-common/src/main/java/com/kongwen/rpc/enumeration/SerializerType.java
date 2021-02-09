package com.kongwen.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 各种序列化器和反序列化器对应编码
 *
 * @Author: WenGang
 */

@Getter
@AllArgsConstructor
public enum  SerializerType {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;
}
