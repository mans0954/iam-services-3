CREATE TABLE grp_to_grp_membership (
	GROUP_ID varchar(32) NOT NULL,
	MEMBER_GROUP_ID varchar(32) NOT NULL,
	CREATE_DATE TIMESTAMP NULL,
	UPDATE_DATE TIMESTAMP NULL,
	CREATED_BY varchar(32) NULL,
	UPDATED_BY varchar(32) NULL,
	UNIQUE(GROUP_ID, MEMBER_GROUP_ID),
	FOREIGN KEY (GROUP_ID) REFERENCES GRP (GRP_ID ),
	FOREIGN KEY (MEMBER_GROUP_ID) REFERENCES GRP (GRP_ID )
)  Engine=InnoDB;

CREATE TRIGGER grp_grp_mem_insert 
BEFORE 
INSERT ON grp_to_grp_membership 
	FOR EACH ROW
	BEGIN
		SET NEW.CREATE_DATE = NOW();
		SET NEW.UPDATE_DATE = NOW();
	END

DROP PROCEDURE IF EXISTS openiam.migrateGroups;

CREATE PROCEDURE openiam.migrateGroups()
	BEGIN
		DECLARE done INT DEFAULT FALSE;
		DECLARE id, parent VARCHAR(32);		
		DECLARE cur1 CURSOR FOR (SELECT GRP_ID, PARENT_GRP_ID FROM openiam.grp WHERE PARENT_GRP_ID IS NOT null AND INHERIT_FROM_PARENT=1);
		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
		OPEN cur1;
		
		REPEAT 
			FETCH cur1 INTO id, parent;
			INSERT INTO test (val1, val2) VALUES(id, parent);
			IF (id IS NOT NULL AND parent IS NOT NULL) THEN
				IF ((SELECT GROUP_ID FROM openiam.grp_to_grp_membership WHERE GROUP_ID=parent AND MEMBER_GROUP_ID=id) IS NULL) THEN
					INSERT INTO openiam.grp_to_grp_membership (GROUP_ID, MEMBER_GROUP_ID) VALUES(parent, id);
				END IF;
			END IF;
		UNTIL done END REPEAT; 
				
		
		CLOSE cur1;
	END;

call openiam.migrateGroups();

DROP PROCEDURE openiam.migrateGroups;