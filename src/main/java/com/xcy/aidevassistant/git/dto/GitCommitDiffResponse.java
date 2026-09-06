package com.xcy.aidevassistant.git.dto;

import java.util.List;

public class GitCommitDiffResponse extends GitCommitResponse {

    private int totalFiles;
    private int totalAdditions;
    private int totalDeletions;
    private List<GitDiffFileResponse> files;

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalAdditions() {
        return totalAdditions;
    }

    public void setTotalAdditions(int totalAdditions) {
        this.totalAdditions = totalAdditions;
    }

    public int getTotalDeletions() {
        return totalDeletions;
    }

    public void setTotalDeletions(int totalDeletions) {
        this.totalDeletions = totalDeletions;
    }

    public List<GitDiffFileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<GitDiffFileResponse> files) {
        this.files = files;
    }
}
