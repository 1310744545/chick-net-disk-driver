package com.chick.net.disk.driver.entity;

import com.chick.net.disk.driver.entity.aliyun.AliyunCacheFolder;
import com.chick.net.disk.driver.entity.aliyun.response.AliyunFile;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName NetDiskFile
 * @Author xiaokexin
 * @Date 2022-08-05 16:47
 * @Description NetDiskFile
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class NetDiskFile {
    private String createdAt;
    private String driveId;
    private String updatedAt;
    private String userMeta;
    private String starred;
    private String parentFileId;
    private String fileId;
    private String name;
    private String type;
    private String fileType; // 文件类型，child、parent
    private String size;
    private String fileExtension;
    private String path; // 文件类型，child、parent

    public NetDiskFile(AliyunCacheFolder aliyunCacheFolder) {
        this.createdAt = aliyunCacheFolder.getCreatedAt();
        this.driveId = aliyunCacheFolder.getDriveId();
        this.updatedAt = aliyunCacheFolder.getUpdatedAt();
        this.userMeta = aliyunCacheFolder.getUserMeta();
        this.starred = aliyunCacheFolder.getStarred();
        this.parentFileId = aliyunCacheFolder.getParentFileId();
        this.fileId = aliyunCacheFolder.getFileId();
        this.name = aliyunCacheFolder.getName();
        this.type = aliyunCacheFolder.getType();
        this.fileType = aliyunCacheFolder.getFileType();
        this.path = aliyunCacheFolder.getPath();
        this.size = aliyunCacheFolder.getSize();
        this.fileExtension = aliyunCacheFolder.getFileExtension();
    }

    public NetDiskFile(AliyunFile aliyunFile, String pathNow) {
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
        this.path = pathNow + "/" + ("folder".equals(aliyunFile.getType()) ? aliyunFile.getName() + "/" : aliyunFile.getName());
    }

    public NetDiskFile(String path) {
        this.updatedAt = "";
        this.name = "parent";
        this.fileType = "folder";
        this.size = "";
        this.fileExtension = "";
        this.path = path;
    }
}
