CREATE TABLE `{prefix}accounts` (
    `uuid`      BINARY(16)          NOT NULL,
    `currency`  VARCHAR(100)        NOT NULL,
    `balance`   DOUBLE              NOT NULL,
    `virtual`   BOOLEAN             NOT NULL    DEFAULT false,
    PRIMARY KEY (`uuid`, `currency`)
) DEFAULT CHARSET = utf8;