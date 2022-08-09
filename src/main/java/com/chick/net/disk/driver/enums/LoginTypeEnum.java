package com.chick.net.disk.driver.enums;

/**
 * @ClassName LoginTypeEnum
 * @Author xiaokexin
 * @Date 2022-08-05 15:53
 * @Description LoginTypeEnum
 * @Version 1.0
 */
public enum LoginTypeEnum {
    REFRESH_TOKEN("refreshToken"),
    PASSWORD("password"),
    QRCODE("qrcode"),
    SMS("sms");

    private String loginType;
    LoginTypeEnum(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public static void main(String[] args) {
        System.out.println(LoginTypeEnum.REFRESH_TOKEN.getLoginType());
    }
}
