package com.xcy.aidevassistant.git.dto;

import java.util.List;

public class GitCommitDetailResponse extends GitCommitResponse {

    private String fullMessage;
    private List<GitFileChangeResponse> changedFiles;

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public List<GitFileChangeResponse> getChangedFiles() {
        return changedFiles;
    }

    public void setChangedFiles(List<GitFileChangeResponse> changedFiles) {
        this.changedFiles = changedFiles;
    }
}
