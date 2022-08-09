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
        // 当前文件夹
        Map<String, AliyunCacheFolder> aliyunCacheFoldersMapNow = new HashMap<>();
        aliyunCacheFoldersMapNow.putAll(aliyunCacheFoldersMap);
        // 当前目录
        StringBuilder pathNow = new StringBuilder("/" + netDiskClientPath);
        // 遍历请求的目录
        for (int index = 0; index < paths.size(); index++) {
            //for (String path : paths) {
            // 更改当前目录
            pathNow.append("/").append(paths.get(index));
            // 从当前文件夹中查找当前文件，可能为空、文件夹、file
            aliyunCacheFolderNow = aliyunCacheFoldersMapNow.get(paths.get(index));
            // 这里是root目录，也就是缓存里的，如果文件不为空且为file类型，直接进行下载
            if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                // 进行下载操作
                netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                return null;
            }
            if (index == 0) {
                // 进到这里，说明开始有一个或以上的请求目录，这里要进行查找当前请求目录的子目录
                // 1、从缓存中获取当前请求的一级目录，aliyunCacheFolder为当前目录
                aliyunCacheFolderNow = aliyunCacheFoldersMapNow.get(paths.get(index));
                // 2、如果缓存中没有，说明是新加的，重新请求根目录更新缓存
                if (ObjectUtils.isEmpty(aliyunCacheFolderNow)) {
                    List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, "root", "", new ArrayList<>());
                    for (AliyunFile aliyunFile : items) {
                        // 更新缓存
                        aliyunCacheFoldersMap.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                        // 更新当前文件夹
                        aliyunCacheFoldersMapNow.putAll(aliyunCacheFoldersMap);
                        // 更新当前文件
                        aliyunCacheFolderNow = aliyunCacheFoldersMapNow.get(paths.get(index));
                    }
                }
                // 3、如果还找不到，返回错误
                if (ObjectUtils.isEmpty(aliyunCacheFolderNow)) {
                    // 找不到路径
                    try {
                        resp.sendError(404,"访问的资源不存在");
                    } catch (IOException e) {
                        return null;
                    }
                    return null;
                }
                // 4、再次判断是否是文件，如果是就进行下载，此处是避免前面想要下载时文件并不在缓存中
                if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                    // 进行下载操作
                    netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                    return null;
                }
                // 5、如果是文件夹，则请求夏季目录，获取到items
                List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, aliyunCacheFolderNow.getFileId(), "", new ArrayList<>());
                // 6、如果不是最后一级目录，清空当前文件夹，替换为新请求的文件夹
                if (index != paths.size() - 1) {
                    aliyunCacheFoldersMapNow.clear();
                    for (AliyunFile aliyunFile : items) {
                        aliyunCacheFoldersMapNow.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                    }
                    continue;
                }
                // 7、如果是最后一级目录，则直接返回请求到的文件目录
                if (index == (paths.size() - 1)) {
                    return aliyunFilesToNetDiskFiles(items, "/" + netDiskClientPath, pathNow.toString());
                }
            } else {
                // 进到此处，说明有二级及以上的目录
                // 1、如果当前文件为空，返回错误
                if (ObjectUtils.isEmpty(aliyunCacheFolderNow)) {
                    // 找不到路径
                    try {
                        resp.sendError(404,"访问的资源不存在");
                    } catch (IOException e) {
                        return null;
                    }
                    return null;
                }
                // 2、如果当前文件是file类型的，则下载
                if (!ObjectUtils.isEmpty(aliyunCacheFolderNow) && "file".equals(aliyunCacheFolderNow.getType())) {
                    // 进行下载操作
                    netDiskClient.getNetDiskOperationEvent().download(aliyunCacheFolderNow.getFileId(), netDiskClient, resp);
                    return null;
                }
                // 3、如果当前文件是文件夹，则请求子目录
                List<AliyunFile> items = httpGetFileList(netDiskClient, AliyunConfig.getFileListUrl, aliyunCacheFolderNow.getFileId(), "", new ArrayList<>());
                // 4、如果不是最后一级目录，更新当前文件夹
                if (index != paths.size() - 1) {
                    aliyunCacheFoldersMapNow.clear();
                    for (AliyunFile aliyunFile : items) {
                        aliyunCacheFoldersMapNow.put(aliyunFile.getName(), new AliyunCacheFolder(aliyunFile, "/" + pathNow));
                    }
                    continue;
                }
                // 5、如果是最后一级目录，添加父目录，返回，如果不是，继续循环
                if (index == paths.size() - 1) {
                    StringJoiner stringJoiner = new StringJoiner("/", "/", "");
                    stringJoiner.add(netDiskClientPath);
                    for (int i = 0; i < paths.size(); i++) {
                        if (i != paths.size() - 1) {
                            stringJoiner.add(paths.get(i));
                        }
                    }
                    return aliyunFilesToNetDiskFiles(items, stringJoiner.toString(), pathNow.toString());
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
