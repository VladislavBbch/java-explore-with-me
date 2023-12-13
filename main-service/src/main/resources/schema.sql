DROP TABLE IF EXISTS REQUESTS, EVENTS_COMPILATIONS, EVENTS, COMPILATIONS, USERS, CATEGORIES;
DROP TYPE IF EXISTS EVENT_STATE;
DROP TYPE IF EXISTS REQUEST_STATUS;

CREATE TABLE IF NOT EXISTS USERS
(
    ID    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(254) NOT NULL,
    NAME  VARCHAR(250) NOT NULL
);

CREATE UNIQUE INDEX USERS_EMAIL_INDEX ON USERS (EMAIL);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    ID   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX CATEGORIES_NAME_INDEX ON CATEGORIES (NAME);

CREATE TYPE EVENT_STATE AS ENUM ('PENDING', 'PUBLISHED', 'CANCELED');

CREATE TABLE IF NOT EXISTS EVENTS
(
    ID                             INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ANNOTATION                     VARCHAR(2000) NOT NULL,
    CATEGORY_ID                    INTEGER       NOT NULL REFERENCES CATEGORIES (ID),
    DESCRIPTION                    VARCHAR(7000) NOT NULL,
    EVENT_DATE                     TIMESTAMP     NOT NULL,
    LATITUDE                       DECIMAL       NOT NULL,
    LONGITUDE                      DECIMAL       NOT NULL,
    IS_PAID                        BOOLEAN       NOT NULL,
    PARTICIPANT_LIMIT              INTEGER       NOT NULL,
    IS_REQUEST_MODERATION_REQUIRED BOOLEAN       NOT NULL,
    TITLE                          VARCHAR(120)  NOT NULL,
    STATE                          EVENT_STATE   NOT NULL,
    INITIATOR_ID                   INTEGER       NOT NULL REFERENCES USERS (ID),
    CREATION_DATE                  TIMESTAMP     NOT NULL DEFAULT NOW(),
    PUBLISH_DATE                   TIMESTAMP     NULL
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    ID        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    IS_PINNED BOOLEAN     NOT NULL,
    TITLE     VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTS_COMPILATIONS
(
    EVENT_ID       INTEGER NOT NULL REFERENCES EVENTS (ID),
    COMPILATION_ID INTEGER NOT NULL REFERENCES COMPILATIONS (ID) ON DELETE CASCADE,
    CONSTRAINT events_compilations_pk PRIMARY KEY (EVENT_ID, COMPILATION_ID)
);

CREATE TYPE REQUEST_STATUS AS ENUM ('PENDING', 'CONFIRMED', 'CANCELED', 'REJECTED');

CREATE TABLE IF NOT EXISTS REQUESTS
(
    ID            INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USER_ID       INTEGER        NOT NULL REFERENCES USERS (ID),
    EVENT_ID      INTEGER        NOT NULL REFERENCES EVENTS (ID),
    STATUS        REQUEST_STATUS NOT NULL,
    CREATION_DATE TIMESTAMP      NOT NULL DEFAULT NOW()
);

