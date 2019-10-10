package com.littlec.sdk.database.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Entity mapped to table "Download".
 */
@Entity
public class DownloadDBEntity {
    @Id
    private String downloadId;
    private Long totalSize;
    private Long completedSize;
    private String url;
    private String saveDirPath;
    private String fileName;
    private Integer downloadStatus;
    @Generated(hash = 476560659)
    public DownloadDBEntity(String downloadId, Long totalSize, Long completedSize,
            String url, String saveDirPath, String fileName,
            Integer downloadStatus) {
        this.downloadId = downloadId;
        this.totalSize = totalSize;
        this.completedSize = completedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
    }
    @Generated(hash = 1143139915)
    public DownloadDBEntity() {
    }
    public String getDownloadId() {
        return this.downloadId;
    }
    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
    public Long getTotalSize() {
        return this.totalSize;
    }
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    public Long getCompletedSize() {
        return this.completedSize;
    }
    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSaveDirPath() {
        return this.saveDirPath;
    }
    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Integer getDownloadStatus() {
        return this.downloadStatus;
    }
    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }



}
