use openiam;	

DELIMITER $$

DROP PROCEDURE IF EXISTS METADATA_SCHEMA_CHANGE$$

CREATE PROCEDURE METADATA_SCHEMA_CHANGE()
	BEGIN
		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'DEFAULT_VALUE_ID') THEN
			ALTER TABLE METADATA_ELEMENT DROP FOREIGN KEY METADATA_ELEMENT_DEFAULT_VALUE_FK;
			ALTER TABLE METADATA_ELEMENT DROP COLUMN DEFAULT_VALUE_ID;
 		END IF;
 		
		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'TEMPLATE_ID') THEN
			ALTER TABLE METADATA_ELEMENT DROP FOREIGN KEY METADATA_ELEMENT_TEMPLATE_FK;
			ALTER TABLE METADATA_ELEMENT DROP COLUMN TEMPLATE_ID;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'RESOURCE_ID') THEN
 			ALTER TABLE METADATA_ELEMENT DROP FOREIGN KEY METADATA_ELEMENT_RES_FK;
			ALTER TABLE METADATA_ELEMENT DROP COLUMN RESOURCE_ID;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'DISPLAY_NAME') THEN
 			ALTER TABLE METADATA_ELEMENT DROP FOREIGN KEY METADATA_ELEMENT_DISPLAY_NAME_FK;
			ALTER TABLE METADATA_ELEMENT DROP COLUMN DISPLAY_NAME;
 		END IF;
 		
 		 IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'DISPLAY_ORDER') THEN
			ALTER TABLE METADATA_ELEMENT DROP COLUMN DISPLAY_ORDER;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'VALIDATOR') THEN
			ALTER TABLE METADATA_ELEMENT DROP COLUMN VALIDATOR;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'STATIC_DEFAULT_VALUE') THEN
 			ALTER TABLE METADATA_ELEMENT DROP COLUMN STATIC_DEFAULT_VALUE;
 		END IF;

 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'DISPLAY_ORDER') THEN
 			ALTER TABLE METADATA_ELEMENT DROP COLUMN DISPLAY_ORDER;
 		END IF;

 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_ELEMENT' AND column_name = 'IS_PUBLIC') THEN
 			ALTER TABLE METADATA_ELEMENT DROP COLUMN IS_PUBLIC;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'METADATA_TYPE' AND column_name = 'IS_PUBLIC') THEN
 			ALTER TABLE METADATA_TYPE DROP COLUMN IS_PUBLIC;
 		END IF;
 		
 		IF EXISTS (SELECT * FROM information_schema.columns WHERE table_name = 'USER_ATTRIBUTES' AND column_name = 'METADATA_ELEMENT_ID') THEN
 			DELETE FROM USER_ATTRIBUTES WHERE METADATA_ELEMENT_ID IS NOT NULL;
 			ALTER TABLE USER_ATTRIBUTES DROP FOREIGN KEY USER_ATTR_METADATA_ELEMENT_FK;
 			ALTER TABLE USER_ATTRIBUTES DROP COLUMN METADATA_ELEMENT_ID;
 		END IF;
	END$$
DELIMITER ;

call METADATA_SCHEMA_CHANGE();

DROP PROCEDURE METADATA_SCHEMA_CHANGE;

DROP TABLE IF EXISTS METADATA_ELEMENT_PAGE_TEMPLATE_XREF;
DROP TABLE IF EXISTS METADATA_ELEMENT_DEFAULT_VALUES;
DROP TABLE IF EXISTS METADATA_ELEMENT_VALID_VALUES;
DROP TABLE IF EXISTS METADATA_ELEMENT_PAGE_TEMPLATE;
DROP TABLE IF EXISTS LANGUAGE_MAPPING;

CREATE TABLE LANGUAGE_MAPPING (
  ID varchar(32) NOT NULL,
  LANGUAGE_ID varchar(32) NOT NULL,
  REFERENCE_ID varchar(32) NOT NULL,
  REFERENCE_TYPE varchar(100) NOT NULL,
  TEXT_VALUE varchar(400) NOT NULL,
  PRIMARY KEY(ID),
  CONSTRAINT LANGUAGE_MAPPING_LANGUAGE_FK FOREIGN KEY (LANGUAGE_ID) REFERENCES LANGUAGE(ID),
  CONSTRAINT LANGUAGE_MAPPING_UNIQUE UNIQUE(LANGUAGE_ID, REFERENCE_ID, REFERENCE_TYPE)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE METADATA_ELEMENT_PAGE_TEMPLATE (
  ID varchar(32) NOT NULL,
  NAME varchar(40) NOT NULL,
  RESOURCE_ID varchar(32) NOT NULL,
  IS_PUBLIC CHAR(1) NOT NULL DEFAULT 'Y',
  PRIMARY KEY(ID),
  CONSTRAINT METADATA_ELEMENT_PAGE_TEMPLATE_UNIQUE UNIQUE(NAME),
  CONSTRAINT METADATA_PAGE_TEMPLATE_RES_FK FOREIGN KEY (RESOURCE_ID) REFERENCES RES(RESOURCE_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE METADATA_ELEMENT_VALID_VALUES (
  ID varchar(32) NOT NULL,
  METADATA_ELEMENT_ID varchar(32) NOT NULL,
  UI_VALUE varchar(200) NULL NOT NULL,
  PRIMARY KEY(ID),
  CONSTRAINT METADATA_FIELD_VALID_VALUES_ELEMENT_FK FOREIGN KEY (METADATA_ELEMENT_ID) REFERENCES METADATA_ELEMENT(METADATA_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE  TABLE METADATA_ELEMENT_PAGE_TEMPLATE_XREF(
  TEMPLATE_ID VARCHAR(32) NOT NULL,
  METADATA_ELEMENT_ID VARCHAR(32) NOT NULL,
  DISPLAY_ORDER INT(11) NOT NULL,
  PRIMARY KEY (TEMPLATE_ID, METADATA_ELEMENT_ID),
  CONSTRAINT METADATA_ELEMENT_PAGE_TEMPLATE_ELEMENT_FK
    FOREIGN KEY (METADATA_ELEMENT_ID) REFERENCES METADATA_ELEMENT(METADATA_ID),
  CONSTRAINT METADATA_ELEMENT_PAGE_TEMPLATE_FK
    FOREIGN KEY (TEMPLATE_ID) REFERENCES METADATA_ELEMENT_PAGE_TEMPLATE(ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE METADATA_ELEMENT ADD TEMPLATE_ID VARCHAR(32) NULL;
ALTER TABLE METADATA_ELEMENT ADD CONSTRAINT METADATA_ELEMENT_TEMPLATE_FK FOREIGN KEY (TEMPLATE_ID) REFERENCES METADATA_ELEMENT_PAGE_TEMPLATE(ID);

ALTER TABLE METADATA_ELEMENT ADD RESOURCE_ID VARCHAR(32) NULL;
ALTER TABLE METADATA_ELEMENT ADD CONSTRAINT METADATA_ELEMENT_RES_FK FOREIGN KEY (RESOURCE_ID) REFERENCES RES(RESOURCE_ID);

ALTER TABLE METADATA_ELEMENT ADD VALIDATOR VARCHAR(150) NULL;

ALTER TABLE METADATA_ELEMENT ADD STATIC_DEFAULT_VALUE VARCHAR(400) NULL;

ALTER TABLE USER_ATTRIBUTES MODIFY METADATA_ID VARCHAR(32) NULL;

ALTER TABLE METADATA_ELEMENT ADD IS_PUBLIC CHAR(1) NOT NULL DEFAULT 'Y';