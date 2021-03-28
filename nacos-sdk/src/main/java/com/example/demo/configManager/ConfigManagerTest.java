package com.example.demo.configManager;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.example.demo.constant.NacosServerProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author 92494
 */
@Slf4j
public class ConfigManagerTest {

    private static final String aa = "123";

    private static volatile ConfigService configService;
    private String ss;
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {
        try {
            String serverAddr = NacosServerProperties.serverAddr;
            String dataId = "austin-provider-server";
            String group = "DEFAULT_GROUP";
            configService = NacosFactory.createConfigService(serverAddr);
            publishConfig(dataId, group, "123");

            String content = getConfig(dataId, group);
            System.out.println(content);
            Listener listener = new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info(configInfo);
                }
            };
            executorService.submit(()-> {
                try {
                    addListener(dataId, group,listener);
                } catch (NacosException e) {
                    log.error("", e);
                }
            });
            Thread.sleep(1000);
            removeListener(dataId, group, listener);
            removeConfig(dataId, group);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 推送配置
     * 
     * @param dataId
     * @param group
     * @param content
     * @throws NacosException
     */
    private static void publishConfig(String dataId, String group, String content) throws NacosException {
        configService.publishConfig(dataId, group, content, ConfigType.getDefaultType().getType());
    }

    private static void removeConfig(String dataId, String group) throws NacosException {
        configService.removeConfig(dataId, group);
    }

    /**
     * 读取配置
     * 
     * @param dataId
     * @param group
     * @return
     * @throws NacosException
     */
    private static String getConfig(String dataId, String group) throws NacosException {
        return configService.getConfig(dataId, group, 5000);
    }

    public static void addListener(String dataId, String group, Listener listener) throws NacosException {
        configService.addListener(dataId, group, listener);
    }

    public static void removeListener(String dataId, String group, Listener listener) {
        configService.removeListener(dataId, group, listener);
    }

}
