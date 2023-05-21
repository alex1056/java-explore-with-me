
DROP TABLE IF EXISTS PUBLIC.STATS cascade;

CREATE TABLE IF NOT EXISTS PUBLIC.STATS (
                                id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                               	app CHARACTER(24),
                                uri CHARACTER(24),
                                ip CHARACTER(20),
                                created TIMESTAMP,
                                CONSTRAINT stats_pk_stat_id PRIMARY KEY (id)
                               );