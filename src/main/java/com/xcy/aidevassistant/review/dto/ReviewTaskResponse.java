package com.xcy.aidevassistant.review.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewTaskResponse {

    private Long id;
    private Long projectId;
    private String commitId;
    private String commitMessage;
    private String commitAuthor;
    private LocalDateTime commitTime;
    private String status;
    private String riskLevel;
    private String summary;
    private Integer totalFiles;
    private Integer totalAdditions;
    private Integer totalDeletions;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<ReviewChangedFileResponse> changedFiles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getCommitAuthor() {
        return commitAuthor;
    }

    public void setCommitAuthor(String commitAuthor) {
        this.commitAuthor = commitAuthor;
    }

    public LocalDateTime getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(LocalDateTime commitTime) {
        this.commitTime = commitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(Integer totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Integer getTotalAdditions() {
        return totalAdditions;
    }

    public void setTotalAdditions(Integer totalAdditions) {
        this.totalAdditions = totalAdditions;
    }

    public Integer getTotalDeletions() {
        return totalDeletions;
    }

    public void setTotalDeletions(Integer totalDeletions) {
        this.totalDeletions = totalDeletions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<ReviewChangedFileResponse> getChangedFiles() {
        return changedFiles;
    }

    public void setChangedFiles(List<ReviewChangedFileResponse> changedFiles) {
        this.changedFiles = changedFiles;
    }
}
