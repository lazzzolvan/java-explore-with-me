

DROP TABLE IF EXISTS endpoint_hits CASCADE;

    CREATE TABLE endpoint_hits (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(50) NOT NULL,
    uri VARCHAR(50) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);