use openiam;

DROP TABLE IF EXISTS URI_META_VALUE_XREF;
DROP TABLE IF EXISTS URI_PATTERN_META_VALUE;
DROP TABLE IF EXISTS URI_PATTERN_META;
DROP TABLE IF EXISTS URI_PATTERN_META_TYPE;
DROP TABLE IF EXISTS URI_PATTERN;
DROP TABLE IF EXISTS CONTENT_PROVIDER_SERVER;
DROP TABLE IF EXISTS CONTENT_PROVIDER;
DROP TABLE IF EXISTS AUTH_LEVEL;


CREATE TABLE AUTH_LEVEL (
	AUTH_LEVEL_ID varchar(32) NOT NULL,
	AUTH_LEVEL_NAME varchar(100) NOT NULL,
	LEVEL int(3) NOT NULL,
	primary key(AUTH_LEVEL_ID),
	CONSTRAINT UNIQUE_AUTH_LEVEL_NAME  UNIQUE(AUTH_LEVEL_NAME),
	CONSTRAINT UNIQUE_AUTH_LEVEL_LEVEL UNIQUE(LEVEL)
)ENGINE=InnoDB;

CREATE TABLE CONTENT_PROVIDER (
	CONTENT_PROVIDER_ID varchar(32) NOT NULL,
	CONTENT_PROVIDER_NAME varchar(100) NOT NULL,
	IS_PUBLIC char(1) NOT NULL DEFAULT 'N',
	MIN_AUTH_LEVEL varchar(32) NOT NULL,
	DOMAIN_PATTERN varchar(100) NOT NULL,
	IS_SSL char(1) NULL, /* NULL means don't care */
	RESOURCE_ID varchar(32) NOT NULL,
	primary key(CONTENT_PROVIDER_ID),
	CONSTRAINT UNIQUE_CP_NAME UNIQUE(CONTENT_PROVIDER_NAME),
	CONSTRAINT UNIQUE_CP_PATTERN UNIQUE(DOMAIN_PATTERN, iS_SSL),
	CONSTRAINT CP_AUTH_LEVEL_FK FOREIGN KEY (MIN_AUTH_LEVEL) REFERENCES AUTH_LEVEL(AUTH_LEVEL_ID),
	CONSTRAINT CP_RES_FK FOREIGN KEY (RESOURCE_ID) REFERENCES RES(RESOURCE_ID)
)ENGINE=InnoDB;


CREATE TABLE CONTENT_PROVIDER_SERVER (
	CONTENT_PROVIDER_SERVER_ID varchar(32) NOT NULL,
	CONTENT_PROVIDER_ID varchar(32) NOT NULL,
	SERVER_URL varchar(100) NOT NULL,
	primary key(CONTENT_PROVIDER_SERVER_ID),
	CONSTRAINT CP_SERVER_CP_FK FOREIGN KEY (CONTENT_PROVIDER_ID) REFERENCES CONTENT_PROVIDER(CONTENT_PROVIDER_ID),
	CONSTRAINT UNIQUE_CP_SERVER UNIQUE(CONTENT_PROVIDER_ID, SERVER_URL)
)ENGINE=InnoDB;


CREATE TABLE URI_PATTERN (
	URI_PATTERN_ID varchar(32) NOT NULL,
	CONTENT_PROVIDER_ID varchar(32) NOT NULL,
	MIN_AUTH_LEVEL varchar(32) NOT NULL, /* logic should default this to value in CP */
	PATTERN varchar(100) NOT NULL,
	IS_PUBLIC char(1) NOT NULL DEFAULT 'N', /* logic should default this to value in CP */
	RESOURCE_ID varchar(32) NOT NULL,
	primary key(URI_PATTERN_ID),
	CONSTRAINT URI_PATTERN_AUTH_LEVEL_FK FOREIGN KEY (MIN_AUTH_LEVEL) REFERENCES AUTH_LEVEL(AUTH_LEVEL_ID),
	CONSTRAINT URI_PATTERN_RES_FK FOREIGN KEY (RESOURCE_ID) REFERENCES RES(RESOURCE_ID),
	CONSTRAINT URI_PATTERN_CP_FK FOREIGN KEY (CONTENT_PROVIDER_ID) REFERENCES CONTENT_PROVIDER(CONTENT_PROVIDER_ID),
	CONSTRAINT URI_PATTERN_UNIQUE UNIQUE (CONTENT_PROVIDER_ID, PATTERN)
)ENGINE=InnoDB;


CREATE TABLE URI_PATTERN_META_TYPE (
	URI_PATTERN_META_TYPE_ID varchar(32) NOT NULL,
	METADATA_TYPE_NAME varchar(100) NOT NULL,
	SPRING_BEAN_NAME varchar(100) NOT NULL,
	primary key(URI_PATTERN_META_TYPE_ID),
	CONSTRAINT UNIQUE_URI_PATTERN_META_TYPE_NAME UNIQUE (METADATA_TYPE_NAME),
	CONSTRAINT UNIQUE_URI_PATTERN_META_TYPE_SPRING_BEAN UNIQUE (SPRING_BEAN_NAME)
)ENGINE=InnoDB;

CREATE TABLE URI_PATTERN_META (
	URI_PATTERN_META_ID varchar(32) NOT NULL,
	URI_PATTERN_ID varchar(32) NOT NULL,
	URI_PATTERN_META_TYPE_ID varchar(22) NOT NULL,
	primary key(URI_PATTERN_META_ID),
	CONSTRAINT URI_PATTERN_META_URI_PATTERN_FK FOREIGN KEY (URI_PATTERN_ID) REFERENCES URI_PATTERN(URI_PATTERN_ID),
	CONSTRAINT URI_PATTERN_META_URI_PATTERN_META_TYPE_FK FOREIGN KEY (URI_PATTERN_META_TYPE_ID) REFERENCES URI_PATTERN_META_TYPE(URI_PATTERN_META_TYPE_ID),
	CONSTRAINT URI_PATTERN_META_UNIQUE UNIQUE (URI_PATTERN_ID, URI_PATTERN_META_TYPE_ID)
)ENGINE=InnoDB;


CREATE TABLE URI_PATTERN_META_VALUE (
	URI_PATTERN_META_VALUE_ID varchar(32) NOT NULL,
	URI_PATTERN_META_ID varchar(32) NOT NULL,
	META_ATTRIBUTE_NAME varchar(100) NOT NULL,
	AM_ATTRIBUTE_ID varchar(32) NOT NULL,
	STATIC_VALUE varchar(100) NOT NULL,
	primary key(URI_PATTERN_META_VALUE_ID),
	CONSTRAINT URI_PATTERN_META_VALUE_AM_FK FOREIGN KEY (AM_ATTRIBUTE_ID) REFERENCES AUTH_RESOURCE_AM_ATTRIBUTE(AM_ATTRIBUTE_ID),
	CONSTRAINT URI_PATTERN_META_VALUE_META_FK FOREIGN KEY (URI_PATTERN_META_ID) REFERENCES URI_PATTERN_META(URI_PATTERN_META_ID)
)ENGINE=InnoDB;

