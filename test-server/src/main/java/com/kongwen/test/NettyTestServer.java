package com.kongwen.test;

import com.kongwen.rpc.annotation.ServiceScan;
import com.kongwen.rpc.serializer.CommonSerializer;
import com.kongwen.rpc.transport.server.NettyServer;

/**
 * 测试用服务端
 * @Author: WenGang
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }

}
