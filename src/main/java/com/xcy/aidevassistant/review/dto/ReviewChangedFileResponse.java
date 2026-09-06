package com.xcy.aidevassistant.review.dto;

public class ReviewChangedFileResponse {

    private Long id;
    private String filePath;
    private String oldPath;
    private String newPath;
    private String changeType;
    private String diffStatus;
    private Integer additions;
    private Integer deletions;
    private String diffSnapshot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getDiffStatus() {
        return diffStatus;
    }

    public void setDiffStatus(String diffStatus) {
        this.diffStatus = diffStatus;
    }

    public Integer getAdditions() {
        return additions;
    }

    public void setAdditions(Integer additions) {
        this.additions = additions;
    }

    public Integer getDeletions() {
        return deletions;
    }

    public void setDeletions(Integer deletions) {
        this.deletions = deletions;
    }

    public String getDiffSnapshot() {
        return diffSnapshot;
    }

    public void setDiffSnapshot(String diffSnapshot) {
        this.diffSnapshot = diffSnapshot;
    }
}
