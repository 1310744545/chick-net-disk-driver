package com.chick.net.disk.driver.event.netDiskEvent;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chick.net.disk.driver.base.R;
import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.client.NetDiskDriverPool;
import com.chick.net.disk.driver.config.AliyunConfig;
import com.chick.net.disk.driver.constant.AliyunConstants;
import com.chick.net.disk.driver.event.NetDiskEvent;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;

/**
 * @ClassName AliyunNetDiskEvent
 * @Author xiaokexin
 * @Date 2022-07-27 14:50
 * @Description AliyunNetDiskEvent
 * @Version 1.0
 */
@Service
@Log4j2
public class AliyunNetDiskEvent implements NetDiskEvent {

    @Resource
    private NetDiskDriverPool netDiskDriverPool;

    @Override
    public void init() {
        String html = HttpUtil.get(AliyunConfig.miniLoginUrl);
        Elements scripts = Jsoup.parse(html).getElementsByTag("script");
        String viewConfig = "";
        String viewData = "";
        for (Element element : scripts) {
            if (element.html().contains("window.viewConfig")) {
                // viewConfig数据
                viewConfig = element.html().split("window.viewConfig = ")[1].split("window.viewData")[0].trim().replace(";", "");
                // viewData数据
                viewData = element.html().split("window.viewData = ")[1].split("window._lang")[0].trim().replace(";", "");
            }
        }
        JSONObject viewConfigJson = JSONObject.parseObject(viewConfig);
        JSONObject viewDataJson = JSONObject.parseObject(viewData);
        AliyunConfig.rsaExponent = viewConfigJson.getString("rsaExponent");
        AliyunConfig.rsaModulus = viewConfigJson.getString("rsaModulus");
        JSONObject apiJsonObject = viewConfigJson.getJSONObject("api");
        AliyunConfig.smsLoginApi = apiJsonObject.getString("smsLoginApi");
        AliyunConfig.loginApi = apiJsonObject.getString("loginApi");
        AliyunConfig.simLoginApi = apiJsonObject.getString("simLoginApi");
        AliyunConfig.preCheckApi = apiJsonObject.getString("preCheckApi");
        AliyunConfig.checkLoginApi = apiJsonObject.getString("checkLoginApi");
        AliyunConfig.smsLoginRegApi = apiJsonObject.getString("smsLoginRegApi");
        AliyunConfig.aKeyCheckApi = apiJsonObject.getString("aKeyCheckApi");
        AliyunConfig.getQRCodeApi = apiJsonObject.getString("getQRCodeApi");
        AliyunConfig.conLoginApi = apiJsonObject.getString("conLoginApi");
        AliyunConfig.sendSmsApi = apiJsonObject.getString("sendSmsApi");
        AliyunConfig.hasLoginApi = apiJsonObject.getString("hasLoginApi");
        AliyunConfig.recommendLoginFlowApi = apiJsonObject.getString("recommendLoginFlowApi");
        AliyunConfig.checkQRCodeApi = apiJsonObject.getString("checkQRCodeApi");
        AliyunConfig.accountCheckApi = apiJsonObject.getString("accountCheckApi");
        AliyunConfig.aKeyPushApi = apiJsonObject.getString("aKeyPushApi");

        AliyunConfig.umidEncryptAppName = viewDataJson.getString("umidEncryptAppName");
        AliyunConfig.appName = viewDataJson.getString("appName");
        AliyunConfig.awscCdn = viewDataJson.getString("awscCdn");
        AliyunConfig.umidServiceLocation = viewDataJson.getString("umidServiceLocation");
        AliyunConfig.umidToken = viewDataJson.getString("umidToken");
        AliyunConfig.nocaptchaAppKey = viewDataJson.getString("nocaptchaAppKey");
        JSONObject loginFormData = viewDataJson.getJSONObject("loginFormData");
        AliyunConfig.appEntrance = loginFormData.getString("appEntrance");
        AliyunConfig._csrf_token = loginFormData.getString("_csrf_token");
        AliyunConfig.lang = loginFormData.getString("lang");
        AliyunConfig.returnUrl = loginFormData.getString("returnUrl");
        AliyunConfig.hsiz = loginFormData.getString("hsiz");
        AliyunConfig.fromSite = loginFormData.getInteger("fromSite");
        AliyunConfig.bizParams = loginFormData.getString("bizParams");
        AliyunConfig.umidServer = viewDataJson.getString("umidServer");

        //解析
        String htmlLogin = HttpUtil.get(AliyunConfig.loginUrl);
        Elements loginScripts = Jsoup.parse(htmlLogin).getElementsByTag("script");
        for (Element element : loginScripts) {
            if (element.html().contains("Global")) {
                AliyunConfig.pdsEndpoint = element.html().split("pds_endpoint: '")[1].split("',")[0];
                AliyunConfig.svEndpoint = element.html().split("sv_endpoint: '")[1].split("',")[0];
            }
        }
    }

    @Override
    public R login(NetDiskClient netDiskClient) {
        netDiskClient.setNetDiskName(AliyunConfig.name);
        netDiskClient.setIcon(AliyunConfig.icon);
        if (AliyunConstants.LOGIN_TYPE_REFRESH_TOKEN.equals(netDiskClient.getLoginEntityVO().getLoginType())) {
            return refreshLogin(netDiskClient);
        }
        return R.ok();
    }

