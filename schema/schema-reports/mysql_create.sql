use openiam;
DROP TABLE IF EXISTS REPORT_INFO;
CREATE TABLE REPORT_INFO(
	REPORT_INFO_ID varchar(32) NOT NULL,
	REPORT_NAME VARCHAR(64) NOT NULL UNIQUE,
    DATASOURCE_FILE_PATH VARCHAR(255) NOT NULL,
    REPORT_FILE_PATH VARCHAR(255),
    PRIMARY KEY (REPORT_INFO_ID)
)Engine=InnoDB;

delete from PERMISSIONS where menu_id = 'BIRT_REPORT';
delete from MENU where menu_id = 'BIRT_REPORT';
delete from MENU where menu_id = 'TESTREPORT';

insert into MENU(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values( 'BIRT_REPORT', 'en', 'IDM', 'BIRT Reports', 'BIRT Reports', 'birtReportList.cnt', '5');

insert into PERMISSIONS(menu_id,role_id) values('BIRT_REPORT','9');
