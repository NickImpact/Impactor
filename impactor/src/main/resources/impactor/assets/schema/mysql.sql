CREATE TABLE `{prefix}accounts` (
    `uuid`      BINARY(16)          NOT NULL,
    `currency`  VARCHAR(100)        NOT NULL,
    `virtual`   BOOLEAN             NOT NULL    DEFAULT false,
    `balance`   DOUBLE              NOT NULL,
    PRIMARY KEY (`uuid`, `currency`)
) DEFAULT CHARSET = utf8;