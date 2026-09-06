package com.xcy.aidevassistant.review.controller;

import com.xcy.aidevassistant.common.api.ApiResponse;
import com.xcy.aidevassistant.review.application.ReviewTaskApplicationService;
import com.xcy.aidevassistant.review.dto.ReviewTaskCreateRequest;
import com.xcy.aidevassistant.review.dto.ReviewTaskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/review-tasks")
public class ReviewTaskController {

    private final ReviewTaskApplicationService reviewTaskApplicationService;

    public ReviewTaskController(ReviewTaskApplicationService reviewTaskApplicationService) {
        this.reviewTaskApplicationService = reviewTaskApplicationService;
    }

    @PostMapping
    public ApiResponse<ReviewTaskResponse> create(@Valid @RequestBody ReviewTaskCreateRequest request) {
        return ApiResponse.success(reviewTaskApplicationService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ReviewTaskResponse>> list(@RequestParam(required = false) Long projectId) {
        return ApiResponse.success(reviewTaskApplicationService.list(projectId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewTaskResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(reviewTaskApplicationService.getById(id));
    }
}
