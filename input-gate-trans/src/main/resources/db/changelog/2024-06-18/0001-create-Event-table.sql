CREATE TABLE EVENT
(
    ID          LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
    CLIENT_ID   VARCHAR(255)                    NOT NULL,
    TOPIC_NAME  VARCHAR(255)                    NOT NULL,
    REQUEST_ID  VARCHAR(255)                    NOT NULL,
    DESCRIPTION VARCHAR(500)                    NOT NULL,
    UPDATE_TIME DATETIME                        NOT NULL,
    EVENT_NAME  VARCHAR(255)                    NOT NULL,
    EVENT_VALUE BLOB                            NOT NULL,
    CONSTRAINT UN_REQUEST UNIQUE (REQUEST_ID)
)
