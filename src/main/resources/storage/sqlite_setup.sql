CREATE TABLE IF NOT EXISTS lushtags_users
(
    uuid BINARY(128) NOT NULL,
    username TEXT,
    tags TEXT,
    PRIMARY KEY (uuid)
);