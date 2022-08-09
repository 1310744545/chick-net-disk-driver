package com.chick.net.disk.driver.controller;

import com.chick.net.disk.driver.base.R;
import com.chick.net.disk.driver.entity.PathEditVO;
import com.chick.net.disk.driver.entity.system.LoginEntityVO;
import com.chick.net.disk.driver.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName SystemController
 * @Author xiaokexin
 * @Date 2022-07-28 21:15
 * @Description SystemController
 * @Version 1.0
 */
@Controller
public class SystemController {

    @Resource
    private SystemService systemService;

    @GetMapping("/system/login")
    public String login() {
        return "login";
    }

    @GetMapping("/system/index")
    public String index() {
        return "index";
    }

    @PostMapping("/system/login")
    @ResponseBody
    public R login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return R.failed("用户名或密码为空");
        }
        return systemService.login(username, password, request);
    }

    @PostMapping("/system/diskList")
    @ResponseBody
    public R diskList() {
        return systemService.diskList();
    }

    @PostMapping("/system/registerNetDisk")
    @ResponseBody
    public R loginEdDiskList() {
        return systemService.registerNetDisk();
    }

    @PostMapping("/system/loginTypes")
    @ResponseBody
    public R loginTypes(String netDiskName) {
        if (StringUtils.isEmpty(netDiskName)){
            return R.failed("网盘名为空");
        }
        return systemService.loginTypes(netDiskName);
    }

    @PostMapping("/system/netDiskRegister")
    @ResponseBody
    public R netDiskRegister(LoginEntityVO loginEntityVO) {
        if (ObjectUtils.isEmpty(loginEntityVO)){
            return R.failed("网盘名为空");
        }
        return systemService.netDiskRegister(loginEntityVO);
    }

    @PostMapping("/system/submitEditPath")
    @ResponseBody
    public R submitEditPath(PathEditVO pathEditVO) {
        if (ObjectUtils.isEmpty(pathEditVO)){
            return R.failed("网盘名为空");
        }
        if (StringUtils.isEmpty(pathEditVO.getEditPathStr()) || StringUtils.isEmpty(pathEditVO.getEditPathOriginalStr())){
            return R.failed("路径不可为空");
        }
        if ("system".equals(pathEditVO.getEditPathStr())){
            return R.failed("基础路经不可为system");
        }
        if (pathEditVO.getEditPathStr().contains("/")){
            return R.failed("基础路经不可包含 / ");
        }
        return systemService.submitEditPath(pathEditVO);
    }

    @PostMapping("/system/deletePath")
    @ResponseBody
    public R deletePath(String path) {
        if (StringUtils.isEmpty(path)){
            return R.failed("路径不可为空");
        }
        return systemService.deletePath(path);
    }
}
