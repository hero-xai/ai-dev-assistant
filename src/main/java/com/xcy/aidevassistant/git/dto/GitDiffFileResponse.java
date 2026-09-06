package com.xcy.aidevassistant.git.dto;

public class GitDiffFileResponse extends GitFileChangeResponse {

    private String diffContent;
    private boolean truncated;

    public String getDiffContent() {
        return diffContent;
    }

    public void setDiffContent(String diffContent) {
        this.diffContent = diffContent;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }
}
