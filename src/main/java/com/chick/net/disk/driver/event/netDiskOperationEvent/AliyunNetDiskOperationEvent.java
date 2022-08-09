package com.chick.net.disk.driver.event.netDiskOperationEvent;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.chick.net.disk.driver.cache.AliyunCache;
import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.client.NetDiskDriverPool;
import com.chick.net.disk.driver.config.AliyunConfig;
import com.chick.net.disk.driver.entity.NetDiskFile;
import com.chick.net.disk.driver.entity.aliyun.AliyunCacheFolder;
import com.chick.net.disk.driver.entity.aliyun.request.DownloadParam;
import com.chick.net.disk.driver.entity.aliyun.request.FileListParam;
import com.chick.net.disk.driver.entity.aliyun.response.AliyunFile;
import com.chick.net.disk.driver.event.NetDiskOperationEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @ClassName AliyunNetDiskOperationEvent
 * @Author xiaokexin
 * @Date 2022-07-30 15:00
 * @Description AliyunNetDiskOperationEvent
 * @Version 1.0
 */
@Service
@Log4j2
public class AliyunNetDiskOperationEvent implements NetDiskOperationEvent {

    @Resource
    private AliyunCache aliyunCache;
    @Resource
    private NetDiskDriverPool netDiskDriverPool;

