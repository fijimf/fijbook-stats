CREATE TABLE team_statistic
(
    id                BIGSERIAL PRIMARY KEY,
    daily_snapshot_id BIGINT NOT NULL,
    team_id           BIGINT NOT NULL,
    value             FLOAT  NOT NULL,
    rank              INT    NOT NULL
);

CREATE UNIQUE INDEX ON team_statistic (daily_snapshot_id, team_id);
CREATE UNIQUE INDEX ON team_statistic (team_id, daily_snapshot_id);


CREATE TABLE daily_snapshot
(
    id                 BIGSERIAL PRIMARY KEY,
    season_snapshot_id BIGINT NOT NULL,
    date               DATE   NOT NULL,
    count              INT    NOT NULL,
    max                FLOAT  NOT NULL,
    median             FLOAT  NOT NULL,
    min                FLOAT  NOT NULL,
    mean               FLOAT  NOT NULL,
    std_dev            FLOAT  NOT NULL
);

CREATE UNIQUE INDEX ON daily_snapshot (season_snapshot_id, date);

CREATE TABLE season_snapshot
(
    id            BIGSERIAL PRIMARY KEY,
    model         VARCHAR(72) NOT NULL,
    key           VARCHAR(72) NOT NULL,
    season_id     BIGINT NOT NULL,
    season_digest VARCHAR(72) NOT NULL
);

CREATE UNIQUE INDEX ON season_snapshot (model, key, season_id);
