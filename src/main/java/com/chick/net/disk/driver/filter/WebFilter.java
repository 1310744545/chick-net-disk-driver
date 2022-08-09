package com.chick.net.disk.driver.filter;

import cn.hutool.core.util.ObjectUtil;
import com.chick.net.disk.driver.service.DiskService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @ClassName WebFilter
 * @Author xiaokexin
 * @Date 2022-07-28 20:44
 * @Description WebFilter
 * @Version 1.0
 */
@Component
public class WebFilter extends OncePerRequestFilter {

    @Resource
    private DiskService diskService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String servletPath = request.getServletPath();
        // 未登录的访问system开头的直接跳转登录 （登录放开）
        if (servletPath.startsWith("/system") && !servletPath.contains("/system/login") && ObjectUtil.isEmpty(session.getAttribute("loginFlag"))) {
            // 后台操作
            response.sendRedirect("/system/login");
            return;
        } else {
            // 系统操作，注：网盘基础路径不能设置为system @todo 网盘基础路径不能设置为system
            if (servletPath.startsWith("/system")) {
                filterChain.doFilter(request, response);
                return;
            }
            // 网盘文件增删改操作
            String method = request.getMethod();
            switch (method) {
                case "GET": // 网盘文件查询、下载
                    diskService.get(request, response);
                    return;
                case "POST": // 网盘文件修改
                    diskService.post(request, response);
                    return;
                case "PUT": // 网盘文件添加
                    diskService.put(request, response);
                    return;
                case "DELETE": // 网盘文件删除
                    diskService.delete(request, response);
                    return;
                default:
                    response.sendError(500, "请求方法错误");
                    return;
            }
        }
    }
}
