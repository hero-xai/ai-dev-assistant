package com.xcy.aidevassistant.project.controller;

import com.xcy.aidevassistant.common.api.ApiResponse;
import com.xcy.aidevassistant.project.application.ProjectApplicationService;
import com.xcy.aidevassistant.project.dto.ProjectCreateRequest;
import com.xcy.aidevassistant.project.dto.ProjectResponse;
import com.xcy.aidevassistant.project.dto.ProjectUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectApplicationService projectApplicationService;

    public ProjectController(ProjectApplicationService projectApplicationService) {
        this.projectApplicationService = projectApplicationService;
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ApiResponse.success(projectApplicationService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> list() {
        return ApiResponse.success(projectApplicationService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(projectApplicationService.getById(id));
    }

    @PutMapping
    public ApiResponse<ProjectResponse> update(@Valid @RequestBody ProjectUpdateRequest request) {
        return ApiResponse.success(projectApplicationService.update(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        projectApplicationService.delete(id);
        return ApiResponse.success(null);
    }
}
