use openiam;

UPDATE AUTH_ATTRIBUTE SET DEFAULT_VALUE='urn:oasis:names:tc:SAML:2.0:ac:classes:Password' WHERE AUTH_ATTRIBUTE_ID='AuthnContextClassRef';

UPDATE AUTH_ATTRIBUTE SET DEFAULT_VALUE='urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress' WHERE AUTH_ATTRIBUTE_ID='NameIdFormat';

INSERT INTO AUTH_ATTRIBUTE (AUTH_ATTRIBUTE_ID, ATTRIBUTE_NAME, PROVIDER_TYPE, DESCRIPTION, REQUIRED, DATA_TYPE) 
					 VALUES('METADATA_EXPOSED', 'Metdata Exposed', 'SAML_PROVIDER', 'Is the metadata for this SAML Provider exposed?  Some Service Providers require a URL that defines the Identity Provider Metadata', 'N', 'booleanValue');