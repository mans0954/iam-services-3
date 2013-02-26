use openiam;

INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('AuthnContextClassRef', 'Authentication Context Class', 'SAML_PROVIDER', 'The Value of the AuthnContextClassRef attribute', 'N', 'singleValue');
	
INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('NameIdFormat', 'Name ID Format', 'SAML_PROVIDER', 'The Value of the Format attribute in the NameID element', 'N', 'singleValue');
	
INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('SPNameQualifier', 'SP Name Qualifier', 'SAML_PROVIDER', 'Value of the SPNameQualifier attribute in the NameID element', 'N', 'singleValue');