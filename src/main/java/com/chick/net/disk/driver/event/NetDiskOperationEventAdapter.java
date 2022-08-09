package com.chick.net.disk.driver.event;

import com.chick.net.disk.driver.config.NetDiskConfig;

/**
 * @ClassName NetDiskEventAdapter
 * @Author xiaokexin
 * @Date 2022-07-30 13:48
 * @Description NetDiskEventAdapter
 * @Version 1.0
 */
public interface NetDiskOperationEventAdapter {

    NetDiskOperationEvent init(NetDiskConfig netDiskConfig);
}
