package com.chick.net.disk.driver.entity.aliyun.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DownloadParam
 * @Author xiaokexin
 * @Date 2022-08-08 15:56
 * @Description DownloadParam
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class DownloadParam {
    private String drive_id;
    private String file_id;

    public DownloadParam(String drive_id, String file_id) {
        this.drive_id = drive_id;
        this.file_id = file_id;
    }
}
