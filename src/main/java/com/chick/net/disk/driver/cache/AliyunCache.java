package com.chick.net.disk.driver.cache;

import com.chick.net.disk.driver.entity.aliyun.AliyunCacheFolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AliyunCache
 * @Author xiaokexin
 * @Date 2022-08-05 17:34
 * @Description AliyunCache
 * @Version 1.0
 */
@Component
public class AliyunCache {
    // driverID
    private  Map<String, Map<String, AliyunCacheFolder>> pool = new ConcurrentHashMap<>();

    public Map<String, AliyunCacheFolder> getPool(String driverId){
        return pool.get(driverId);
    }

    public Map<String, AliyunCacheFolder> addPool(String driverId){
        pool.put(driverId, new HashMap<>());
        return getPool(driverId);
    }

    public void cleanPool(){
        pool = new ConcurrentHashMap<>();
    }
}
