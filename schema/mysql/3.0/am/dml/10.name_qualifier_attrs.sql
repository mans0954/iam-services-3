use openiam;

INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('NameQualifier', 'Name Qualifier', 'SAML_PROVIDER', 'Value of the NameQualifier attribute in the NameID element', 'N', 'singleValue');