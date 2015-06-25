use openiam;

CREATE TABLE MNG_SYS_POLICY (
       MNG_SYS_POLICY_ID            varchar(32) NOT NULL,
       NAME                 varchar(60) NULL,
       DESCRIPTION          varchar(255) NULL,
       IS_PRIMARY           CHAR(1) NOT NULL DEFAULT 'N',
       CREATE_DATE          datetime NULL,
       LAST_UPDATE          datetime NULL,
       MANAGED_SYS_ID      VARCHAR(32) NOT NULL,
       METADATA_TYPE_ID		VARCHAR(20) NULL,
       PRIMARY KEY (MNG_SYS_POLICY_ID),
	   FOREIGN KEY (MANAGED_SYS_ID) REFERENCES MANAGED_SYS(MANAGED_SYS_ID),
       FOREIGN KEY (METADATA_TYPE_ID) REFERENCES METADATA_TYPE(TYPE_ID)
) ENGINE=InnoDB;

INSERT INTO METADATA_TYPE (TYPE_ID, NAME, ACTIVE, SYNC_MANAGED_SYS, GROUPING, IS_BINARY, IS_SENSITIVE, USED_FOR_SMS_OTP)
                  VALUES ('USER_OBJECT', 'Provision User', 'Y', 'Y', 'PROV_OBJECT', 'N', 'N', 'N');
INSERT INTO METADATA_TYPE (TYPE_ID, NAME, ACTIVE, SYNC_MANAGED_SYS, GROUPING, IS_BINARY, IS_SENSITIVE, USED_FOR_SMS_OTP)
                  VALUES ('GROUP_OBJECT', 'Provision Group', 'Y', 'Y', 'PROV_OBJECT', 'N', 'N', 'N');
INSERT INTO METADATA_TYPE (TYPE_ID, NAME, ACTIVE, SYNC_MANAGED_SYS, GROUPING, IS_BINARY, IS_SENSITIVE, USED_FOR_SMS_OTP)
                  VALUES ('COMPUTER_OBJECT', 'Provision Computer', 'Y', 'Y', 'PROV_OBJECT', 'N', 'N', 'N');


INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_win02','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), 'active_dir_win02_managed_sys_id', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_win02','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), 'active_dir_win02_managed_sys_id', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_win02','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), 'active_dir_win02_managed_sys_id', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_0','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '0', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_0','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '0', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_0','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '0', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_101','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '101', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_101','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '101', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_101','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '101', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_103','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '103', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_103','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '103', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_103','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '103', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_104','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '104', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_104','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '104', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_104','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '104', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_105','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '105', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_105','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '105', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_105','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '105', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_106','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '106', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_106','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '106', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_106','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '106', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_113','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '113', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_113','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '113', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_113','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '113', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_110','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '110', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_110','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '110', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_110','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '110', 'COMPUTER_OBJECT');

INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('USER_POLICY_150','USER POLICY','Attribute policy for user provisioning', '1', CURDATE(), '150', 'USER_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('GROUP_POLICY_150','GROUP POLICY','Attribute policy for group provisioning', '1', CURDATE(), '150', 'GROUP_OBJECT');
INSERT INTO MNG_SYS_POLICY(MNG_SYS_POLICY_ID, NAME, DESCRIPTION, IS_PRIMARY, CREATE_DATE, MANAGED_SYS_ID, METADATA_TYPE_ID)
    VALUES ('COMPUTER_POLICY_150','COMPUTER POLICY','Attribute policy for computer provisioning', '1', CURDATE(), '150', 'COMPUTER_OBJECT');

ALTER TABLE ATTRIBUTE_MAP ADD COLUMN MNG_SYS_POLICY_ID varchar(32) NULL;
ALTER TABLE ATTRIBUTE_MAP ADD FOREIGN KEY (MNG_SYS_POLICY_ID) REFERENCES MNG_SYS_POLICY(MNG_SYS_POLICY_ID);

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_win02' WHERE MANAGED_SYS_ID = 'active_dir_win02_managed_sys_id' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='GROUP_POLICY_win02' WHERE MANAGED_SYS_ID = 'active_dir_win02_managed_sys_id' and (MAP_FOR_OBJECT_TYPE = 'GROUP_PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'GROUP');

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_0' WHERE MANAGED_SYS_ID = '0' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_101' WHERE MANAGED_SYS_ID = '101' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='GROUP_POLICY_101' WHERE MANAGED_SYS_ID = '101' and (MAP_FOR_OBJECT_TYPE = 'GROUP_PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'GROUP');

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_103' WHERE MANAGED_SYS_ID = '103' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_104' WHERE MANAGED_SYS_ID = '104' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_105' WHERE MANAGED_SYS_ID = '105' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_106' WHERE MANAGED_SYS_ID = '106' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_113' WHERE MANAGED_SYS_ID = '113' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='GROUP_POLICY_113' WHERE MANAGED_SYS_ID = '113' and (MAP_FOR_OBJECT_TYPE = 'GROUP_PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'GROUP');

UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='USER_POLICY_150' WHERE MANAGED_SYS_ID = '150' and (MAP_FOR_OBJECT_TYPE = 'PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'USER' or MAP_FOR_OBJECT_TYPE = 'EMAIL');
UPDATE ATTRIBUTE_MAP set MNG_SYS_POLICY_ID='GROUP_POLICY_150' WHERE MANAGED_SYS_ID = '150' and (MAP_FOR_OBJECT_TYPE = 'GROUP_PRINCIPAL' or MAP_FOR_OBJECT_TYPE = 'GROUP');

ALTER TABLE ATTRIBUTE_MAP DROP COLUMN MANAGED_SYS_ID;




