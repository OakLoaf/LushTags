CREATE TABLE IF NOT EXISTS lushtags_users
(
    uuid CHAR(36) NOT NULL,
    username TEXT,
    tags TEXT,
    PRIMARY KEY (uuid)
);