    @Override
    public Boolean loginState() {
        return null;
    }

    @Override
    public void reFreshToken(NetDiskClient netDiskClient) {
        refreshLogin(netDiskClient);
    }

    private String passwordEncrypt(String password) {
//        String html = HttpUtil.get(aliyunConfig.getMiniLoginUrl());
//        Elements scripts = Jsoup.parse(html).getElementsByTag("script");
        return "1";
    }

    /**
    * @Author xkx
    * @Description refreshToken方式登录
    * @Date 2022-07-30 21:53
    * @Param [netDiskClient]
    * @return com.chick.net.disk.driver.base.R
    **/
    private R refreshLogin(NetDiskClient netDiskClient) {
        // 通过refreshToken获取refreshToken和accessToken 以及其他信息
        com.alibaba.fastjson2.JSONObject jsonObject = new com.alibaba.fastjson2.JSONObject();
        jsonObject.put("refresh_token", netDiskClient.getLoginEntityVO().getRefreshToken());
        String body = HttpUtil.createPost(AliyunConfig.refreshTokenUrl).body(jsonObject.toJSONString()).execute().body();
        if (StringUtils.isEmpty(body)){
            return R.failed("系统错误，登录返回信息为空");
        }
        JSONObject jsonObjectGet = JSONObject.parseObject(body);
        if (AliyunConstants.INVALID_TOKEN_ERROR.equals(jsonObjectGet.getString("code"))){
            return R.failed("refreshToken无效，请更换新的refreshToken");
        }
        // 设置access_token
        if (StringUtils.isEmpty(jsonObjectGet.getString("access_token"))){
            return R.failed("获取access_token失败，请检查网络后重试");
        }
        netDiskClient.getLoginEntityVO().setAccessToken(jsonObjectGet.getString("access_token"));
        // 设置refresh_token
        if (StringUtils.isEmpty(jsonObjectGet.getString("refresh_token"))){
            return R.failed("获取新的refresh_token失败，请检查网络后重试");
        }
        netDiskClient.getLoginEntityVO().setRefreshToken(jsonObjectGet.getString("refresh_token"));

        // 设置driver_id
        if (StringUtils.isEmpty(jsonObjectGet.getString("default_drive_id"))){
            return R.failed("获取新的device_id失败，请检查网络后重试");
        }
        netDiskClient.setDriverId(jsonObjectGet.getString("default_drive_id"));
        // 设置user_id
        if (StringUtils.isEmpty(jsonObjectGet.getString("user_id")) || StringUtils.isEmpty(jsonObjectGet.getString("nick_name"))){
            return R.failed("获取用户失败，请检查网络后重试");
        }
        netDiskClient.setUserId(jsonObjectGet.getString("user_id"));
        netDiskClient.setUserName(jsonObjectGet.getString("nick_name"));
        // 获取用户头像手机号
        getUserInfo(netDiskClient);
        // 获取硬盘容量
        getDriveCapacityDetails(netDiskClient);
        return netDiskDriverPool.addClient(netDiskClient);
    }

    // 获取用户头像手机号
    void getUserInfo(NetDiskClient netDiskClient){
        // 获取用户头像、手机号
        String userInfoStr = HttpUtil.createPost(AliyunConfig.getUserUrl).body(new JSONObject().toJSONString()).header("authorization", "Bearer " + netDiskClient.getLoginEntityVO().getAccessToken()).execute().body();
        if (StringUtils.isEmpty(userInfoStr)){
            log.error("获取用户信息失败");
        }
        JSONObject userInfoJson = JSONObject.parseObject(userInfoStr);
        // 设置用户头像
        if (StringUtils.isEmpty(userInfoJson.getString("phone")) || StringUtils.isEmpty(userInfoJson.getString("avatar"))){
            log.error("获取用户头像和手机号失败");
        } else {
            netDiskClient.setAvatar(userInfoJson.getString("avatar"));
            netDiskClient.setPhone(userInfoJson.getString("phone"));
        }
    }

    // 获取硬盘容量
    void getDriveCapacityDetails(NetDiskClient netDiskClient){
        // 获取硬盘容量
        String diskInfo = HttpUtil.createPost(AliyunConfig.getDriveCapacityDetailsUrl).body(new JSONObject().toJSONString()).header("authorization", "Bearer " + netDiskClient.getLoginEntityVO().getAccessToken()).execute().body();
        if (StringUtils.isEmpty(diskInfo)){
            log.error("获取硬盘容量");
        }
        JSONObject diskInfoJson = JSONObject.parseObject(diskInfo);
        // 设置用户头像
        if (ObjectUtils.isEmpty(diskInfoJson.getLong("drive_total_size")) || ObjectUtils.isEmpty(diskInfoJson.getLong("drive_used_size"))){
            log.error("获取硬盘总容量和部分容量");
        } else {
            netDiskClient.setCapacityTotal(diskInfoJson.getLong("drive_total_size"));
            netDiskClient.setCapacityUsed(diskInfoJson.getLong("drive_used_size"));
        }
    }
}

