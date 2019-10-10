package com.littlec.sdk.database.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entity mapped to table "Upload".
 */
@Entity
public class UploadDBEntity {
    @Id
    private String uploadId;
    private String uuid;
    private String localPath;
    private Integer count;
    private Long completedSize;
    private Long totalSize;
    private Integer uploadStatus;
    @Generated(hash = 120180376)
    public UploadDBEntity(String uploadId, String uuid, String localPath,
            Integer count, Long completedSize, Long totalSize,
            Integer uploadStatus) {
        this.uploadId = uploadId;
        this.uuid = uuid;
        this.localPath = localPath;
        this.count = count;
        this.completedSize = completedSize;
        this.totalSize = totalSize;
        this.uploadStatus = uploadStatus;
    }
    @Generated(hash = 969841808)
    public UploadDBEntity() {
    }
    public String getUploadId() {
        return this.uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getLocalPath() {
        return this.localPath;
    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
    public Integer getCount() {
        return this.count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public Long getCompletedSize() {
        return this.completedSize;
    }
    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }
    public Long getTotalSize() {
        return this.totalSize;
    }
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    public Integer getUploadStatus() {
        return this.uploadStatus;
    }
    public void setUploadStatus(Integer uploadStatus) {
        this.uploadStatus = uploadStatus;
    }


}