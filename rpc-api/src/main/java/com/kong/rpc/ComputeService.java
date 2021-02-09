package com.kong.rpc;

/**
 * @Author: WenGang
 */
public interface ComputeService {
    Integer add(Integer a, Integer b);
    Integer minus(Integer a, Integer b);
    Integer multiply(Integer a, Integer b);
    Integer divide(Integer a, Integer b);
}