    @Override
    public List<NetDiskFile> getFileList(List<String> paths, String netDiskClientPath, HttpServletResponse resp) {
        NetDiskClient netDiskClient = netDiskDriverPool.getDriverPool().get(netDiskClientPath);
        if (ObjectUtils.isEmpty(netDiskClient)) {
            log.error("从网盘客户端pool中获取网盘客户端失败");
            return new ArrayList<>();
        }
        // 从缓存中获取
        Map<String, AliyunCacheFolder> aliyunCacheFoldersMap = aliyunCache.getPool(netDiskClient.getDriverId());
        Collection<AliyunCacheFolder> aliyunCacheFolders = new ArrayList<>();
        if (!ObjectUtils.isEmpty(aliyunCacheFoldersMap)) {
            aliyunCacheFolders = aliyunCacheFoldersMap.values();
        }
        if (ObjectUtils.isEmpty(aliyunCacheFolders) || aliyunCacheFolders.size() == 0) {
            // 第一次请求
            aliyunCacheFoldersMap = aliyunCache.addPool(netDiskClient.getDriverId());
            List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, "root", "", new ArrayList<>());
            for (AliyunFile aliyunFile : items) {
                aliyunCacheFoldersMap.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + netDiskClientPath));
            }
        }
        // 如果是根路径
        if (paths.size() == 0) {
            return cacheFilesToNetDiskFiles(aliyunCacheFoldersMap.values());
        }
        // 先从缓存中获取每个文件夹，没有的去阿里云请求
        AliyunCacheFolder aliyunCacheFolderNow = null;
        Map<String, AliyunCacheFolder> aliyunCacheFoldersMapNow = new HashMap<>();
        aliyunCacheFoldersMapNow.putAll(aliyunCacheFoldersMap);
        String pathNow = "/" + netDiskClientPath;
        for (String path : paths) {
            pathNow = pathNow + "/" + path;
            aliyunCacheFolderNow = aliyunCacheFoldersMapNow.get(path);
            if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                // 进行下载操作
                netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                return null;
            }
            if (paths.indexOf(path) == 0) {
                // 只有一级目录时
                AliyunCacheFolder aliyunCacheFolder = aliyunCacheFoldersMapNow.get(path);
                if (ObjectUtils.isEmpty(aliyunCacheFolder)) {
                    List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, "root", "", new ArrayList<>());
                    for (AliyunFile aliyunFile : items) {
                        aliyunCacheFoldersMap.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                        aliyunCacheFoldersMapNow.putAll(aliyunCacheFoldersMap);
                    }
                }
                aliyunCacheFolder = aliyunCacheFoldersMapNow.get(path);
                if (ObjectUtils.isEmpty(aliyunCacheFolder)) {
                    // 找不到路径
                    log.error("找不到请求路径");
                    return new ArrayList<>();
                }
                aliyunCacheFolderNow = aliyunCacheFolder;
                if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                    // 进行下载操作
                    netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                    return null;
                }
                List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, aliyunCacheFolderNow.getFileId(), "", new ArrayList<>());
                if (paths.indexOf(path) != paths.size() - 1) {
                    aliyunCacheFoldersMapNow.clear();
                    for (AliyunFile aliyunFile : items) {
                        aliyunCacheFoldersMapNow.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                    }
                    continue;
                }
                if (paths.indexOf(path) == paths.size() - 1) {
                    return aliyunFilesToNetDiskFiles(items, "/" + netDiskClientPath, pathNow);
                }
            } else {
                // 有一级以上的路径时
                aliyunCacheFolderNow = aliyunCacheFoldersMapNow.get(path);
                if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                    // 进行下载操作
                    netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                    return null;
                }
                List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, aliyunCacheFolderNow.getFileId(), "", new ArrayList<>());
                if (paths.indexOf(path) != paths.size() - 1) {
                    aliyunCacheFoldersMapNow.clear();
                    for (AliyunFile aliyunFile : items) {
                        aliyunCacheFoldersMapNow.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                    }
                    continue;
                }
                if (paths.indexOf(path) == paths.size() - 1) {
                    StringJoiner stringJoiner = new StringJoiner("/", "/", "");
                    stringJoiner.add(netDiskClientPath);
                    for (String pathParent : paths) {
                        if (!path.equals(pathParent)){
                            stringJoiner.add(pathParent);
                        }
                    }
                    return aliyunFilesToNetDiskFiles(items, stringJoiner.toString(), pathNow);
                }
            }
        }
        return null;
    }

    @Override
    public void download(String filePath, NetDiskClient netDiskClient, HttpServletResponse resp) {
        String referer = HttpUtil.createGet(AliyunConfig.downloadFileUrl).body(JSONObject.toJSONString(new DownloadParam(netDiskClient.getDriverId(), filePath))).header("authorization", "Bearer " + netDiskClient.getLoginEntityVO().getAccessToken()).execute().body();
        JSONObject jsonObject1 = JSONObject.parseObject(referer);
        InputStream inputStream = HttpUtil.createGet(jsonObject1.getString("url")).header("referer", AliyunConfig.rootUrl).execute().bodyStream();
        try {
            ServletOutputStream outputStream = resp.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("下载出错" + e.getMessage());
        }
    }

    @Override
    public boolean checkReturnInfo(String returnStr, String netDiskClientPath) {
        // 检查token是否过期
        NetDiskClient netDiskClient = netDiskDriverPool.getDriverPool().get(netDiskClientPath);
        JSONObject returnJson = JSONObject.parseObject(returnStr);
        String code = returnJson.getString("code");
        if (!StringUtils.isEmpty(code) && "AccessTokenInvalid".equals(code)) {
            netDiskClient.getNetDiskEvent().reFreshToken(netDiskClient);
            return false;
        }
        return true;
    }

    private List<NetDiskFile> cacheFilesToNetDiskFiles(Collection<AliyunCacheFolder> aliyunCacheFolders) {
        List<NetDiskFile> netDiskFiles = new ArrayList<>();
        for (AliyunCacheFolder aliyunCacheFolder : aliyunCacheFolders) {
            netDiskFiles.add(new NetDiskFile(aliyunCacheFolder));
        }
        return netDiskFiles;
    }

    private List<NetDiskFile> aliyunFilesToNetDiskFiles(Collection<AliyunFile> aliyunFiles, String parentPath, String path) {
        List<NetDiskFile> netDiskFiles = new ArrayList<>();
        netDiskFiles.add(new NetDiskFile(parentPath));
        for (AliyunFile aliyunFile : aliyunFiles) {
            netDiskFiles.add(new NetDiskFile(aliyunFile, path));
        }
        return netDiskFiles;
    }

    private List<AliyunFile> httpGetFileList(NetDiskClient netDiskClient, String url, String fileId, String nextMarkerParam, List<AliyunFile> aliyunFiles) {
        long start = System.currentTimeMillis();
        String returnStr = HttpUtil.createGet(url).body(JSONObject.toJSONString(new FileListParam(netDiskClient.getDriverId(), fileId, nextMarkerParam))).header("authorization", "Bearer " + netDiskClient.getLoginEntityVO().getAccessToken()).execute().body();
        long end = System.currentTimeMillis();
        log.info("请求花费" + (end - start) + "秒");
        // 每次请求后都要检查返回值的有效性，如果无效进行相应的处理，并重新调用
        if (!netDiskClient.getNetDiskOperationEvent().checkReturnInfo(returnStr, netDiskClient.getPath())) {
            return httpGetFileList(netDiskClient, url, fileId, "", aliyunFiles);
        }
        String nextMarker = JSONObject.parseObject(returnStr).getString("next_marker");
        if (!StringUtils.isEmpty(nextMarker)) {
            aliyunFiles.addAll(JSONObject.parseArray(JSONObject.parseObject(returnStr).getString("items"), AliyunFile.class));
            httpGetFileList(netDiskClient, url, fileId, nextMarker, aliyunFiles);
        } else {
            aliyunFiles.addAll(JSONObject.parseArray(JSONObject.parseObject(returnStr).getString("items"), AliyunFile.class));
        }
        return aliyunFiles;
    }

}
