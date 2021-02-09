package com.kongwen.rpc.transport;

import com.kongwen.rpc.annotation.Service;
import com.kongwen.rpc.annotation.ServiceScan;
import com.kongwen.rpc.enumeration.RpcError;
import com.kongwen.rpc.exception.RpcException;
import com.kongwen.rpc.provider.ServiceProvider;
import com.kongwen.rpc.registry.ServiceRegistry;
import com.kongwen.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 *Rpc服务端抽象类
 * @Author: WenGang
 */
public abstract class AbstractRpcServer implements RpcServer{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 在本地和注册中心注册服务
     * @param service
     * @param serviceName
     * @param <T>
     */
    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

    /**
     * 扫描SerciceScan注解指定包下所有Service注解的服务类，并注册到注册中心
     */
    public void scanServices() {
        //获取启动类类名
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            //若启动类无@ServiceScan 注解，报错
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少@ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }

        } catch (ClassNotFoundException e) {
            logger.error("出现未知异常");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        //获取@ServiceScan 注解指定的包名
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        //若包名为空则默认扫描启动类所在包
        if (basePackage.equals("")) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        //扫描获取basePackage的所有class文件
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        //对于有@Service注解的class文件，注册服务
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (IllegalAccessException  | InstantiationException e) {
                   logger.error("创建" + clazz + "服务类实例时有错误发生");
                   continue;
                }
                //若没有指定服务名，就默认为实现的接口名并注册服务
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }


}
