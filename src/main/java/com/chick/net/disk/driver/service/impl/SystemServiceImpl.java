package com.chick.net.disk.driver.service.impl;

import cn.hutool.core.util.ClassUtil;
import com.chick.net.disk.driver.base.R;
import com.chick.net.disk.driver.cache.AliyunCache;
import com.chick.net.disk.driver.client.NetDiskClient;
import com.chick.net.disk.driver.client.NetDiskDriverPool;
import com.chick.net.disk.driver.config.NetDiskConfig;
import com.chick.net.disk.driver.entity.NetDiskBaseInfo;
import com.chick.net.disk.driver.entity.PathEditVO;
import com.chick.net.disk.driver.entity.SystemUserVO;
import com.chick.net.disk.driver.entity.aliyun.AliyunCacheFolder;
import com.chick.net.disk.driver.entity.system.LoginEntityVO;
import com.chick.net.disk.driver.factory.NetDiskClientFactory;
import com.chick.net.disk.driver.service.SystemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName SystemService
 * @Author xiaokexin
 * @Date 2022-07-29 11:21
 * @Description SystemService
 * @Version 1.0
 */
@Service
@Log4j2
public class SystemServiceImpl implements SystemService {
    @Resource
    private SystemUserVO systemUser;
    @Resource
    private NetDiskClientFactory netDiskClientFactory;

    @Resource
    private NetDiskDriverPool netDiskDriverPool;
    @Resource
    private AliyunCache aliyunCache;

    @Override
    public R login(String username, String password, HttpServletRequest request) {
        if (username.equals(systemUser.getUsername()) && password.equals(systemUser.getPassword())) {
            HttpSession session = request.getSession();
            session.setAttribute("loginFlag", true);
            return R.ok("登录成功");
        }
        return R.failed("用户名密码错误");
    }

    @Override
    public R diskList() {
        Set<Class<?>> instances = getInstances2(NetDiskConfig.class);
        ArrayList<NetDiskBaseInfo> baseInfos = new ArrayList<>();
        for (Class clazz : instances) {
            NetDiskBaseInfo netDiskBaseInfo = new NetDiskBaseInfo();
            try {
                netDiskBaseInfo.setName(clazz.getField("name").get(null).toString());
                netDiskBaseInfo.setIcon(clazz.getField("icon").get(null).toString());
            } catch (Exception e) {
                log.error("错误--> 请检查网盘实现类是否有name和icon属性");
                return R.failed("错误 请查看日志");
            }
            baseInfos.add(netDiskBaseInfo);
        }
        return R.ok(baseInfos);
    }

    @Override
    public R registerNetDisk() {
        return R.ok(netDiskDriverPool.getDriverPoolContent());
    }

    @Override
    public R loginTypes(String netDiskName) {
        Set<Class<?>> instances = getInstances2(NetDiskConfig.class);
        for (Class clazz : instances) {
            try {
                if (netDiskName.equals(clazz.getField("name").get(null).toString())) {
                    return R.ok((List) clazz.getField("loginTypes").get(ArrayList.class));
                }
            } catch (Exception e) {
                log.error("错误--> 请检查网盘实现类是否有name和icon属性");
                return R.failed("错误 请查看日志");
            }
        }
        return R.failed("系统错误");
    }

    @Override
    public R netDiskRegister(LoginEntityVO loginEntityVO) {
        NetDiskClient netDiskClient = null;
        Set<Class<?>> instances = getInstances2(NetDiskConfig.class);
        for (Class clazz : instances) {
            try {
                if (loginEntityVO.getName().equals(clazz.getField("name").get(null).toString())) {
                    NetDiskConfig netDiskConfig = (NetDiskConfig) clazz.newInstance();
                    netDiskConfig.setName(loginEntityVO.getName());
                    netDiskClient = netDiskClientFactory.getMessageEvent(netDiskConfig);
                    netDiskClient.setLoginEntityVO(loginEntityVO);
                }
            } catch (Exception e) {
                log.error("错误--> 请检查网盘实现类是否有name和icon属性");
                return R.failed("错误 请查看日志");
            }
        }
        if (ObjectUtils.isEmpty(netDiskClient)) {
            return R.failed("创建网盘客户端失败");
        }
        // 网盘登录
        return netDiskClient.getNetDiskEvent().login(netDiskClient);
    }

    @Override
    public R submitEditPath(PathEditVO pathEditVO) {
        Map<String, NetDiskClient> driverPool = netDiskDriverPool.getDriverPool();
        // 检查是否重复
        Set<String> keys = driverPool.keySet();
        for (String key : keys){
            if (key.equals(pathEditVO.getEditPathStr())){
                return R.failed("路径已存在，请更换");
            }
        }
        NetDiskClient netDiskClient = driverPool.get(pathEditVO.getEditPathOriginalStr());
        netDiskClient.setPath(pathEditVO.getEditPathStr());
        driverPool.put(pathEditVO.getEditPathStr(), netDiskClient);
        netDiskDriverPool.removeClient(pathEditVO.getEditPathOriginalStr());
        // 清空缓存
        aliyunCache.cleanPool();
        return R.ok("修改成功");
    }

    @Override
    public R deletePath(String path) {
        NetDiskClient netDiskClient = netDiskDriverPool.getDriverPool().get(path);
        if (ObjectUtils.isEmpty(netDiskClient)){
            return R.failed("删除失败");
        }
        return netDiskDriverPool.removeClient(path);
    }

    private static Set<Class<?>> getInstances(Class supClass) {
        String aPackage = ClassUtil.getPackage(supClass);
        Set<Class<?>> classes = ClassUtil.scanPackage(aPackage);
        Set<Class<?>> collect = classes.stream().filter(sonClass -> {
            boolean allAssignableFrom = ClassUtil.isAllAssignableFrom(new Class[]{supClass},
                    new Class[]{sonClass});
            //要将 本身排除
            return allAssignableFrom && sonClass != supClass;
        }).collect(Collectors.toSet());
        return collect;
    }

    private static Set<Class<?>> getInstances2(Class supClass) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(supClass));
        // scan in org.example.package
        System.out.println(NetDiskConfig.class.getPackage().toString().replaceAll("\\.", "/"));
        Set<BeanDefinition> components = provider.findCandidateComponents(supClass.getPackage().toString().replaceAll("\\.", "/").replace("package ", ""));
        Set<Class<?>> collect = new HashSet<>();
        for (BeanDefinition component : components) {
            try {
                if (!Class.forName(component.getBeanClassName()).equals(supClass)) {
                    collect.add(Class.forName(component.getBeanClassName()));
                }
            } catch (ClassNotFoundException e) {
                log.error("类没有找到");
            }
        }
        return collect;
    }
}
