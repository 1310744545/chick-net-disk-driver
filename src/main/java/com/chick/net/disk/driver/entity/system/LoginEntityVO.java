package com.chick.net.disk.driver.entity.system;

import lombok.Data;

/**
 * @ClassName login
 * @Author xiaokexin
 * @Date 2022-07-28 13:17
 * @Description login
 * @Version 1.0
 */
@Data
public class LoginEntityVO {
    // 自定义参数用户名
    private String username;
    // 自定义参数密码
    private String password;
    // 登录获取参数
    private String accessToken;
    // token
    private String refreshToken;
    // 登录状态
    private boolean loginState;
    // 登录类型
    private String loginType;
    // 应用名
    private String name;
}
