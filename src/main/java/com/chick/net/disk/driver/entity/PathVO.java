package com.chick.net.disk.driver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PathVO
 * @Author xiaokexin
 * @Date 2022-08-05 13:54
 * @Description PathVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@ToString
public class PathVO {
    private String basePath;
    private List<String> driverPath;

    public PathVO() {
        this.driverPath = new ArrayList<String>();
    }
}
