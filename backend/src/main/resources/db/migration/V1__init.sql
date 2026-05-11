CREATE TABLE monitored_targets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE TABLE check_results (
    id BIGSERIAL PRIMARY KEY,
    target_id BIGINT NOT NULL REFERENCES monitored_targets(id) ON DELETE CASCADE,
    status_code INTEGER,
    response_time_ms INTEGER,
    available BOOLEAN NOT NULL,
    checked_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_check_results_target_checked_at
    ON check_results (target_id, checked_at DESC);
