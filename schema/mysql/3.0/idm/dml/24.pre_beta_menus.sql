use openiam;

UPDATE RES SET URL='javascript:void(0);' WHERE RESOURCE_ID IN('IDM', 'SELFSERVICE', 'ACC_CONTROL', 'ADMIN');
UPDATE RES SET IS_PUBLIC='Y' WHERE RESOURCE_ID IN('SELFCENTER', 'CHNGPSWD', 'IDQUEST', 'PROFILE', 'SELF_USERIDENTITY', 'IDM');
UPDATE RES SET URL='/webconsole/users.html' WHERE RESOURCE_ID='IDMAN';
UPDATE RES SET URL='/selfservice/editProfile.html' WHERE RESOURCE_ID='PROFILE';
UPDATE RES SET URL='/webconsole/users.html' WHERE RESOURCE_ID='SELF_QUERYUSER';

DELETE FROM res_to_res_membership WHERE MEMBER_RESOURCE_ID='SELF_USERIDENTITY';
INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES('SELFCENTER', 'SELF_USERIDENTITY');

DELETE FROM res_to_res_membership WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM res_to_res_membership WHERE MEMBER_RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM RESOURCE_USER WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM RESOURCE_PROP WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM RESOURCE_GROUP WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');
DELETE FROM RES WHERE RESOURCE_ID IN('SELF_USERSUMMARY', 'SELF_USERGROUP', 'SELF_USERROLE', 'SELF_USERPSWDRESET');

DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='ACCESSCENTER' AND ROLE_ID='1';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='REQINBOX' AND ROLE_ID='1';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='MANAGEREQ' AND ROLE_ID='1';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER' AND ROLE_ID='1';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER-NOAPPRV' AND ROLE_ID='1';

DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='CREATEREQ' AND ROLE_ID='2';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='MANAGEREQ' AND ROLE_ID='2';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER' AND ROLE_ID='2';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER-NOAPPRV' AND ROLE_ID='2';

DELIMITER $$

DROP PROCEDURE IF EXISTS hideBetaMenus$$
DROP PROCEDURE IF EXISTS updateIcons$$
DROP PROCEDURE IF EXISTS updateMenuMembership$$

