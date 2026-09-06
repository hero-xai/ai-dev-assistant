package com.xcy.aidevassistant.git.application;

import com.xcy.aidevassistant.git.dto.GitCommitDetailResponse;
import com.xcy.aidevassistant.git.dto.GitCommitDiffResponse;
import com.xcy.aidevassistant.git.dto.GitCommitResponse;
import com.xcy.aidevassistant.git.dto.GitDiffFileResponse;
import com.xcy.aidevassistant.git.dto.GitFileChangeResponse;
import com.xcy.aidevassistant.project.application.ProjectApplicationService;
import com.xcy.aidevassistant.project.domain.DevProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitCommitApplicationService {

    private static final int MAX_DIFF_CHARS_PER_FILE = 20_000;

    private final ProjectApplicationService projectApplicationService;

    public GitCommitApplicationService(ProjectApplicationService projectApplicationService) {
        this.projectApplicationService = projectApplicationService;
    }

    public List<GitCommitResponse> listCommits(Long projectId, int limit) {
        int safeLimit = normalizeLimit(limit);
        DevProject project = projectApplicationService.getProjectEntity(projectId);

        try (Repository repository = openRepository(project.getRepositoryPath());
             Git git = new Git(repository)) {
            List<GitCommitResponse> result = new ArrayList<>();
            Iterable<RevCommit> commits = git.log().setMaxCount(safeLimit).call();
            for (RevCommit commit : commits) {
                result.add(toCommitResponse(commit));
            }
            return result;
        } catch (Exception exception) {
            throw new IllegalArgumentException("读取 Git 提交记录失败：" + exception.getMessage());
        }
    }

    public GitCommitDetailResponse getCommitDetail(Long projectId, String commitId) {
        DevProject project = projectApplicationService.getProjectEntity(projectId);

        try (Repository repository = openRepository(project.getRepositoryPath());
             RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = resolveCommit(repository, revWalk, commitId);
            GitCommitDetailResponse response = toCommitDetailResponse(commit);
            response.setChangedFiles(listChangedFiles(repository, commit));
            return response;
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("读取 Git 提交详情失败：" + exception.getMessage());
        }
    }

    public GitCommitDiffResponse getCommitDiff(Long projectId, String commitId) {
        DevProject project = projectApplicationService.getProjectEntity(projectId);

        try (Repository repository = openRepository(project.getRepositoryPath());
             RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = resolveCommit(repository, revWalk, commitId);
            List<GitDiffFileResponse> files = listDiffFiles(repository, commit);

            GitCommitDiffResponse response = new GitCommitDiffResponse();
            fillCommitResponse(response, commit);
            response.setFiles(files);
            response.setTotalFiles(files.size());
            response.setTotalAdditions(files.stream().mapToInt(GitDiffFileResponse::getAdditions).sum());
            response.setTotalDeletions(files.stream().mapToInt(GitDiffFileResponse::getDeletions).sum());
            return response;
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("生成 Git Diff 失败：" + exception.getMessage());
        }
    }

    private Repository openRepository(String repositoryPath) throws IOException {
        File directory = new File(repositoryPath);
        Repository repository = new FileRepositoryBuilder()
                .findGitDir(directory)
                .readEnvironment()
                .build();
        if (repository.getDirectory() == null) {
            throw new IllegalArgumentException("该项目路径不是 Git 仓库");
        }
        return repository;
    }

    private RevCommit resolveCommit(Repository repository, RevWalk revWalk, String commitId) throws IOException {
        ObjectId objectId = repository.resolve(commitId);
        if (objectId == null) {
            throw new IllegalArgumentException("Commit 不存在");
        }
        return revWalk.parseCommit(objectId);
    }

    private List<GitFileChangeResponse> listChangedFiles(Repository repository, RevCommit commit) throws IOException {
        try (RevWalk revWalk = new RevWalk(repository);
             DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);

            AbstractTreeIterator oldTree = createOldTreeIterator(repository, revWalk, commit);
            AbstractTreeIterator newTree = createTreeIterator(repository, commit);
            List<DiffEntry> entries = diffFormatter.scan(oldTree, newTree);

            List<GitFileChangeResponse> changes = new ArrayList<>();
            for (DiffEntry entry : entries) {
                changes.add(toFileChangeResponse(diffFormatter, entry));
            }
            return changes;
        }
    }

    private List<GitDiffFileResponse> listDiffFiles(Repository repository, RevCommit commit) throws IOException {
        try (RevWalk revWalk = new RevWalk(repository);
             DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);

            AbstractTreeIterator oldTree = createOldTreeIterator(repository, revWalk, commit);
            AbstractTreeIterator newTree = createTreeIterator(repository, commit);
            List<DiffEntry> entries = diffFormatter.scan(oldTree, newTree);

            List<GitDiffFileResponse> files = new ArrayList<>();
            for (DiffEntry entry : entries) {
                files.add(toDiffFileResponse(repository, entry));
            }
            return files;
        }
    }

    private AbstractTreeIterator createOldTreeIterator(Repository repository, RevWalk revWalk, RevCommit commit) throws IOException {
        if (commit.getParentCount() == 0) {
            return new EmptyTreeIterator();
        }
        RevCommit parent = revWalk.parseCommit(commit.getParent(0).getId());
        return createTreeIterator(repository, parent);
    }

    private AbstractTreeIterator createTreeIterator(Repository repository, RevCommit commit) throws IOException {
        CanonicalTreeParser parser = new CanonicalTreeParser();
        try (var reader = repository.newObjectReader()) {
            parser.reset(reader, commit.getTree());
        }
        return parser;
    }

    private GitFileChangeResponse toFileChangeResponse(DiffFormatter diffFormatter, DiffEntry entry) throws IOException {
        GitFileChangeResponse response = new GitFileChangeResponse();
        fillFileChangeResponse(response, diffFormatter, entry);
        return response;
    }

    private GitDiffFileResponse toDiffFileResponse(Repository repository, DiffEntry entry) throws IOException {
        GitDiffFileResponse response = new GitDiffFileResponse();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DiffFormatter diffFormatter = new DiffFormatter(outputStream)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);
            fillFileChangeResponse(response, diffFormatter, entry);
            diffFormatter.format(entry);

            String diffContent = outputStream.toString(StandardCharsets.UTF_8);
            response.setTruncated(diffContent.length() > MAX_DIFF_CHARS_PER_FILE);
            response.setDiffContent(response.isTruncated()
                    ? diffContent.substring(0, MAX_DIFF_CHARS_PER_FILE)
                    : diffContent);
        }
        return response;
    }

    private void fillFileChangeResponse(GitFileChangeResponse response, DiffFormatter diffFormatter, DiffEntry entry) throws IOException {
        response.setChangeType(entry.getChangeType().name());
        response.setOldPath(DiffEntry.DEV_NULL.equals(entry.getOldPath()) ? null : entry.getOldPath());
        response.setNewPath(DiffEntry.DEV_NULL.equals(entry.getNewPath()) ? null : entry.getNewPath());

        int additions = 0;
        int deletions = 0;
        for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
            additions += edit.getEndB() - edit.getBeginB();
            deletions += edit.getEndA() - edit.getBeginA();
        }
        response.setAdditions(additions);
        response.setDeletions(deletions);
    }

    private GitCommitDetailResponse toCommitDetailResponse(RevCommit commit) {
        GitCommitDetailResponse response = new GitCommitDetailResponse();
        fillCommitResponse(response, commit);
        response.setFullMessage(commit.getFullMessage());
        return response;
    }

    private GitCommitResponse toCommitResponse(RevCommit commit) {
        GitCommitResponse response = new GitCommitResponse();
        fillCommitResponse(response, commit);
        return response;
    }

    private void fillCommitResponse(GitCommitResponse response, RevCommit commit) {
        response.setCommitId(commit.getName());
        response.setShortCommitId(commit.getName().substring(0, 8));
        response.setShortMessage(commit.getShortMessage());
        response.setAuthorName(commit.getAuthorIdent().getName());
        response.setAuthorEmail(commit.getAuthorIdent().getEmailAddress());
        response.setCommitTime(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(commit.getCommitTime()),
                ZoneId.systemDefault()
        ));
        response.setParentCount(commit.getParentCount());
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 20;
        }
        return Math.min(limit, 100);
    }
}
