use openiam;
DROP TABLE IF EXISTS REPORT_QUERY;
CREATE TABLE REPORT_QUERY(
	report_query_id varchar(20) NOT NULL,
	report_name VARCHAR(64) NOT NULL UNIQUE,
    query_script_path VARCHAR(255) NOT NULL,
    report_file_path VARCHAR(255) NOT NULL,
    params VARCHAR(255) NOT NULL,
    required_params VARCHAR(255) NOT NULL,
    dto_class VARCHAR(255) NOT NULL,
    PRIMARY KEY (report_query_id)
)Engine=InnoDB;

insert into menu(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values( 'BIRT_REPORT', 'en', 'IDM', 'BIRT Reports', 'BIRT Reports', 'birtReport.cnt', '5');

insert into menu(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values('TESTREPORT', 'en', 'BIRT_REPORT', 'Test Reports', 'Test reports', 'testReport.cnt', '2');

insert into PERMISSIONS(menu_id,role_id) values('BIRT_REPORT','SUPER_SEC_ADMIN');
