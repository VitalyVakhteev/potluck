CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role VARCHAR(16) NOT NULL,
    active BOOLEAN NOT NULL,
    username VARCHAR(40) NOT NULL,
    password_hash VARCHAR(100),
    bio VARCHAR(160),
    email VARCHAR(254),
    phone_number VARCHAR(20),

    total_points INT NOT NULL DEFAULT 0,
    location VARCHAR(120),
    banner_color VARCHAR(7),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uc_users_username UNIQUE (username),
);
CREATE UNIQUE INDEX uc_users_email_ci ON users ((lower(email)));
CREATE INDEX idx_users_active ON users (active);
CREATE INDEX idx_users_total_points_desc ON users (total_points DESC);

CREATE TABLE fundraisers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    title VARCHAR(80) NOT NULL,
    description VARCHAR(500),
    email VARCHAR(254),
    phone_number VARCHAR(20),
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_fundraisers_active_lat_lon ON fundraisers (active, lat, lon);
CREATE INDEX IF NOT EXISTS idx_fundraisers_organizer ON fundraisers (organizer_id);

CREATE TABLE user_favorite_fundraisers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fundraiser_id UUID NOT NULL REFERENCES fundraisers(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL
    CONSTRAINT pk_user_fav UNIQUE (user_id, fundraiser_id)
    CONSTRAINT ck_fund_lat CHECK (lat BETWEEN -90 AND 90),
    CONSTRAINT ck_fund_lon CHECK (lon BETWEEN -180 AND 180);
);
CREATE INDEX idx_user_fav_user ON user_favorite_fundraisers (user_id);
CREATE INDEX idx_user_fav_fundraiser ON user_favorite_fundraisers (fundraiser_id);

CREATE TABLE user_followers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,          -- being followed
    follower_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- who follows
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_user_follow UNIQUE (user_id, follower_user_id),
    CONSTRAINT ck_no_self_follow CHECK (user_id <> follower_user_id)
);
CREATE INDEX idx_follow_user ON user_followers (user_id);
CREATE INDEX idx_follow_follower ON user_followers (follower_user_id);

CREATE TABLE scans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fundraiser_id UUID NOT NULL,
    participant_user_id UUID NOT NULL,
    organizer_user_id UUID NOT NULL,
    source VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    idempotency_key VARCHAR(64) NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_scans_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT fk_scans_fundraiser FOREIGN KEY (fundraiser_id) REFERENCES fundraisers (id),
    CONSTRAINT fk_scans_participant_user FOREIGN KEY (participant_user_id) REFERENCES users (id),
    CONSTRAINT fk_scans_organizer_user FOREIGN KEY (organizer_user_id) REFERENCES users (id)
);
CREATE INDEX idx_scans_created_at ON scans (created_at);
CREATE INDEX idx_scans_fundraiser_created_at ON scans (fundraiser_id, created_at DESC);
CREATE INDEX idx_scans_participant_created_at ON scans (participant_user_id, created_at DESC);
CREATE INDEX idx_scans_organizer_created_at ON scans (organizer_user_id, created_at DESC);


CREATE TABLE points_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    fundraiser_id UUID NOT NULL,
    scan_id UUID NOT NULL,
    delta INT NOT NULL,
    reason VARCHAR(16) NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_points_transaction_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_points_transaction_fundraiser FOREIGN KEY (fundraiser_id) REFERENCES fundraisers (id),
    CONSTRAINT fk_points_transaction_scan FOREIGN KEY (scan_id) REFERENCES scans (id),
    CONSTRAINT ck_points_transaction_delta CHECK (delta BETWEEN -100000 AND 100000)
);
CREATE INDEX idx_points_transactions_created_at ON points_transactions (created_at);
CREATE INDEX idx_points_transactions_reason ON points_transactions (reason);
CREATE INDEX idx_points_transactions_user_created_at ON points_transactions (user_id, created_at DESC);
CREATE INDEX idx_points_transactions_fundraiser_created_at ON points_transactions (fundraiser_id, created_at DESC);
CREATE INDEX idx_points_transactions_scan_created_at ON points_transactions (scan_id, created_at DESC);