package com.chick.net.disk.driver.service;

import com.chick.net.disk.driver.base.R;
import com.chick.net.disk.driver.entity.PathEditVO;
import com.chick.net.disk.driver.entity.system.LoginEntityVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName SystemService
 * @Author xiaokexin
 * @Date 2022-07-29 11:21
 * @Description SystemService
 * @Version 1.0
 */
public interface SystemService {
    R login(String username, String password, HttpServletRequest request);

    R diskList();

    R registerNetDisk();

    R loginTypes(String netDiskName);

    R netDiskRegister(LoginEntityVO loginEntityVO);

    R submitEditPath(PathEditVO pathEditVO);

    R deletePath(String path);
}
