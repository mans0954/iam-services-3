use openiam;

DELETE FROM RES_TO_RES_MEMBERSHIP where RESOURCE_ID ='PROVISIONING';
DELETE FROM res_to_res_membership WHERE RESOURCE_ID IN(
	'PROVCONNECT_MENU_ITEM'
);
DELETE FROM RESOURCE_PROP WHERE RESOURCE_ID IN(
	'PROVCONNECT_MENU_ITEM',
	'PROV_CONNECTOR_NEW'
);
DELETE FROM RES WHERE RESOURCE_ID IN(
	'PROVCONNECT_MENU_ITEM',
	'PROV_CONNECTOR_NEW'
);

INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, NAME, DESCRIPTION, URL) VALUES('PROVCONNECT_MENU_ITEM', 'MENU_ITEM', 'PROVCONNECT_ROOT', 'Provisioning Connectors', '/webconsole-idm/provisioning/connectorlist.html');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('PROVCONNECT_PUB', 'PROVCONNECT_MENU_ITEM', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('PROVCONNECT_DESC', 'PROVCONNECT_MENU_ITEM', 'MENU_DISPLAY_NAME', 'Provisioning Connectors');

INSERT INTO RES (RESOURCE_ID, RESOURCE_TYPE_ID, NAME, DESCRIPTION, URL) VALUES('PROV_CONNECTOR_NEW', 'MENU_ITEM', 'CONNECTOR_NEW', 'Create Provisioning Connector', '/webconsole-idm/provisioning/connector.html');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('PROV_CONNECTOR_NEW_PUB', 'PROV_CONNECTOR_NEW', 'MENU_IS_PUBLIC', 'true');
INSERT INTO RESOURCE_PROP (RESOURCE_PROP_ID, RESOURCE_ID, NAME, PROP_VALUE) VALUES ('PROV_CONNECTOR_DSC', 'PROV_CONNECTOR_NEW', 'MENU_DISPLAY_NAME', 'Create New Connector');


INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES('PROVISIONING', 'PROVCONNECT_MENU_ITEM');
INSERT INTO res_to_res_membership (RESOURCE_ID, MEMBER_RESOURCE_ID) VALUES('PROVCONNECT_MENU_ITEM', 'PROV_CONNECTOR_NEW');
