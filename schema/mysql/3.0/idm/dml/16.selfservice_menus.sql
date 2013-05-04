use openiam;

UPDATE RES SET URL='/selfservice/changePasswordInternal.html' WHERE RESOURCE_ID='CHNGPSWD';

UPDATE RES SET URL='/selfservice/editProfile.html' WHERE RESOURCE_ID IN('SELF_QUERYUSER', 'SELF_USERSUMMARY');

UPDATE RES SET URL='/selfservice/myIdentities.html' WHERE RESOURCE_ID='SELF_USERIDENTITY';

UPDATE RES SET URL='/selfservice/challengeResponse.html' WHERE RESOURCE_ID='IDQUEST';

UPDATE RES SET URL='/selfservice/resetPasswordInternal.html' WHERE RESOURCE_ID='SELF_USERPSWDRESET';

DELETE FROM RESOURCE_PROP WHERE RESOURCE_ID IN('SELF_LOGIN_ROOT', 'SELF_LOGIN_LIST', 'SELF_LOGIN_NEW');
DELETE FROM res_to_res_membership WHERE RESOURCE_ID IN('SELF_LOGIN_ROOT', 'SELF_LOGIN_LIST', 'SELF_LOGIN_NEW');
DELETE FROM res_to_res_membership WHERE MEMBER_RESOURCE_ID IN('SELF_LOGIN_ROOT', 'SELF_LOGIN_LIST', 'SELF_LOGIN_NEW');
DELETE FROM RES WHERE RESOURCE_ID IN('SELF_LOGIN_ROOT', 'SELF_LOGIN_LIST', 'SELF_LOGIN_NEW');


INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, NAME, DESCRIPTION) VALUES('SELF_LOGIN_ROOT', 'MENU_ITEM', 'SELF_LOGIN_ROOT', 'SelfService Login Root Context Menu');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_ROOT_PUB', 'SELF_LOGIN_ROOT', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_ROOT_DESC', 'SELF_LOGIN_ROOT', 'MENU_DISPLAY_NAME', 'SelfService Login Root');


INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, NAME, DESCRIPTION, URL) VALUES('SELF_LOGIN_LIST', 'MENU_ITEM', 'SELF_LOGIN_LIST', 'My Identities','/selfservice/myIdentities.html');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_LIST_PUB', 'SELF_LOGIN_LIST', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_LIST_DESC', 'SELF_LOGIN_LIST', 'MENU_DISPLAY_NAME', 'My Identities');

INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, NAME, DESCRIPTION, URL) VALUES('SELF_LOGIN_NEW', 'MENU_ITEM', 'SELF_LOGIN_NEW', 'New Login','/selfservice/identity.html');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_NEW_PUB', 'SELF_LOGIN_NEW', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('SELF_LOGIN_NEW_DESC', 'SELF_LOGIN_NEW', 'MENU_DISPLAY_NAME', 'New Login');

INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES('SELF_LOGIN_ROOT', 'SELF_LOGIN_LIST');
INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES('SELF_LOGIN_ROOT', 'SELF_LOGIN_NEW');
