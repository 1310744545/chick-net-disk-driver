package com.chick.net.disk.driver.factory;

import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.config.NetDiskConfig;
import com.chick.net.disk.driver.event.DefaultDiskOperationEventAdapter;
import com.chick.net.disk.driver.event.DefaultNetDiskEventAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @ClassName NetDiskClientFactory
 * @Author xiaokexin
 * @Date 2022-07-30 13:42
 * @Description 网盘客户端工厂
 * @Version 1.0
 */
@Component
public class NetDiskClientFactory {

    @Resource
    private DefaultDiskOperationEventAdapter defaultDiskOperationEventAdapter;
    @Resource
    private DefaultNetDiskEventAdapter defaultNetDiskEventAdapter;

    public NetDiskClient getMessageEvent(NetDiskConfig netDiskConfig) {
        NetDiskClient netDiskClient = new NetDiskClient();
        netDiskClient.setId(UUID.randomUUID().toString().replace("-", ""));
        netDiskClient.setPath(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        netDiskClient.setNetDiskOperationEvent(defaultDiskOperationEventAdapter.init(netDiskConfig));
        netDiskClient.setNetDiskEvent(defaultNetDiskEventAdapter.init(netDiskConfig));
        return netDiskClient;
    }
}
