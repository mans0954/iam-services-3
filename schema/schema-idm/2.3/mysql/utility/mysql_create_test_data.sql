DELIMITER $$

DROP PROCEDURE IF EXISTS createTestAuthorizationData$$

CREATE PROCEDURE createTestAuthorizationData(IN numUsers INT, IN numResources INT, IN numGroups INT, IN numRoles INT, IN numXrefs INT)
	BEGIN
		DECLARE cnt INT DEFAULT 0;
		DECLARE cnt_inner INT DEFAULT 0;
		DECLARE id VARCHAR(32);
		
		create_roles : LOOP
			IF (cnt >= numRoles) THEN
				LEAVE create_roles;
			END IF;
			SET id = concat("STRESS", cnt);
			INSERT INTO ROLE (ROLE_ID, ROLE_NAME, SERVICE_ID) VALUES(id, id, 'USR_SEC_DOMAIN');
			SET cnt = cnt + 1;
		END LOOP create_roles;
		
		SET cnt = 0;
		
		create_groups : LOOP
			IF (cnt >= numGroups) THEN
				LEAVE create_groups;
			END IF;
			SET id = concat("STRESS", cnt);
			INSERT INTO GRP (GRP_ID, GRP_NAME) VALUES(id, id);
			
			SET cnt_inner = 0;
			create_group_role_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_group_role_xref;
				END IF;
				INSERT INTO GRP_ROLE (GRP_ID, ROLE_ID) VALUES (id,CONCAT("STRESS", cnt_inner));
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_group_role_xref;
			
			SET cnt = cnt + 1;
		END LOOP create_groups;
		
		SET cnt = 0;
		
		create_resources : LOOP
			IF (cnt >= numResources) THEN
				LEAVE create_resources;
			END IF;
			SET id = concat("STRESS", cnt);
			INSERT INTO RES (RESOURCE_ID, NAME) VALUES(id, id);
			
			SET cnt_inner = 0;
			create_resource_group_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_resource_group_xref;
				END IF;
				INSERT INTO RESOURCE_GROUP (RES_GROUP_ID, RESOURCE_ID, GRP_ID) VALUES(CONCAT('STRESS_', cnt, '_', cnt_inner), id, CONCAT('STRESS', cnt_inner));
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_resource_group_xref;
			
			SET cnt_inner = 0;
			create_resource_role_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_resource_role_xref;
				END IF;
				INSERT INTO RESOURCE_ROLE (RESOURCE_ID, ROLE_ID) VALUES(id,CONCAT('STRESS', cnt_inner));
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_resource_role_xref;
			
			SET cnt = cnt + 1;
		END LOOP create_resources;
		
		SET cnt = 0;
		
		create_users : LOOP
			IF (cnt >= numUsers) THEN
				LEAVE create_users;
			END IF;
			
			SET id = concat("STRESS", cnt);
			INSERT INTO USERS (USER_ID, FIRST_NAME, LAST_NAME) VALUES(id, id, id);
			INSERT INTO LOGIN (SERVICE_ID, LOGIN, MANAGED_SYS_ID, USER_ID) VALUES('USR_SEC_DOMAIN', id, '0', id);
			
			IF(MOD(cnt, 5) = 0) THEN
				UPDATE LOGIN SET LAST_LOGIN=CURRENT_TIMESTAMP WHERE USER_ID=id;
			END IF;
			
			
			SET cnt_inner = 0;
			create_user_group_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_user_group_xref;
				END IF;
				INSERT INTO USER_GRP (USER_GRP_ID, GRP_ID, USER_ID) VALUES (CONCAT('STRESS_', cnt, '_', cnt_inner) , CONCAT('STRESS', cnt_inner), id);
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_user_group_xref;
			
			SET cnt_inner = 0;
			create_user_role_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_user_role_xref;
				END IF;
				INSERT INTO USER_ROLE (USER_ROLE_ID, ROLE_ID, USER_ID) VALUES (CONCAT('STRESS_', cnt, '_', cnt_inner) , CONCAT('STRESS', cnt_inner), id);
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_user_role_xref;
			
			SET cnt_inner = 0;
			create_user_resoruce_xref : LOOP
				IF (cnt_inner >= numXrefs) THEN
					LEAVE create_user_resoruce_xref;
				END IF;
				INSERT INTO RESOURCE_USER (RESOURCE_USER_ID, RESOURCE_ID, USER_ID) VALUES (CONCAT('STRESS_', cnt, '_', cnt_inner) , CONCAT('STRESS', cnt_inner), id);
				SET cnt_inner = cnt_inner + 1;
			END LOOP create_user_resoruce_xref;
			
			SET cnt = cnt + 1;
		END LOOP create_users;
		
		SET cnt = 0;
	END$$
DELIMITER ;
	
call createTestAuthorizationData(30000, 5000, 500, 1000, 20);
	

DELETE FROM USER_GRP WHERE USER_ID LIKE '%STRESS%';
DELETE FROM USER_ROLE WHERE USER_ID LIKE '%STRESS%';
DELETE FROM RESOURCE_USER WHERE USER_ID LIKE '%STRESS%';
DELETE FROM RESOURCE_ROLE WHERE RESOURCE_ID LIKE '%STRESS%';
DELETE FROM RESOURCE_GROUP WHERE RESOURCE_ID LIKE '%STRESS%';
DELETE FROM GRP_ROLE WHERE ROLE_ID LIKE '%STRESS%';
DELETE FROM ROLE WHERE ROLE_ID LIKE '%STRESS%';
DELETE FROM GRP WHERE GRP_ID LIKE '%STRESS%';
DELETE FROM RES WHERE RESOURCE_ID LIKE '%STRESS%';
DELETE FROM LOGIN WHERE USER_ID LIKE '%STRESS%';
DELETE FROM USERS WHERE USER_ID LIKE '%STRESS%';