use openiam;

/* rename column that cauases Oracle to blow up */
ALTER TABLE AUTH_LEVEL CHANGE LEVEL AUTH_LEVEL_DIG int (3) NOT NULL;

/* 30 char limit for table names in Oracle */
RENAME TABLE METADATA_ELEMENT_VALID_VALUES TO MD_ELEMENT_VALID_VALUES;

RENAME TABLE METADATA_ELEMENT_PAGE_TEMPLATE_XREF TO PAGE_TEMPLATE_XREF;

RENAME TABLE METADATA_ELEMENT_TEMPLATE_URI_PATTERN_XREF TO METADATA_URI_XREF;

/* 32 char limit for FK name sin Oracle */
ALTER TABLE PAGE_TEMPLATE_XREF DROP FOREIGN KEY METADATA_ELEMENT_PAGE_TEMPLATE_ELEMENT_FK;

ALTER TABLE PAGE_TEMPLATE_XREF 
	ADD CONSTRAINT MD_ELMT_TEMPLATE_FK FOREIGN KEY (METADATA_ELEMENT_ID)
	REFERENCES METADATA_ELEMENT(METADATA_ID);
	
	

	
ALTER TABLE USER_IDENTITY_ANS DROP FOREIGN KEY FK_USER_IDENTITY_ANS_IDENTITY_QUESTION;

ALTER TABLE USER_IDENTITY_ANS 
	ADD CONSTRAINT FK_ID_AND_QUEST_FK FOREIGN KEY (IDENTITY_QUESTION_ID)
	REFERENCES IDENTITY_QUESTION(IDENTITY_QUESTION_ID);


	
ALTER TABLE USER_ATTRIBUTES DROP FOREIGN KEY FK_USER_ATTRIBUTES_METADATA_ELEMENT;

ALTER TABLE USER_ATTRIBUTES 
	ADD CONSTRAINT FK_USR_ATTR_ELMT_FK FOREIGN KEY (METADATA_ID)
	REFERENCES METADATA_ELEMENT(METADATA_ID);


	
ALTER TABLE URI_PATTERN_META_TYPE DROP INDEX UNIQUE_URI_PATTERN_META_TYPE_SPRING_BEAN;

ALTER TABLE URI_PATTERN_META_TYPE ADD CONSTRAINT META_SPRING_BEAN_UNIQUE  UNIQUE (SPRING_BEAN_NAME);



ALTER TABLE URI_PATTERN_META_TYPE DROP INDEX UNIQUE_URI_PATTERN_META_TYPE_NAME;

ALTER TABLE URI_PATTERN_META_TYPE ADD CONSTRAINT URI_META_TYPE_NAME_UNIQUE UNIQUE (METADATA_TYPE_NAME);



ALTER TABLE URI_PATTERN_META DROP FOREIGN KEY URI_PATTERN_META_URI_PATTERN_META_TYPE_FK;

ALTER TABLE URI_PATTERN_META DROP INDEX URI_PATTERN_META_URI_PATTERN_META_TYPE_FK;

ALTER TABLE URI_PATTERN_META
	ADD CONSTRAINT URI_PATTERN_META_META_TYPE_FK
    	FOREIGN KEY (URI_PATTERN_META_TYPE_ID) REFERENCES URI_PATTERN_META_TYPE (URI_PATTERN_META_TYPE_ID),
    ADD INDEX URI_PATTERN_META_META_TYPE_FK (URI_PATTERN_META_TYPE_ID ASC);
    
    

ALTER TABLE IDENTITY_QUESTION DROP FOREIGN KEY FK_IDENTITY_QUESTION_IDENTITY_QUEST_GRP;

ALTER TABLE IDENTITY_QUESTION
	ADD CONSTRAINT ID_QU_IDEN_QUEST_GRP_FK
    	FOREIGN KEY (IDENTITY_QUEST_GRP_ID) REFERENCES IDENTITY_QUEST_GRP(IDENTITY_QUEST_GRP_ID);
    	

ALTER TABLE RELATION_CATEGORY DROP FOREIGN KEY FK_RELATION_CATEGORY_RELATION_SET;

