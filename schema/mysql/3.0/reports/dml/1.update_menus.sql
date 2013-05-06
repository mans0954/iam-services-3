use openiam;

delete from PERMISSIONS where menu_id = 'BIRT_REPORT';
delete from MENU where menu_id = 'BIRT_REPORT';
delete from MENU where menu_id = 'TESTREPORT';

insert into MENU(menu_id, language_cd, menu_group, menu_name, menu_desc, url, display_order)
	values( 'BIRT_REPORT', 'en', 'IDM', 'BIRT Reports', 'BIRT Reports', 'birtReportList.cnt', '5');

insert into PERMISSIONS(menu_id,role_id) values('BIRT_REPORT','9');
