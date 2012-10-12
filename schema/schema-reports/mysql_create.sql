use openiam;
DROP TABLE IF EXISTS REPORT_INFO;
CREATE TABLE REPORT_INFO(
	REPORT_INFO_ID varchar(32) NOT NULL,
	REPORT_NAME VARCHAR(64) NOT NULL UNIQUE,
    GROOVY_SCRIPT_PATH VARCHAR(255) NOT NULL,
    REPORT_FILE_PATH VARCHAR(255),
    PARAMS VARCHAR(255) NOT NULL,
    REQUIRED_PARAMS VARCHAR(255) NOT NULL,
    PRIMARY KEY (REPORT_INFO_ID)
)Engine=InnoDB;

insert into MENU(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values( 'BIRT_REPORT', 'en', 'IDM', 'BIRT Reports', 'BIRT Reports', 'birtReport.cnt', '5');

insert into MENU(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values('TESTREPORT', 'en', 'BIRT_REPORT', 'Test Reports', 'Test reports', 'testReport.cnt', '2');

insert into PERMISSIONS(menu_id,role_id) values('BIRT_REPORT','9');