CREATE PROCEDURE hideBetaMenus()
	BEGIN
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='DIRECTORY' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='DIRECTORY' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('DIRECTORY_VISIBLE', 'DIRECTORY', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='REQINBOX' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='REQINBOX' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('REQINBOX_VISIBLE', 'REQINBOX', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='CREATEREQ' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='CREATEREQ' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('CREATEREQ_VISIBLE', 'CREATEREQ', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='NEWUSER' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='NEWUSER' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('NEWUSER_VISIBLE', 'NEWUSER', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='MANAGEREQ' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='MANAGEREQ' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('MANAGEREQ_VISIBLE', 'MANAGEREQ', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='CREATEREQ' AND NAME='MENU_DISPLAY_NAME') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='Create Request' WHERE  RESOURCE_ID='CREATEREQ' AND NAME='MENU_DISPLAY_NAME';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('MANAGEREQ_DISPLAY_NAME', 'CREATEREQ', 'MENU_DISPLAY_NAME', 'Create Request');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='REPORT' AND NAME='MENU_DISPLAY_NAME') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='Manage Reports' WHERE  RESOURCE_ID='REPORT' AND NAME='MENU_DISPLAY_NAME';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('REPORT_DISPLAY_NAME', 'REPORT', 'MENU_DISPLAY_NAME', 'Manage Reports');
		END IF;
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='REPORT' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='REPORT' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('REPORT_VISIBLE', 'REPORT', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='USER_BULK' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='USER_BULK' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('USER_BULK_VISIBLE', 'USER_BULK', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='PROFILE' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='true' WHERE  RESOURCE_ID='PROFILE' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('PROFILE_VISIBLE', 'PROFILE', 'IS_VISIBLE', 'true');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SELF_USERIDENTITY' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='true' WHERE  RESOURCE_ID='SELF_USERIDENTITY' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SELF_USERIDENTITY_VISIBLE', 'SELF_USERIDENTITY', 'IS_VISIBLE', 'true');
		END IF;
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SECDOMAIN' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='SECDOMAIN' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SECDOMAIN_VISIBLE', 'SECDOMAIN', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='LOCATION' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='LOCATION' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('LOCATION_VISIBLE', 'LOCATION', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='CHALLENGE' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='CHALLENGE' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('CHALLENGE_VISIBLE', 'CHALLENGE', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='BATCH_PROC' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='BATCH_PROC' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('BATCH_PROC_VISIBLE', 'BATCH_PROC', 'IS_VISIBLE', 'false');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='METADATA' AND NAME='IS_VISIBLE') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='false' WHERE  RESOURCE_ID='METADATA' AND NAME='IS_VISIBLE';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('METADATA_VISIBLE', 'METADATA', 'IS_VISIBLE', 'false');
		END IF;
	END$$

CREATE PROCEDURE updateIcons()
	BEGIN
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SELFSERVICE_MYINFO' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/menu7.png' WHERE  RESOURCE_ID='SELFSERVICE_MYINFO' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SELFSERVICE_MYINFO_MENU_ICON', 'SELFSERVICE_MYINFO', 'MENU_ICON', '/openiam-ui-static/images/common/icons/menu7.png');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='ACCESSCENTER' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/access_control.png' WHERE  RESOURCE_ID='ACCESSCENTER' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('ACCESSCENTER_MENU_ICON', 'ACCESSCENTER', 'MENU_ICON', '/openiam-ui-static/images/common/icons/access_control.png');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SELFSERVICE_MYAPPS' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/sso.png' WHERE  RESOURCE_ID='SELFSERVICE_MYAPPS' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SELFSERVICE_MYAPPS_MENU_ICON', 'SELFSERVICE_MYAPPS', 'MENU_ICON', '/openiam-ui-static/images/common/icons/sso.png');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SELFCENTER' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/user_manager.png' WHERE  RESOURCE_ID='SELFCENTER' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SELFCENTER_MENU_ICON', 'SELFCENTER', 'MENU_ICON', '/openiam-ui-static/images/common/icons/user_manager.png');
		END IF;
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='IDMAN' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/user_manager.png' WHERE  RESOURCE_ID='IDMAN' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('IDMAN_MENU_ICON', 'IDMAN', 'MENU_ICON', '/openiam-ui-static/images/common/icons/user_manager.png');
		END IF;
		
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='ACC_CONTROL' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/access_control.png' WHERE  RESOURCE_ID='ACC_CONTROL' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('ACC_CONTROL_MENU_ICON', 'ACC_CONTROL', 'MENU_ICON', '/openiam-ui-static/images/common/icons/access_control.png');
		END IF;
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='PROVISIONING' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/menu7.png' WHERE  RESOURCE_ID='PROVISIONING' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('PROVISIONING_MENU_ICON', 'PROVISIONING', 'MENU_ICON', '/openiam-ui-static/images/common/icons/menu7.png');
		END IF;
		
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='ADMIN' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/admin.png' WHERE  RESOURCE_ID='ADMIN' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('ADMIN_MENU_ICON', 'ADMIN', 'MENU_ICON', '/openiam-ui-static/images/common/icons/admin.png');
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_PROP WHERE RESOURCE_ID='SECURITY_POLICY' AND NAME='MENU_ICON') >= 1) THEN
			UPDATE RESOURCE_PROP SET PROP_VALUE='/openiam-ui-static/images/common/icons/sso.png' WHERE  RESOURCE_ID='SECURITY_POLICY' AND NAME='MENU_ICON';
		ELSE
			INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES('SECURITY_POLICY_MENU_ICON', 'SECURITY_POLICY', 'MENU_ICON', '/openiam-ui-static/images/common/icons/sso.png');
		END IF;
	END$$

	
CREATE PROCEDURE updateMenuMembership()
	BEGIN
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='SELF_USERIDENTITY' AND ROLE_ID='1') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='1') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('1', 'SELF_USERIDENTITY');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='ACCESSCENTER' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'ACCESSCENTER');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='REQINBOX' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'REQINBOX');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='MANAGEREQ' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'MANAGEREQ');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'NEWUSER');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='NEWUSER-NOAPPRV' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'NEWUSER-NOAPPRV');
			END IF;
		END IF;
		
		IF ((SELECT count(*) FROM RESOURCE_ROLE WHERE RESOURCE_ID='CREATEREQ' AND ROLE_ID='4') = 0) THEN
			IF((SELECT count(*) FROM ROLE WHERE ROLE_ID='4') >= 1) THEN
				INSERT INTO RESOURCE_ROLE (ROLE_ID, RESOURCE_ID) VALUES('4', 'CREATEREQ');
			END IF;
		END IF;
	END$$

DELIMITER ;

call hideBetaMenus();
call updateIcons();
call updateMenuMembership();

DROP PROCEDURE updateIcons;
DROP PROCEDURE hideBetaMenus;
DROP PROCEDURE updateMenuMembership;