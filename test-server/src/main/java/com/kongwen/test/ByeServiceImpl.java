package com.kongwen.test;

import com.kong.rpc.ByeService;
import com.kongwen.rpc.annotation.Service;

/**
 * @Author: WenGang
 */
@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
