package com.chick.net.disk.driver.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AccountService
 * @Author xiaokexin
 * @Date 2022-07-28 13:38
 * @Description AccountService
 * @Version 1.0
 */
public interface DiskService {
    /**
    * @Author xkx
    * @Description 网盘操作获取
    * @Date 2022-08-04 21:34
    * @Param [request, response]
    * @return void
    **/
    void get(HttpServletRequest request, HttpServletResponse response);

    /**
    * @Author xkx
    * @Description 网盘操作修改
    * @Date 2022-08-04 21:35
    * @Param [request, response]
    * @return void
    **/
    void post(HttpServletRequest request, HttpServletResponse response);

    /**
     * @Author xkx
     * @Description 网盘操作新增
     * @Date 2022-08-04 21:35
     * @Param [request, response]
     * @return void
     **/
    void put(HttpServletRequest request, HttpServletResponse response);

    /**
    * @Author xkx
    * @Description 网盘操作删除
    * @Date 2022-08-04 21:35
    * @Param [request, response]
    * @return void
    **/
    void delete(HttpServletRequest request, HttpServletResponse response);
}
