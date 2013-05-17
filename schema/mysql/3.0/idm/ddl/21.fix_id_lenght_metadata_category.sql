use openiam;

ALTER TABLE METADATA_TYPE MODIFY TYPE_ID varchar(32) not null;

ALTER TABLE CATEGORY_LANGUAGE MODIFY CATEGORY_ID varchar(32) not null;

ALTER TABLE CATEGORY_TYPE 
	MODIFY 
		CATEGORY_ID varchar(32) not null, 
	MODIFY	
		TYPE_ID varchar(32) not null;
ALTER TABLE CATEGORY 
	MODIFY 
		CATEGORY_ID varchar(32) not null, 
	MODIFY	
		PARENT_ID varchar(32) null;
ALTER TABLE METADATA_ELEMENT 
	MODIFY 
		METADATA_ID varchar(32) not null, 
	MODIFY	
		TYPE_ID varchar(32) not null;
