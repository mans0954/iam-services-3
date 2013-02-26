use openiam;
DROP TABLE IF EXISTS REPORT_INFO;
CREATE TABLE REPORT_INFO(
	REPORT_INFO_ID varchar(32) NOT NULL,
	REPORT_NAME VARCHAR(64) NOT NULL UNIQUE,
    DATASOURCE_FILE_PATH VARCHAR(255) NOT NULL,
    REPORT_FILE_PATH VARCHAR(255),
    PRIMARY KEY (REPORT_INFO_ID)
)Engine=InnoDB;