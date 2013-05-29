
USE openiam;

ALTER TABLE RECONCILIATION_CONFIG
ADD COLUMN CSV_LINE_SEPARATOR VARCHAR(10) NULL DEFAULT 'comma',
ADD COLUMN CSV_END_OF_LINE VARCHAR(10) NULL DEFAULT 'enter',
ADD COLUMN NOTIFICATION_EMAIL_ADDRESS VARCHAR(120) NULL,
ADD COLUMN TARGET_SYS_MATCH_SCRIPT VARCHAR(120) NULL;

