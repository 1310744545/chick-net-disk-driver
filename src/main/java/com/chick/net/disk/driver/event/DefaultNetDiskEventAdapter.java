package com.chick.net.disk.driver.event;

import com.chick.net.disk.driver.config.AliyunConfig;
import com.chick.net.disk.driver.config.NetDiskConfig;
import com.chick.net.disk.driver.event.netDiskEvent.AliyunNetDiskEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName NetDiskEventAdapter
 * @Author xiaokexin
 * @Date 2022-07-30 13:47
 * @Description 默认网盘登录事件代理器
 * @Version 1.0
 */
@Component
public class DefaultNetDiskEventAdapter implements NetDiskEventAdapter {

    @Resource
    private AliyunNetDiskEvent aliyunNetDiskEvent;

    @Override
    public NetDiskEvent init(NetDiskConfig netDiskConfig) {
        switch (netDiskConfig.getName()){
            case AliyunConfig.name:
                return aliyunNetDiskEvent;
            default:
                return null;
        }
    }
}
