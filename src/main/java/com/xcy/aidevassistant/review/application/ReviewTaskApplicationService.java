package com.xcy.aidevassistant.review.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcy.aidevassistant.git.application.GitCommitApplicationService;
import com.xcy.aidevassistant.git.dto.GitCommitDiffResponse;
import com.xcy.aidevassistant.git.dto.GitDiffFileResponse;
import com.xcy.aidevassistant.review.domain.ReviewChangedFile;
import com.xcy.aidevassistant.review.domain.ReviewTask;
import com.xcy.aidevassistant.review.dto.ReviewChangedFileResponse;
import com.xcy.aidevassistant.review.dto.ReviewTaskCreateRequest;
import com.xcy.aidevassistant.review.dto.ReviewTaskResponse;
import com.xcy.aidevassistant.review.mapper.ReviewChangedFileMapper;
import com.xcy.aidevassistant.review.mapper.ReviewTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewTaskApplicationService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String DIFF_STATUS_FULL = "FULL";
    private static final String DIFF_STATUS_TRUNCATED = "TRUNCATED";

    private final ReviewTaskMapper reviewTaskMapper;
    private final ReviewChangedFileMapper changedFileMapper;
    private final GitCommitApplicationService gitCommitApplicationService;

    public ReviewTaskApplicationService(ReviewTaskMapper reviewTaskMapper,
                                        ReviewChangedFileMapper changedFileMapper,
                                        GitCommitApplicationService gitCommitApplicationService) {
        this.reviewTaskMapper = reviewTaskMapper;
        this.changedFileMapper = changedFileMapper;
        this.gitCommitApplicationService = gitCommitApplicationService;
    }

    @Transactional
    public ReviewTaskResponse create(ReviewTaskCreateRequest request) {
        GitCommitDiffResponse commitDiff = gitCommitApplicationService.getCommitDiff(request.getProjectId(), request.getCommitId());
        LocalDateTime now = LocalDateTime.now();

        ReviewTask task = new ReviewTask();
        task.setProjectId(request.getProjectId());
        task.setCommitId(commitDiff.getCommitId());
        task.setCommitMessage(commitDiff.getShortMessage());
        task.setCommitAuthor(buildCommitAuthor(commitDiff));
        task.setCommitTime(commitDiff.getCommitTime());
        task.setStatus(STATUS_PENDING);
        task.setSummary("已生成 Diff 快照，等待 AI Code Review");
        task.setCreatedAt(now);
        reviewTaskMapper.insert(task);

        for (GitDiffFileResponse diffFile : commitDiff.getFiles()) {
            ReviewChangedFile changedFile = new ReviewChangedFile();
            changedFile.setReviewTaskId(task.getId());
            changedFile.setFilePath(resolveFilePath(diffFile));
            changedFile.setOldPath(diffFile.getOldPath());
            changedFile.setNewPath(diffFile.getNewPath());
            changedFile.setChangeType(diffFile.getChangeType());
            changedFile.setDiffStatus(diffFile.isTruncated() ? DIFF_STATUS_TRUNCATED : DIFF_STATUS_FULL);
            changedFile.setAdditions(diffFile.getAdditions());
            changedFile.setDeletions(diffFile.getDeletions());
            changedFile.setDiffSnapshot(diffFile.getDiffContent());
            changedFile.setCreatedAt(now);
            changedFileMapper.insert(changedFile);
        }

        return getById(task.getId());
    }

    public List<ReviewTaskResponse> list(Long projectId) {
        LambdaQueryWrapper<ReviewTask> wrapper = new LambdaQueryWrapper<ReviewTask>()
                .eq(projectId != null, ReviewTask::getProjectId, projectId)
                .orderByDesc(ReviewTask::getCreatedAt);
        return reviewTaskMapper.selectList(wrapper).stream()
                .map(task -> toTaskResponse(task, false))
                .toList();
    }

    public ReviewTaskResponse getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("评审任务ID不能为空");
        }
        ReviewTask task = reviewTaskMapper.selectById(id);
        if (task == null) {
            throw new IllegalArgumentException("评审任务不存在");
        }
        return toTaskResponse(task, true);
    }

    private ReviewTaskResponse toTaskResponse(ReviewTask task, boolean includeChangedFiles) {
        ReviewTaskResponse response = new ReviewTaskResponse();
        response.setId(task.getId());
        response.setProjectId(task.getProjectId());
        response.setCommitId(task.getCommitId());
        response.setCommitMessage(task.getCommitMessage());
        response.setCommitAuthor(task.getCommitAuthor());
        response.setCommitTime(task.getCommitTime());
        response.setStatus(task.getStatus());
        response.setRiskLevel(task.getRiskLevel());
        response.setSummary(task.getSummary());
        response.setCreatedAt(task.getCreatedAt());
        response.setStartedAt(task.getStartedAt());
        response.setFinishedAt(task.getFinishedAt());

        List<ReviewChangedFile> files = listChangedFiles(task.getId());
        response.setTotalFiles(files.size());
        response.setTotalAdditions(files.stream().mapToInt(file -> nullToZero(file.getAdditions())).sum());
        response.setTotalDeletions(files.stream().mapToInt(file -> nullToZero(file.getDeletions())).sum());
        if (includeChangedFiles) {
            response.setChangedFiles(files.stream().map(this::toChangedFileResponse).toList());
        }
        return response;
    }

    private List<ReviewChangedFile> listChangedFiles(Long taskId) {
        LambdaQueryWrapper<ReviewChangedFile> wrapper = new LambdaQueryWrapper<ReviewChangedFile>()
                .eq(ReviewChangedFile::getReviewTaskId, taskId)
                .orderByAsc(ReviewChangedFile::getId);
        return changedFileMapper.selectList(wrapper);
    }

    private ReviewChangedFileResponse toChangedFileResponse(ReviewChangedFile file) {
        ReviewChangedFileResponse response = new ReviewChangedFileResponse();
        response.setId(file.getId());
        response.setFilePath(file.getFilePath());
        response.setOldPath(file.getOldPath());
        response.setNewPath(file.getNewPath());
        response.setChangeType(file.getChangeType());
        response.setDiffStatus(file.getDiffStatus());
        response.setAdditions(file.getAdditions());
        response.setDeletions(file.getDeletions());
        response.setDiffSnapshot(file.getDiffSnapshot());
        return response;
    }

    private String buildCommitAuthor(GitCommitDiffResponse commitDiff) {
        if (!StringUtils.hasText(commitDiff.getAuthorEmail())) {
            return commitDiff.getAuthorName();
        }
        return commitDiff.getAuthorName() + " <" + commitDiff.getAuthorEmail() + ">";
    }

    private String resolveFilePath(GitDiffFileResponse diffFile) {
        if (StringUtils.hasText(diffFile.getNewPath())) {
            return diffFile.getNewPath();
        }
        return diffFile.getOldPath();
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }
}
