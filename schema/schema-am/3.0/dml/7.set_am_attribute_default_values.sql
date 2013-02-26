use openiam;

UPDATE AUTH_ATTRIBUTE SET DEFAULT_VALUE='urn:oasis:names:tc:SAML:2.0:ac:classes:Password' WHERE AUTH_ATTRIBUTE_ID='AuthnContextClassRef';

UPDATE AUTH_ATTRIBUTE SET DEFAULT_VALUE='urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress' WHERE AUTH_ATTRIBUTE_ID='NameIdFormat';
