CREATE TABLE IF NOT EXISTS dev_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    repository_path VARCHAR(500) NOT NULL,
    default_branch VARCHAR(100) NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dev_project_repository_path (repository_path),
    KEY idx_dev_project_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接入的本地 Git 项目';

CREATE TABLE IF NOT EXISTS review_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    commit_id VARCHAR(80) NOT NULL,
    parent_commit_id VARCHAR(80) NULL,
    commit_message VARCHAR(1000) NULL,
    commit_author VARCHAR(200) NULL,
    commit_time DATETIME NULL,
    status VARCHAR(30) NOT NULL,
    risk_level VARCHAR(30) NULL,
    summary VARCHAR(2000) NULL,
    model_name VARCHAR(100) NULL,
    error_message VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME NULL,
    finished_at DATETIME NULL,
    KEY idx_review_task_project_id (project_id),
    KEY idx_review_task_commit_id (commit_id),
    KEY idx_review_task_status (status),
    CONSTRAINT fk_review_task_project
        FOREIGN KEY (project_id) REFERENCES dev_project (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Code Review 任务';

CREATE TABLE IF NOT EXISTS review_changed_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_task_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    old_path VARCHAR(500) NULL,
    new_path VARCHAR(500) NULL,
    change_type VARCHAR(30) NOT NULL,
    diff_status VARCHAR(50) NOT NULL,
    additions INT NOT NULL DEFAULT 0,
    deletions INT NOT NULL DEFAULT 0,
    diff_snapshot MEDIUMTEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_review_changed_file_task_id (review_task_id),
    KEY idx_review_changed_file_path (file_path),
    CONSTRAINT fk_review_changed_file_task
        FOREIGN KEY (review_task_id) REFERENCES review_task (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Review 任务中的变更文件快照';

CREATE TABLE IF NOT EXISTS review_issue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_task_id BIGINT NOT NULL,
    file_path VARCHAR(500) NULL,
    category VARCHAR(80) NOT NULL,
    severity VARCHAR(30) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    code_snippet TEXT NULL,
    suggestion VARCHAR(2000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_review_issue_task_id (review_task_id),
    KEY idx_review_issue_severity (severity),
    KEY idx_review_issue_category (category),
    CONSTRAINT fk_review_issue_task
        FOREIGN KEY (review_task_id) REFERENCES review_task (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结构化 Code Review 问题';

CREATE TABLE IF NOT EXISTS generated_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_task_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    content MEDIUMTEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_generated_document_task_id (review_task_id),
    KEY idx_generated_document_type (document_type),
    CONSTRAINT fk_generated_document_task
        FOREIGN KEY (review_task_id) REFERENCES review_task (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 生成的研发文档';