ALTER TABLE RELATION_CATEGORY
	ADD CONSTRAINT REL_CAT_REL_SET_FK
    	FOREIGN KEY (RELATION_SET_ID) REFERENCES RELATION_SET(RELATION_SET_ID);
    	
    	
    	
    	
    	
    	
    	
    	
ALTER TABLE POLICY_ATTRIBUTE DROP FOREIGN KEY FK_POLICY_ATTRIBUTE_POLICY_DEF_PARAM;

ALTER TABLE POLICY_ATTRIBUTE ADD CONSTRAINT POLI_ATTR_POL_DEF_PARAM FOREIGN KEY (DEF_PARAM_ID) REFERENCES POLICY_DEF_PARAM(DEF_PARAM_ID);




ALTER TABLE PAGE_TEMPLATE_XREF DROP FOREIGN KEY METADATA_ELEMENT_PAGE_TEMPLATE_FK;

ALTER TABLE PAGE_TEMPLATE_XREF ADD CONSTRAINT META_PG_TEMPLATE_FK FOREIGN KEY (TEMPLATE_ID) REFERENCES METADATA_ELEMENT_PAGE_TEMPLATE(ID);



ALTER TABLE  METADATA_URI_XREF DROP FOREIGN KEY TEMPLATE_PATTERN_XREF_TEMPLATE_FK;

ALTER TABLE  METADATA_URI_XREF ADD CONSTRAINT TEMPLETE_PATTERN_XREF_FK FOREIGN KEY (TEMPLATE_ID) REFERENCES METADATA_ELEMENT_PAGE_TEMPLATE(ID);



ALTER TABLE METADATA_ELEMENT_PAGE_TEMPLATE DROP INDEX METADATA_ELEMENT_PAGE_TEMPLATE_UNIQUE;

ALTER TABLE METADATA_ELEMENT_PAGE_TEMPLATE ADD CONSTRAINT PAGE_TEMPLATE_UNIQUE UNIQUE (NAME);


ALTER TABLE METADATA_ELEMENT DROP FOREIGN KEY FK_METADATA_ELEMENT_METADATA_TYPE;

ALTER TABLE METADATA_ELEMENT ADD CONSTRAINT FK_MD_ELMT_TYPE_FK FOREIGN KEY (TYPE_ID) REFERENCES METADATA_TYPE(TYPE_ID);



ALTER TABLE MD_ELEMENT_VALID_VALUES DROP FOREIGN KEY METADATA_FIELD_VALID_VALUES_ELEMENT_FK; 

ALTER TABLE MD_ELEMENT_VALID_VALUES ADD CONSTRAINT VALID_VALUES_ELEMENT_FK FOREIGN KEY (METADATA_ELEMENT_ID) REFERENCES METADATA_ELEMENT(METADATA_ID);




ALTER TABLE GRP_ATTRIBUTES DROP FOREIGN KEY FK_GRP_ATTRIBUTES_METADATA_ELEMENT;

ALTER TABLE GRP_ATTRIBUTES ADD CONSTRAINT GRP_ATTR_META_ELMT_FK FOREIGN KEY (METADATA_ID) REFERENCES METADATA_ELEMENT(METADATA_ID);



ALTER TABLE COMPANY_TO_COMPANY_MEMBERSHIP DROP FOREIGN KEY COMPANY_TO_COMPANY_MEMBERSHIP_ibfk_1;

ALTER TABLE COMPANY_TO_COMPANY_MEMBERSHIP DROP FOREIGN KEY COMPANY_TO_COMPANY_MEMBERSHIP_ibfk_2;

ALTER TABLE COMPANY_TO_COMPANY_MEMBERSHIP ADD CONSTRAINT COMPANY_COMPANY_CHILD FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (COMPANY_ID );

ALTER TABLE COMPANY_TO_COMPANY_MEMBERSHIP ADD CONSTRAINT COMPANY_COMPANY_PARENT FOREIGN KEY (MEMBER_COMPANY_ID) REFERENCES COMPANY (COMPANY_ID );
