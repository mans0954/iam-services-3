use openiam;

START TRANSACTION;

DELIMITER $$

/*make group names unique*/
DROP PROCEDURE IF EXISTS makeGroupNamesUnique$$

CREATE PROCEDURE makeGroupNamesUnique()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE groupName, groupId VARCHAR(32);		
		DECLARE group_idx INT DEFAULT 1;
		DECLARE cur1 CURSOR FOR SELECT GRP_NAME, GRP_ID FROM GRP WHERE GRP_NAME IN ( SELECT GRP_NAME FROM GRP GROUP BY GRP_NAME HAVING count(GRP_NAME) > 1 );
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT 
			FETCH cur1 INTO groupName, groupId;
			IF ((groupName IS NOT NULL) AND (groupId IS NOT NULL)) THEN
				UPDATE GRP SET GRP_NAME=CONCAT(groupName, "_", group_idx) WHERE GRP_ID=groupId;
				SET group_idx = group_idx + 1;
			END IF;
		UNTIL done END REPEAT; 
				
		CLOSE cur1;
	END$$
DELIMITER ;

call makeGroupNamesUnique();

DROP PROCEDURE makeGroupNamesUnique;

DELIMITER $$

/*make role names unique*/
DROP PROCEDURE IF EXISTS makeResourceNamesUnique;

CREATE PROCEDURE makeResourceNamesUnique()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE resourceName, resourceId VARCHAR(32);		
		DECLARE res_idx INT DEFAULT 1;
		DECLARE cur1 CURSOR FOR SELECT NAME, RESOURCE_ID FROM RES WHERE NAME IN ( SELECT NAME FROM RES GROUP BY NAME HAVING count(NAME) > 1 );
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT 
			FETCH cur1 INTO resourceName, resourceId;
			IF ((resourceName IS NOT NULL) AND (resourceId IS NOT NULL)) THEN
				UPDATE RES SET NAME=CONCAT(resourceName, "_", res_idx) WHERE RESOURCE_ID=resourceId;
				SET res_idx = res_idx + 1;
			END IF;
		UNTIL done END REPEAT; 
				
		CLOSE cur1;
	END$$
DELIMITER ;

call makeResourceNamesUnique();

DROP PROCEDURE makeResourceNamesUnique;


ALTER TABLE GRP ADD UNIQUE(GRP_NAME);

ALTER TABLE RES ADD UNIQUE(NAME);

COMMIT;