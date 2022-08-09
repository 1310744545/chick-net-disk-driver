package com.chick.net.disk.driver.constant;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;

/**
 * @ClassName AliyunConstants
 * @Author xiaokexin
 * @Date 2022-07-27 20:22
 * @Description AliyunConstants
 * @Version 1.0
 */
public class AliyunConstants {
    public static final String HEADER_AUTHORITY = "passport.aliyundrive.com";
    public static final String HEADER_METHOD = "POST";
    public static final String HEADER_PATH = "passport.aliyundrive.com";
    public static final String HEADER_SCHEME = "https";
    public static final String HEADER_ACCEPT = "application/json, text/plain, */*";
    public static final String HEADER_ACCEPT_ENCODING = "gzip, deflate, br";
    public static final String HEADER_ACCEPT_LANGUAGE = "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6";
    public static final String HEADER_CONTENT_LENGTH = "4123";
    public static final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String HEADER_ORIGIN = "https://passport.aliyundrive.com";
    public static final String HEADER_REFERER = "https://passport.aliyundrive.com/mini_login.htm?lang=zh_cn&appName=aliyun_drive&appEntrance=web&styleType=auto&bizParams=&notLoadSsoView=false&notKeepLogin=false&isMobile=false&ad__pass__q__rememberLogin=true&ad__pass__q__rememberLoginDefaultValue=true&ad__pass__q__forgotPassword=true&ad__pass__q__licenseMargin=true&ad__pass__q__loginType=normal&hidePhoneCode=true&rnd=";
    public static final String HEADER_SEC_CH_UA = "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"103\", \"Chromium\";v=\"103\"";
    public static final String HEADER_SEC_CH_MOBILE = "?0";
    public static final String HEADER_SEC_CH_PLATFORM = "\"Windows\"";
    public static final String HEADER_SEC_FETCH_DEST = "empty";
    public static final String HEADER_SEC_FETCH_MODE = "cors";
    public static final String HEADER_SEC_FETCH_SITE = "same-origin";
    public static final String HEADER_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36 Edg/103.0.1264.62";


    public static final String LOGIN_TYPE_REFRESH_TOKEN = "refreshToken";
    public static final String LOGIN_TYPE_PASSWORD = "password";
    public static final String LOGIN_TYPE_QRCODE = "qrcode";

    public static final String INVALID_TOKEN_ERROR = "InvalidParameter.RefreshToken";
}
