CREATE TABLE IF NOT EXISTS users
(
    id
    UUID
    PRIMARY
    KEY,
    role
    VARCHAR
(
    16
) NOT NULL,
    active BOOLEAN NOT NULL,
    username VARCHAR
(
    40
) NOT NULL,
    password_hash VARCHAR
(
    100
),
    first_name VARCHAR
(
    50
),
    last_name VARCHAR
(
    50
),
    bio VARCHAR
(
    160
),
    email VARCHAR
(
    254
),
    phone_number VARCHAR
(
    32
),

    total_points INT NOT NULL DEFAULT 0,
    total_fundraisers INT NOT NULL DEFAULT 0,
    location VARCHAR
(
    120
),
    banner_color VARCHAR
(
    7
),

    display_name BOOLEAN NOT NULL DEFAULT FALSE,
    display_email BOOLEAN NOT NULL DEFAULT FALSE,
    display_phone BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uc_users_username UNIQUE
(
    username
),
    CONSTRAINT uc_users_email UNIQUE
(
    email
),
    CONSTRAINT uc_users_phone_number UNIQUE
(
    phone_number
)
    );
CREATE UNIQUE INDEX uc_users_email_ci ON users ((lower (email)) );
CREATE INDEX idx_users_active ON users (active);
CREATE INDEX idx_users_total_points_desc ON users (total_points DESC);

CREATE TABLE IF NOT EXISTS fundraisers
(
    id
    UUID
    PRIMARY
    KEY,
    organizer_id
    UUID
    NOT
    NULL
    REFERENCES
    users
(
    id
) ON DELETE CASCADE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    reward BOOLEAN NOT NULL DEFAULT TRUE,
    title VARCHAR
(
    80
) NOT NULL,
    description VARCHAR
(
    500
),
    email VARCHAR
(
    254
),
    phone_number VARCHAR
(
    20
),
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    starts_at TIMESTAMPTZ,
    ends_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL
    );
CREATE INDEX IF NOT EXISTS idx_fundraisers_active_lat_lon ON fundraisers (active, lat, lon);
CREATE INDEX IF NOT EXISTS idx_fundraisers_organizer ON fundraisers (organizer_id);
CREATE INDEX IF NOT EXISTS idx_fundraisers_starts_at ON fundraisers (starts_at);
CREATE INDEX IF NOT EXISTS idx_fundraisers_ends_at ON fundraisers (ends_at);
CREATE INDEX IF NOT EXISTS idx_fundraisers_created_at_desc ON fundraisers (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_fundraisers_organizer_created_at_desc ON fundraisers (organizer_id, created_at DESC);

CREATE TABLE IF NOT EXISTS user_favorite_fundraisers
(
    id
    UUID
    PRIMARY
    KEY,
    user_id
    UUID
    NOT
    NULL
    REFERENCES
    users
(
    id
) ON DELETE CASCADE,
    fundraiser_id UUID NOT NULL REFERENCES fundraisers
(
    id
)
  ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_user_fav UNIQUE
(
    user_id,
    fundraiser_id
)
    );
CREATE INDEX idx_user_fav_user ON user_favorite_fundraisers (user_id);
CREATE INDEX idx_user_fav_fundraiser ON user_favorite_fundraisers (fundraiser_id);

CREATE TABLE IF NOT EXISTS user_followers
(
    id
    UUID
    PRIMARY
    KEY,
    user_id
    UUID
    NOT
    NULL
    REFERENCES
    users
(
    id
) ON DELETE CASCADE,
    follower_user_id UUID NOT NULL REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_user_follow UNIQUE
(
    user_id,
    follower_user_id
),
    CONSTRAINT ck_no_self_follow CHECK
(
    user_id
    <>
    follower_user_id
)
    );
CREATE INDEX idx_follow_user ON user_followers (user_id);
CREATE INDEX idx_follow_follower ON user_followers (follower_user_id);

CREATE TABLE IF NOT EXISTS scans
(
    id
    UUID
    PRIMARY
    KEY,
    fundraiser_id
    UUID
    NOT
    NULL,
    participant_user_id
    UUID
    NOT
    NULL,
    organizer_user_id
    UUID
    NOT
    NULL,
    source
    VARCHAR
(
    16
) NOT NULL,
    status VARCHAR
(
    16
) NOT NULL,
    idempotency_key VARCHAR
(
    64
) NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_scans_idempotency_key UNIQUE
(
    idempotency_key
),
    CONSTRAINT fk_scans_fundraiser FOREIGN KEY
(
    fundraiser_id
) REFERENCES fundraisers
(
    id
),
    CONSTRAINT fk_scans_participant_user FOREIGN KEY
(
    participant_user_id
) REFERENCES users
(
    id
),
    CONSTRAINT fk_scans_organizer_user FOREIGN KEY
(
    organizer_user_id
) REFERENCES users
(
    id
),
    CONSTRAINT fk_no_dupe_scan UNIQUE
(
    participant_user_id,
    fundraiser_id
)
    );
CREATE INDEX idx_scans_participant_created_at ON scans (participant_user_id, created_at DESC);


CREATE TABLE IF NOT EXISTS points_transactions
(
    id
    UUID
    PRIMARY
    KEY,
    user_id
    UUID
    NOT
    NULL,
    fundraiser_id
    UUID
    NOT
    NULL,
    scan_id
    UUID
    NOT
    NULL,
    delta
    INT
    NOT
    NULL,
    reason
    VARCHAR
(
    16
) NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_points_transaction_user FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
),
    CONSTRAINT fk_points_transaction_fundraiser FOREIGN KEY
(
    fundraiser_id
) REFERENCES fundraisers
(
    id
),
    CONSTRAINT fk_points_transaction_scan FOREIGN KEY
(
    scan_id
) REFERENCES scans
(
    id
),
    CONSTRAINT ck_points_transaction_delta CHECK
(
    delta
    BETWEEN
    -
    100000
    AND
    100000
)
    );
CREATE INDEX idx_points_transactions_created_at ON points_transactions (created_at);
CREATE INDEX idx_points_transactions_user_created_at ON points_transactions (user_id, created_at DESC);
CREATE INDEX idx_points_transactions_fundraiser_created_at ON points_transactions (fundraiser_id, created_at DESC);
CREATE INDEX idx_points_transactions_scan_created_at ON points_transactions (scan_id, created_at DESC);