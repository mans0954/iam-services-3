use openiam;

INSERT INTO AUTH_PROVIDER_TYPE (PROVIDER_TYPE, DESCRIPTION) VALUES('SAML_PROVIDER', 'SAML Provider');

INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('1', 'Request Issuer', 'SAML_PROVIDER', 'The Issuer element to look for in the SAMLRequest', 'Y', 'singleValue');
	
INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('2', 'Response Issuer', 'SAML_PROVIDER', 'The Issuer element to send in the SAMLResponse.  Default is the SAML Login Page URL', 'N', 'singleValue');
	
INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('3', 'Assertion Consumer URL', 'SAML_PROVIDER', 'Where the SAMLResponse will be POSTed to', 'Y', 'singleValue');
	
INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE)
	VALUES('4', 'Audiences', 'SAML_PROVIDER', 'The Audience value(s) to send in the SAMLResponse', 'Y', 'listValue');