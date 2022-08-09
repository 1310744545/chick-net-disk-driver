package com.chick.net.disk.driver.event;

import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.entity.NetDiskFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName NetDiskOperationEvent
 * @Author xiaokexin
 * @Date 2022-07-28 11:01
 * @Description NetDiskOperationEvent
 * @Version 1.0
 */
public interface NetDiskOperationEvent {
    List<NetDiskFile> getFileList(List<String> paths, String netDiskClientPath, HttpServletResponse resp);

    void download(String filePath, NetDiskClient netDiskClient, HttpServletResponse resp);

    boolean checkReturnInfo(String returnStr, String netDiskClientPath);
}
