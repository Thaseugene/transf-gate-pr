CREATE TABLE MESSAGES
(
    ID                 BIGSERIAL PRIMARY KEY NOT NULL,
    USER_ID            BIGINT                NOT NULL,
    CHAT_ID            BIGINT                NOT NULL,
    MESSAGE_ID         BIGINT                NOT NULL,
    REQUEST_ID         VARCHAR(255)          NOT NULL,
    TIMESTAMP          TIMESTAMP             NOT NULL,
    MESSAGE_VALUE      BYTEA                 NOT NULL,
    MESSAGE_RESULT     BYTEA,
    TRANSLATE_RESULT   BYTEA,
    TRANSLATE_LANGUAGE VARCHAR(255),
    STATUS             BIGINT,
    CONSTRAINT UN_REQUEST UNIQUE (REQUEST_ID),
    CONSTRAINT FK_USER FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID),
    CONSTRAINT FK_STATUS FOREIGN KEY (STATUS) REFERENCES MESSAGE_STATUS (ID)
);

CREATE INDEX IF NOT EXISTS IDX_MESSAGES_MESSAGE_ID ON MESSAGES (MESSAGE_ID);
CREATE INDEX IF NOT EXISTS IDX_MESSAGES_CHAT_ID ON MESSAGES (CHAT_ID);
