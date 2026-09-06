package com.xcy.aidevassistant.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewTaskCreateRequest {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "Commit ID不能为空")
    private String commitId;

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
}
