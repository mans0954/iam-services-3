
use openiam;

insert into USERS (USER_ID,FIRST_NAME, LAST_NAME, STATUS, COMPANY_ID, SYSTEM_FLAG ) values('0001','system','system','ACTIVE','100','1');

insert into LOGIN(SERVICE_ID, LOGIN, MANAGED_SYS_ID, USER_ID, PASSWORD,RESET_PWD, IS_LOCKED, AUTH_FAIL_COUNT) VALUES('IDM','system','0','0001','passwd00',0,0,0);

update LOGIN set PASSWORD='passwd00';

UPDATE MANAGED_SYS SET USER_ID = '0001';


commit;


