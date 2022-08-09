package com.chick.net.disk.driver.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName SystemUser
 * @Author xiaokexin
 * @Date 2022-07-28 21:13
 * @Description SystemUser
 * @Version 1.0
 */
@Component
@Data
public class SystemUserVO {
    @Value("${system.username}")
    private String username;
    @Value("${system.password}")
    private String password;
}
