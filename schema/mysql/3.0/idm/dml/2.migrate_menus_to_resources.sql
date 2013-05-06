use openiam;

INSERT INTO RESOURCE_TYPE (RESOURCE_TYPE_ID, DESCRIPTION, METADATA_TYPE_ID) VALUES('MENU_ITEM', 'Menus', 'MENU_ITEM');

START TRANSACTION;

DELIMITER $$

DROP PROCEDURE IF EXISTS copyMenuDataIntoResources$$

CREATE PROCEDURE copyMenuDataIntoResources()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE resourceId, parentResourceId VARCHAR(32);
		DECLARE menuId, menuGroup, menuName, menuDescription, menuUrl VARCHAR(32);		
		DECLARE displayOrder INT DEFAULT null;
		DECLARE cur2 CURSOR FOR (SELECT MENU_ID, MENU_GROUP, MENU_NAME, MENU_DESC, URL, DISPLAY_ORDER FROM MENU WHERE MENU_ID IN(
			SELECT MENU_GROUP FROM MENU WHERE MENU_GROUP IS NOT NULL
		));
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur2;
		
		REPEAT
			FETCH cur2 INTO menuId, menuGroup, menuName, menuDescription, menuUrl, displayOrder;
			SET resourceId = CONCAT(menuId, '');
			IF ((SELECT RESOURCE_ID FROM RES WHERE RESOURCE_ID=resourceId) IS NULL) THEN
				INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, DESCRIPTION, NAME, URL, IS_PUBLIC, DISPLAY_ORDER) VALUES(resourceId, 'MENU_ITEM', menuDescription, resourceId, menuUrl, 'N', displayOrder);
				INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES (CONCAT(resourceId, '_MENU_DISPLAY'), resourceId, 'MENU_DISPLAY_NAME', menuName);
			END IF;
		UNTIL done END REPEAT;
		
		CLOSE cur2;
	END$$
DELIMITER ;

call copyMenuDataIntoResources();

DROP PROCEDURE copyMenuDataIntoResources;

DELIMITER $$

DROP PROCEDURE IF EXISTS copyMenuDataIntoResources2$$

CREATE PROCEDURE copyMenuDataIntoResources2()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE resourceId, parentResourceId VARCHAR(32);
		DECLARE menuId, menuGroup, menuName, menuDescription, menuUrl VARCHAR(32);
		DECLARE newResourceName VARCHAR(32);
		DECLARE displayOrder INT DEFAULT null;
		DECLARE name_idx INT DEFAULT 0;
		DECLARE cur1 CURSOR FOR (SELECT MENU_ID, MENU_GROUP, MENU_NAME, MENU_DESC, URL, DISPLAY_ORDER FROM MENU);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT
			FETCH cur1 INTO menuId, menuGroup, menuName, menuDescription, menuUrl, displayOrder;
			SET resourceId = CONCAT(menuId, '');
			SET parentResourceId = CONCAT(menuGroup, '');
			SET newResourceName = menuName;
			
			IF((SELECT NAME FROM RES WHERE NAME=menuName) IS NOT NULL) THEN
				SET newResourceName = concat(menuName, '_', name_idx);
				SET name_idx = name_idx + 1;
			END IF;
			
			IF ((SELECT RESOURCE_ID FROM RES WHERE RESOURCE_ID=resourceId) IS NULL) THEN
				INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, DESCRIPTION, NAME, URL, IS_PUBLIC, DISPLAY_ORDER) VALUES(resourceId, 'MENU_ITEM', menuDescription, resourceId, menuUrl, 'N', displayOrder);
				INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES (CONCAT(resourceId, '_MENU_DISPLAY'), resourceId, 'MENU_DISPLAY_NAME', menuName);
			END IF;
			
			IF(parentResourceId IS NOT NULL AND resourceId IS NOT NULL) THEN
				IF((SELECT RESOURCE_ID FROM res_to_res_membership WHERE RESOURCE_ID=parentResourceId AND MEMBER_RESOURCE_ID=resourceId) IS NULL) THEN
					INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES(parentResourceId, resourceId);
				END IF;
			END IF;
		UNTIL done END REPEAT; 
		
		CLOSE cur1;
	END$$
DELIMITER ;

call copyMenuDataIntoResources2();

DROP PROCEDURE copyMenuDataIntoResources2;

DELIMITER $$

DROP PROCEDURE IF EXISTS migratePermissions$$

CREATE PROCEDURE migratePermissions()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE menuId, roleId, resourceId VARCHAR(32);
		DECLARE cur1 CURSOR FOR (SELECT MENU_ID, ROLE_ID FROM PERMISSIONS);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT
			FETCH cur1 INTO menuId, roleId;
			SET resourceId = CONCAT(menuId, '');
			IF ( (SELECT RESOURCE_ID FROM RESOURCE_ROLE WHERE RESOURCE_ID=resourceId AND ROLE_ID=roleId) IS NULL) THEN
				INSERT INTO RESOURCE_ROLE (RESOURCE_ID, ROLE_ID) VALUES(resourceId, roleId);
			END IF;
		UNTIL done END REPEAT; 
		
		CLOSE cur1;
	END$$
DELIMITER ;

call migratePermissions();

DROP PROCEDURE migratePermissions;

DELETE FROM res_to_res_membership WHERE RESOURCE_ID IN('ROOT');
DELETE FROM RESOURCE_PROP WHERE RESOURCE_ID IN('ROOT');
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID IN('ROOT');
DELETE FROM RES WHERE RESOURCE_ID IN('ROOT');

INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES (CONCAT('IDM', '_MENU_PUBLIC'), 'IDM', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES (CONCAT('SELFSERVICE', '_MENU_PUBLIC'), 'SELFSERVICE', 'MENU_IS_PUBLIC', 'true');
