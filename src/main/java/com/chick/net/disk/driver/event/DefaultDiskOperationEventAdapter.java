package com.chick.net.disk.driver.event;

import com.chick.net.disk.driver.config.AliyunConfig;
import com.chick.net.disk.driver.config.NetDiskConfig;
import com.chick.net.disk.driver.event.netDiskOperationEvent.AliyunNetDiskOperationEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName DefaultDiskOperationEventAdapter
 * @Author xiaokexin
 * @Date 2022-07-30 13:50
 * @Description DefaultDiskOperationEventAdapter
 * @Version 1.0
 */
@Component
public class DefaultDiskOperationEventAdapter implements NetDiskOperationEventAdapter {
    @Resource
    private AliyunNetDiskOperationEvent aliyunNetDiskOperationEvent;

    @Override
    public NetDiskOperationEvent init(NetDiskConfig netDiskConfig) {
        switch (netDiskConfig.getName()) {
            case AliyunConfig.name:
                return aliyunNetDiskOperationEvent;
            default:
                return null;
        }
    }
}
