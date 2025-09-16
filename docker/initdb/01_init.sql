CREATE TYPE role_enum AS ENUM ('ADMIN', 'ORGANIZER', 'SEEKER');
CREATE TYPE source_enum AS ENUM ('QR');
CREATE TYPE status_enum AS ENUM ('ACCEPTED', 'REJECTED');
CREATE TYPE reason_enum AS ENUM ('SCAN_EARN', 'REDEEM', 'ADMIN_ADJUST');


CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role role_enum NOT NULL,
    active BOOLEAN NOT NULL,

    username VARCHAR(40) NOT NULL,
    password_hash VARCHAR(100),
    bio VARCHAR (160),
    email VARCHAR(254) UNIQUE,
    phone_number VARCHAR(20),

    created_at TIMESTAMPZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uc_users_username UNIQUE (username),
    CONSTRAINT uc_users_email UNIQUE (email),
);

CREATE INDEX idx_users_active ON users (active);

CREATE TABLE fundraisers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_id UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    title VARCHAR(80),
    description VARCHAR(500),
    email VARCHAR(254),
    phone_number VARCHAR(20),
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    starts_at TIMESTAMPZ,
    ends_at TIMESTAMPZ,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_organizer_user FOREIGN KEY (organizer_id) REFERENCES users (id)
);

CREATE INDEX idx_fundraisers_active_lat_lon ON fundraisers (active, lat, lon);
CREATE INDEX idx_fundraisers_organizer ON fundraisers (organizer_id);


CREATE TABLE scans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fundraiser_id UUID NOT NULL,
    participant_user_id UUID NOT NULL,
    organizer_user_id UUID NOT NULL,
    source source_enum NOT NULL,
    status status_enum NOT NULL,
    idempotency_key VARCHAR(64) NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT uk_scans_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT fk_scans_fundraiser FOREIGN KEY (fundraiser_id) REFERENCES fundraisers (id),
    CONSTRAINT fk_scans_participant_user FOREIGN KEY (participant_user_id) REFERENCES users (id),
    CONSTRAINT fk_scans_organizer_user FOREIGN KEY (organizer_user_id) REFERENCES users (id)
);

CREATE INDEX idx_scans_created_at ON scans (created_at);
CREATE INDEX idx_scans_fundraiser_created_at ON scans (fundraiser_id, created_at DESC);
CREATE INDEX idx_scans_participant_created_at ON scans (participant_user_id, created_at DESC);


CREATE TABLE points_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    fundraiser_id UUID NOT NULL,
    scan_id UUID NOT NULL,
    delta INT NOT NULL,
    reason reason_enum NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

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