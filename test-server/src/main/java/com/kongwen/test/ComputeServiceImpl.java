package com.kongwen.test;

import com.kong.rpc.ComputeService;
import com.kongwen.rpc.annotation.Service;

/**
 * @Author: WenGang
 */
@Service
public class ComputeServiceImpl implements ComputeService {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer minus(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        return a / b;
    }
}
