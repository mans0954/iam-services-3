use openiam;

START TRANSACTION;

/*DROP all references to the Role Table*/

ALTER TABLE grp_role DROP FOREIGN KEY FK_GRP_ROLE_ROLE;

ALTER TABLE grp_role DROP INDEX FK_GRP_ROLE_ROLE;

ALTER TABLE grp_role DROP PRIMARY KEY;

ALTER TABLE resource_role DROP FOREIGN KEY FK_RESOURCE_ROLE_ROLE;

ALTER TABLE resource_role DROP INDEX FK_RESOURCE_ROLE_ROLE;

ALTER TABLE resource_role DROP FOREIGN KEY FK_RESOURCE_ROLE_RESOURCE;

ALTER TABLE resource_role DROP PRIMARY KEY;

ALTER TABLE resource_role ADD CONSTRAINT FK_RESOURCE_ROLE_RESOURCE FOREIGN KEY (RESOURCE_ID) REFERENCES RES(RESOURCE_ID);

ALTER TABLE role_attribute DROP FOREIGN KEY FK_ROLE_ROLE_ATTRIBUTE;

ALTER TABLE role_attribute DROP INDEX FK_ROLE_ROLE_ATTRIBUTE;

ALTER TABLE user_role DROP FOREIGN KEY FK_USR_ROLE_ROLE;

ALTER TABLE user_role DROP INDEX FK_USR_ROLE_ROLE;

ALTER TABLE user_role DROP PRIMARY KEY;

ALTER TABLE role_policy DROP FOREIGN KEY role_policy_ibfk_1; # this might break

ALTER TABLE role_policy DROP INDEX ROLE_ID;

DROP TABLE role_entitlement;

DROP VIEW user_role_vw;

ALTER TABLE resource_policy DROP FOREIGN KEY RS_PL_RL_RLID;

ALTER TABLE resource_policy DROP KEY RS_PL_RL_RLID;

ALTER TABLE permissions DROP PRIMARY KEY;

ALTER TABLE role DROP FOREIGN KEY FK_ROLE_SERVICE;

ALTER TABLE role DROP INDEX FK_ROLE_SERVICE;

ALTER TABLE role DROP PRIMARY KEY;

ALTER TABLE role ADD CONSTRAINT FK_ROLE_SERVICE FOREIGN KEY (SERVICE_ID) REFERENCES SECURITY_DOMAIN(DOMAIN_ID);

/*Add the new ID, and populate it via stored procedure (no better way to do this)*/
ALTER TABLE role ADD TEMP_ROLE_ID varchar(32) NULL;

DELIMITER $$

DROP PROCEDURE IF EXISTS generateSimpleRoleId$$

CREATE PROCEDURE generateSimpleRoleId()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE role, service, roleId VARCHAR(32);		
		DECLARE newId INT DEFAULT 1;
		DECLARE cur1 CURSOR FOR (SELECT ROLE_ID, SERVICE_ID, TEMP_ROLE_ID FROM ROLE);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT 
			FETCH cur1 INTO role, service, roleId;
			IF (roleId IS NULL) THEN
				UPDATE ROLE SET TEMP_ROLE_ID=newId WHERE SERVICE_ID=service AND ROLE_ID=role;
				SET newId = newId + 1;
			END IF;
		UNTIL done END REPEAT; 
				
		
		CLOSE cur1;
	END$$
DELIMITER ;

call generateSimpleRoleId();

DROP PROCEDURE generateSimpleRoleId;

/*create the reference column on all tables referencing ROLE, and populate it*/
ALTER TABLE grp_role ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE grp_role r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE resource_role ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE resource_role r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE role_attribute ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE role_attribute r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE user_role ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE user_role r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE role_policy ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE role_policy r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE resource_policy ADD TEMP_ROLE_ID varchar(32) NULL;

