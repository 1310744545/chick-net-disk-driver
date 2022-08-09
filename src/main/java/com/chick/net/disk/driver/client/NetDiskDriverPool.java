package com.chick.net.disk.driver.client;

import com.chick.net.disk.driver.base.R;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName NetDiskDriver
 * @Author xiaokexin
 * @Date 2022-07-28 10:26
 * @Description 网盘客户端
 * @Version 1.0
 */
@Component
@Data
public class NetDiskDriverPool {

    private static Map<String, NetDiskClient> driverPool = new ConcurrentHashMap<>();

    public List<NetDiskClient> getDriverPoolContent(){
        Collection<NetDiskClient> values = driverPool.values();
        List<NetDiskClient> netDiskClients = new ArrayList<>();
        netDiskClients.addAll(values);
        return netDiskClients;
    }

    public Map<String, NetDiskClient> getDriverPool(){
        return driverPool;
    }

    public R addClient(NetDiskClient netDiskClient){
        List<NetDiskClient> driverPoolContent = getDriverPoolContent();
        for (NetDiskClient netDiskClientInPool : driverPoolContent){
            if (netDiskClientInPool.getUserId().equals(netDiskClient.getUserId())){
                return R.failed("重复登录");
            }
        }
        driverPool.put(netDiskClient.getPath(), netDiskClient);
        return R.ok("登录成功");
    }

    public R removeClient(String path){
        getDriverPool().remove(path);
        return R.ok("删除成功");
    }
}
