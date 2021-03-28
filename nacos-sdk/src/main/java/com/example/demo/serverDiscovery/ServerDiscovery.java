package com.example.demo.serverDiscovery;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.demo.constant.NacosServerProperties;
import com.example.demo.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerDiscovery {

    private static volatile NamingService namingService;

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws NacosException, InterruptedException {
        String serverAddr = NacosServerProperties.serverAddr;
        String serviceName = "austin-provider-server";
        namingService = NacosFactory.createNamingService(serverAddr);
        registerInstance(serviceName, "1.1.1.1", 9091, "BeautifulDay");
        // deregisterInstance("austin-provider-server", "1.1.1.1", 9090);
        List<Instance> instanceList = getAllInstances(serviceName);
        log.info(JsonUtil.formatString(instanceList));
        instanceList = selectInstances(serviceName, true);
        log.info(JsonUtil.formatString(instanceList));
        instanceList = selectInstances(serviceName, false);
        log.info(JsonUtil.formatString(instanceList));
        Instance instance = selectOneHealthyInstance(serviceName);
        log.info(JsonUtil.formatString(instance));

        EventListener listener = (event)-> {
            log.info(JsonUtil.formatString(event));
        };
        executorService.submit(()-> {
            try {
                subscribe(serviceName, listener);
            } catch (NacosException e) {
                log.error("", e);
            }
        });
        TimeUnit.SECONDS.sleep(2);
        unsubscribe(serviceName, listener);

    }

    private static void registerInstance(String serviceName, String ip, int port, String clusterName) throws NacosException {
        namingService.registerInstance(serviceName, ip, port, clusterName);
    }

    private static void deregisterInstance(String serviceName, String ip, int port) throws NacosException {
        namingService.deregisterInstance(serviceName, ip, port);
    }

    private static List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    private static List<Instance> selectInstances(String serviceName, boolean healthy) throws NacosException {
        return namingService.selectInstances(serviceName, healthy);
    }

    private static Instance selectOneHealthyInstance(String serviceName) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName);
    }

    private static void subscribe(String serviceName, EventListener listener) throws NacosException {
        namingService.subscribe(serviceName, listener);
    }

    private static void unsubscribe(String serviceName, EventListener listener) throws NacosException {
        namingService.unsubscribe(serviceName, listener);
    }
}
