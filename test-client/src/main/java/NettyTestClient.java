import com.kong.rpc.ByeService;
import com.kong.rpc.ComputeService;
import com.kong.rpc.HelloObject;
import com.kong.rpc.HelloService;
import com.kongwen.rpc.serializer.CommonSerializer;
import com.kongwen.rpc.transport.RpcClientProxy;
import com.kongwen.rpc.transport.client.NettyClient;

/**
 * 测试用客户端
 * @Author: WenGang
 */
public class NettyTestClient {
    public static void main(String[] args) {
        NettyClient client = new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println("Rpc调用服务hellosService.hello：" + res);
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        System.out.println("Rpc调用服务ByeService.bye：");
        System.out.println(byeService.bye( "Netty"));
        ComputeService computeService = rpcClientProxy.getProxy(ComputeService.class);
        System.out.println("Rpc调用服务ComputeService.add：");
        System.out.println(computeService.add(1, 2));
    }
}
