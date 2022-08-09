package com.chick.net.disk.driver.event;

import com.chick.net.disk.driver.base.R;
import com.chick.net.disk.driver.client.NetDiskClient;

/**
 * @ClassName DiskEvent
 * @Author xiaokexin
 * @Date 2022-07-27 14:49
 * @Description NetDiskEvent
 * @Version 1.0
 */
public interface NetDiskEvent {

    /**
     * @Author xkx
     * @Description 登录
     * @Date 2022-07-27 14:52
     * @Param
     * @return
     **/
    void init();

    /**
    * @Author xkx
    * @Description 登录
    * @Date 2022-07-27 14:52
    * @Param
    * @return
    **/
    R login(NetDiskClient netDiskClient);

    /**
     * @Author xkx
     * @Description 登录状态
     * @Date 2022-07-27 14:52
     * @Param
     * @return
     **/
    Boolean loginState();

    /**
    * @Author xkx
    * @Description 刷新token
    * @Date 2022-07-27 14:52
    * @Param []
    * @return void
    **/
    void reFreshToken(NetDiskClient netDiskClient);
}
