package com.chick.net.disk.driver.entity.aliyun.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AliyunFile
 * @Author xiaokexin
 * @Date 2022-08-06 18:11
 * @Description AliyunFile
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class AliyunFile {
    private String created_at;
    private String drive_id;
    private String updated_at;
    private String user_meta;
    private String starred;
    private String parent_file_id;
    private String file_id;
    private String name;
    private String type;
    private String size;
    private String file_extension;

    public AliyunFile(String updated_at, String name, String size) {
        this.updated_at = updated_at;
        this.name = name;
        this.size = size;
    }
}
