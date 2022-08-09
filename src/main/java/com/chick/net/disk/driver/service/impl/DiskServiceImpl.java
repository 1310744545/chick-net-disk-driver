package com.chick.net.disk.driver.service.impl;


import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.client.NetDiskDriverPool;
import com.chick.net.disk.driver.entity.NetDiskFile;
import com.chick.net.disk.driver.entity.PathVO;
import com.chick.net.disk.driver.enums.ContentTypeEnum;
import com.chick.net.disk.driver.service.DiskService;
import com.chick.net.disk.driver.utils.HtmlUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @ClassName AccountServiceImpl
 * @Author xiaokexin
 * @Date 2022-07-28 13:38
 * @Description AccountServiceImpl
 * @Version 1.0
 */
@Service
@Log4j2
public class DiskServiceImpl implements DiskService {

    @Resource
    private NetDiskDriverPool netDiskDriverPool;

    @Override
    public void get(HttpServletRequest req, HttpServletResponse resp) {
        String returnStr = "";
        String path = req.getServletPath();
        // 请求路径为空，进新手教程
        if ("/".equals(path)) {
            returnStr = "进入新手教程";
            goReturn(ContentTypeEnum.HTML.getContentType(), resp, null, returnStr);
        }
        // 获取基础路径和网盘路径
        PathVO pathVO = getPathVO(path);
        // 获取网盘客户端池
        Map<String, NetDiskClient> driverPool = netDiskDriverPool.getDriverPool();
        if (driverPool.keySet().size() == 0) {
            returnStr = "没有登录状态的网盘，请先配置网盘";
            goReturn(ContentTypeEnum.HTML.getContentType(), resp, null, returnStr);
            return;
        }
        // 获取网盘客户端
        NetDiskClient netDiskClient = driverPool.get(pathVO.getBasePath());
        if (ObjectUtils.isEmpty(netDiskClient)){
            returnStr = "404 ~ 请确认基础路径是否正确";
            goReturn(ContentTypeEnum.HTML.getContentType(), resp, null, returnStr);
            return;
        }
        // null：下载、不存在
        // 空list：空文件夹
        // list：文件列表
        List<NetDiskFile> fileList = netDiskClient.getNetDiskOperationEvent().getFileList(pathVO.getDriverPath(), pathVO.getBasePath(), resp);
        if (ObjectUtils.isEmpty(fileList)){
            log.info("执行下载");
            return;
        }
        returnStr = HtmlUtil.createTableHtml(fileList);
        goReturn(ContentTypeEnum.HTML.getContentType(), resp, null, returnStr);
        return;
    }

    @Override
    public void post(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void put(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public void delete(HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * @return com.chick.net.disk.driver.entity.PathVO
     * @Author xkx
     * @Description 获取网盘基础路径和网盘路径
     * @Date 2022-08-05 15:01
     * @Param [path]
     **/
    private PathVO getPathVO(String path) {
        String[] paths = path.split("/");
        PathVO pathVO = new PathVO();
        pathVO.setBasePath(paths[1]);
        // 只有基础路径
        if (paths.length == 2) {
            return pathVO;
        }
        // 请求了网盘路径
        for (int i = 2; i < paths.length; i++) {
            pathVO.getDriverPath().add(paths[i]);
        }
        return pathVO;
    }

    private void goReturn(String contentType, HttpServletResponse resp, InputStream is, String html) {
        resp.setHeader("content-type", contentType);
        try {
            switch (Objects.requireNonNull(ContentTypeEnum.getByValue(contentType))) {
                case HTML:
                    // do something
                    break;
                case JSON:
                    System.out.println(2);
                    break;
                default:
                    System.out.println("zxc");
                    break;
            }
            ServletOutputStream os = resp.getOutputStream();
            byte[] dataByteArr = html.getBytes(StandardCharsets.UTF_8);
            os.write(dataByteArr);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("e:{}", e.getMessage());
        }
    }


}
