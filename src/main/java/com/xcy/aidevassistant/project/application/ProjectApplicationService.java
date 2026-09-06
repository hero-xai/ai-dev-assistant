package com.xcy.aidevassistant.project.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcy.aidevassistant.project.domain.DevProject;
import com.xcy.aidevassistant.project.dto.ProjectCreateRequest;
import com.xcy.aidevassistant.project.dto.ProjectResponse;
import com.xcy.aidevassistant.project.dto.ProjectUpdateRequest;
import com.xcy.aidevassistant.project.mapper.DevProjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectApplicationService {

    private final DevProjectMapper projectMapper;

    public ProjectApplicationService(DevProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        String repositoryPath = normalizePath(request.getRepositoryPath());
        ensureRepositoryPathNotExists(repositoryPath, null);

        DevProject project = new DevProject();
        project.setName(request.getName().trim());
        project.setRepositoryPath(repositoryPath);
        project.setDefaultBranch(trimToNull(request.getDefaultBranch()));
        project.setDescription(trimToNull(request.getDescription()));
        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        projectMapper.insert(project);
        return toResponse(project);
    }

    public List<ProjectResponse> list() {
        LambdaQueryWrapper<DevProject> wrapper = new LambdaQueryWrapper<DevProject>()
                .orderByDesc(DevProject::getUpdatedAt);
        return projectMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getById(Long id) {
        DevProject project = findProject(id);
        return toResponse(project);
    }

    public DevProject getProjectEntity(Long id) {
        return findProject(id);
    }

    @Transactional
    public ProjectResponse update(ProjectUpdateRequest request) {
        DevProject project = findProject(request.getId());
        String repositoryPath = normalizePath(request.getRepositoryPath());
        ensureRepositoryPathNotExists(repositoryPath, request.getId());

        project.setName(request.getName().trim());
        project.setRepositoryPath(repositoryPath);
        project.setDefaultBranch(trimToNull(request.getDefaultBranch()));
        project.setDescription(trimToNull(request.getDescription()));
        project.setUpdatedAt(LocalDateTime.now());

        projectMapper.updateById(project);
        return toResponse(project);
    }

    @Transactional
    public void delete(Long id) {
        DevProject project = findProject(id);
        projectMapper.deleteById(project.getId());
    }

    private DevProject findProject(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        DevProject project = projectMapper.selectById(id);
        if (project == null) {
            throw new IllegalArgumentException("项目不存在");
        }
        return project;
    }

    private void ensureRepositoryPathNotExists(String repositoryPath, Long currentProjectId) {
        LambdaQueryWrapper<DevProject> wrapper = new LambdaQueryWrapper<DevProject>()
                .eq(DevProject::getRepositoryPath, repositoryPath);
        DevProject existing = projectMapper.selectOne(wrapper);
        if (existing != null && !existing.getId().equals(currentProjectId)) {
            throw new IllegalArgumentException("该本地仓库路径已被接入");
        }
    }

    private String normalizePath(String repositoryPath) {
        if (!StringUtils.hasText(repositoryPath)) {
            throw new IllegalArgumentException("本地仓库路径不能为空");
        }
        return Path.of(repositoryPath.trim()).toAbsolutePath().normalize().toString();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private ProjectResponse toResponse(DevProject project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setRepositoryPath(project.getRepositoryPath());
        response.setDefaultBranch(project.getDefaultBranch());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }
}