UPDATE resource_policy r SET TEMP_ROLE_ID= (
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

ALTER TABLE permissions ADD COLUMN TEMP_ROLE_ID varchar(32);

UPDATE permissions r SET TEMP_ROLE_ID=(
	SELECT TEMP_ROLE_ID FROM role WHERE ROLE_ID=r.ROLE_ID AND SERVICE_ID=r.SERVICE_ID
);

/*drop the old column that referenced the composite key*/
ALTER TABLE grp_role DROP COLUMN SERVICE_ID;

ALTER TABLE grp_role DROP COLUMN ROLE_ID;

ALTER TABLE resource_role DROP COLUMN SERVICE_ID;

ALTER TABLE resource_role DROP COLUMN ROLE_ID;

ALTER TABLE role_attribute DROP COLUMN SERVICE_ID;

ALTER TABLE role_attribute DROP COLUMN ROLE_ID;

ALTER TABLE user_role DROP COLUMN SERVICE_ID;

ALTER TABLE user_role DROP COLUMN ROLE_ID;

ALTER TABLE role_policy DROP COLUMN SERVICE_ID;

ALTER TABLE role_policy DROP COLUMN ROLE_ID;

ALTER TABLE resource_policy DROP COLUMN SERVICE_ID;

ALTER TABLE resource_policy DROP COLUMN ROLE_ID;

ALTER TABLE permissions DROP COLUMN SERVICE_ID;

ALTER TABLE permissions DROP COLUMN ROLE_ID;

ALTER TABLE role DROP COLUMN ROLE_ID;

/*rename the TEMP_ROLE_ID columns to be ROLE_ID NOT NULL*/
alter table grp_role CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table resource_role CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table role_attribute CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table user_role CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table role_policy CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table resource_policy CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

alter table role CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

/* it is possible that permissions records did not map to an existing Role or Menu - delete those */
DELETE FROM permissions WHERE TEMP_ROLE_ID IS NULL OR TEMP_ROLE_ID='';
DELETE FROM permissions WHERE MENU_ID NOT IN (
	SELECT MENU_ID FROM menu
);

alter table permissions CHANGE TEMP_ROLE_ID ROLE_ID varchar(32) NOT NULL;

/*make ROLE_ID primary on ROLE*/
ALTER TABLE role ADD PRIMARY KEY (ROLE_ID);

/*add foreign keys to the new column*/
ALTER TABLE grp_role ADD CONSTRAINT FK_GRP_ROLE_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE resource_role ADD CONSTRAINT FK_RESOURCE_ROLE_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE role_attribute ADD CONSTRAINT FK_ROLE_ROLE_ATTRIBUTE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE user_role ADD CONSTRAINT FK_USR_ROLE_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE role_policy ADD CONSTRAINT FK_ROLE_POLICY_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE resource_policy ADD CONSTRAINT FK_RESOURCE_POLICY_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE permissions ADD CONSTRAINT FK_PERMISSIONS_ROLE FOREIGN KEY(role_id) REFERENCES role(role_id);

ALTER TABLE permissions ADD CONSTRAINT FK_PERMISSIONS_MENU FOREIGN KEY(menu_id) REFERENCES menu(menu_id);

/*recreate primary keys*/
ALTER TABLE grp_role ADD PRIMARY KEY (GRP_ID, ROLE_ID);

ALTER TABLE resource_role ADD PRIMARY KEY (RESOURCE_ID, ROLE_ID, PRIVILEGE_ID);

ALTER TABLE user_role add PRIMARY KEY (ROLE_ID, USER_ID);

ALTER TABLE permissions ADD PRIMARY KEY (MENU_ID, ROLE_ID);

/*make the role names unique, if not already*/
DELIMITER $$

DROP PROCEDURE IF EXISTS makeRoleNamesUnique$$

CREATE PROCEDURE makeRoleNamesUnique()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE id VARCHAR(32);
		DECLARE name VARCHAR(80);
		DECLARE service VARCHAR(20);
		DECLARE newId INT DEFAULT 1;
		DECLARE cur1 CURSOR FOR (SELECT ROLE_ID, SERVICE_ID, ROLE_NAME FROM ROLE);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT 
			FETCH cur1 INTO id, service, name;
			IF ((SELECT count(*) FROM ROLE WHERE ROLE_NAME=name) > 1) THEN
				UPDATE ROLE SET ROLE_NAME= CONCAT(name, '_',service) WHERE ROLE_ID=id;
			END IF;
		UNTIL done END REPEAT; 
				
		
		CLOSE cur1;
	END$$
DELIMITER ;

call makeRoleNamesUnique();

DROP PROCEDURE makeRoleNamesUnique;

/*add the unique constraint*/
ALTER TABLE ROLE ADD UNIQUE(ROLE_NAME);

COMMIT;