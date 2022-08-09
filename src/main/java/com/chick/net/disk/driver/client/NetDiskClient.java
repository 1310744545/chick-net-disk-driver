package com.chick.net.disk.driver.client;

import com.chick.net.disk.driver.config.NetDiskConfig;
import com.chick.net.disk.driver.entity.system.LoginEntityVO;
import com.chick.net.disk.driver.event.NetDiskEvent;
import com.chick.net.disk.driver.event.NetDiskOperationEvent;
import lombok.Data;

/**
 * @ClassName NetDiskClient
 * @Author xiaokexin
 * @Date 2022-07-28 11:04
 * @Description NetDiskClient
 * @Version 1.0
 */
@Data
public class NetDiskClient {
    private String id;
    private String userId; // 网盘方的
    private String path;
    private String userName;
    private Long capacityTotal;
    private Long capacityUsed;
    private String netDiskName;
    private String icon;
    private String avatar;
    private String phone;
    private String driverId;
    private LoginEntityVO loginEntityVO;
    private NetDiskConfig netDiskConfig;
    private NetDiskEvent netDiskEvent;
    private NetDiskOperationEvent netDiskOperationEvent;
}
