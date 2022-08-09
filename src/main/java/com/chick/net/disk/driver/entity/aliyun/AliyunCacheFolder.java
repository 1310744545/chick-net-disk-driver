package com.chick.net.disk.driver.entity.aliyun;

import com.chick.net.disk.driver.entity.aliyun.response.AliyunFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName AliyunCacheFolder
 * @Author xiaokexin
 * @Date 2022-08-05 17:35
 * @Description AliyunCacheFolder
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class AliyunCacheFolder {
    private String createdAt;
    private String driveId;
    private String updatedAt;
    private String userMeta;
    private String starred;
    private String parentFileId;
    private String fileId;
    private String name;
    private String type;
    private String path;
    private String size;
    private String fileExtension;
    private String fileType; // 文件类型，child、parent
    private List<AliyunCacheFolder> aliyunCacheFolders;

    public AliyunCacheFolder(AliyunFile aliyunFile, String path) {
        this.createdAt = aliyunFile.getCreated_at();
        this.driveId = aliyunFile.getDrive_id();
        this.updatedAt = aliyunFile.getUpdated_at();
        this.userMeta = aliyunFile.getUser_meta();
        this.starred = aliyunFile.getStarred();
        this.parentFileId = aliyunFile.getParent_file_id();
        this.fileId = aliyunFile.getFile_id();
        this.type = aliyunFile.getType();
        this.name = "folder".equals(aliyunFile.getType()) ? aliyunFile.getName() + "/" : aliyunFile.getName();
        this.fileType = aliyunFile.getType();
        this.size = StringUtils.isEmpty(aliyunFile.getSize()) ? "/" : aliyunFile.getSize();
        this.fileExtension = aliyunFile.getFile_extension();
        this.path = path + "/" + ("folder".equals(aliyunFile.getType()) ? aliyunFile.getName() + "/" : aliyunFile.getName());
    }
}
