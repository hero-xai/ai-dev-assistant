package com.xcy.aidevassistant.git.controller;

import com.xcy.aidevassistant.common.api.ApiResponse;
import com.xcy.aidevassistant.git.application.GitCommitApplicationService;
import com.xcy.aidevassistant.git.dto.GitCommitDetailResponse;
import com.xcy.aidevassistant.git.dto.GitCommitDiffResponse;
import com.xcy.aidevassistant.git.dto.GitCommitResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/git")
public class GitCommitController {

    private final GitCommitApplicationService gitCommitApplicationService;

    public GitCommitController(GitCommitApplicationService gitCommitApplicationService) {
        this.gitCommitApplicationService = gitCommitApplicationService;
    }

    @GetMapping("/commits")
    public ApiResponse<List<GitCommitResponse>> listCommits(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(gitCommitApplicationService.listCommits(projectId, limit));
    }

    @GetMapping("/commits/{commitId}")
    public ApiResponse<GitCommitDetailResponse> getCommitDetail(
            @PathVariable Long projectId,
            @PathVariable String commitId) {
        return ApiResponse.success(gitCommitApplicationService.getCommitDetail(projectId, commitId));
    }

    @GetMapping("/commits/{commitId}/diff")
    public ApiResponse<GitCommitDiffResponse> getCommitDiff(
            @PathVariable Long projectId,
            @PathVariable String commitId) {
        return ApiResponse.success(gitCommitApplicationService.getCommitDiff(projectId, commitId));
    }
}
