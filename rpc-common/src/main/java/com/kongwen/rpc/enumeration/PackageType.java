package com.kongwen.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 数据包类型枚举
 *
 * @Author: WenGang
 */
@Getter
@AllArgsConstructor
public enum  PackageType {
    RPC_REQUEST_PACK(0),
    RPC_RESPONSE_PACK(1);

    private final int typeCode;
}
