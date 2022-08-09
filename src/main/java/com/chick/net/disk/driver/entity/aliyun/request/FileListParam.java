package com.chick.net.disk.driver.entity.aliyun.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FileListParam
 * @Author xiaokexin
 * @Date 2022-08-06 19:22
 * @Description FileListParam
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class FileListParam {
    private Boolean all;
    private String drive_id;
    private String fields;
    private String image_thumbnail_process;
    private String image_url_process;
    private Integer limit;
    private String order_by;
    private String order_direction;
    private String parent_file_id;
    private Integer url_expire_sec;
    private String video_thumbnail_process;
    private String marker;

    public FileListParam(String drive_id, String parent_file_id, String marker) {
        this.drive_id = drive_id;
        this.parent_file_id = parent_file_id;
        this.all = true;
        this.fields = "*";
        this.image_thumbnail_process = "image/resize,w_400/format,jpeg";
        this.image_url_process = "image/resize,w_1920/format,jpeg";
        this.limit = 200;
        this.order_by = "created_at";
        this.order_direction = "DESC";
        this.url_expire_sec = 1600;
        this.marker = marker;
        this.video_thumbnail_process = "video/snapshot,t_1000,f_jpg,ar_auto,w_300";
    }
}
