use openiam;

DELETE FROM resource_prop WHERE resource_id='SYNCDETAIL';
DELETE FROM resource_role WHERE resource_id='SYNCDETAIL';
DELETE FROM res_to_res_membership WHERE resource_id='SYNCDETAIL' or member_resource_id='SYNCDETAIL';
DELETE FROM res WHERE resource_id = 'SYNCDETAIL';

UPDATE res SET url='/webconsole-idm/provisioning/synchronizationlist.html' WHERE resource_id='SYNCUSER';

INSERT INTO res (resource_id, resource_type_id, name, description, url) VALUES ('SYNCUSER_MENU_ITEM', 'MENU_ITEM', 'SYNCUSER_MENU_ITEM', 'Synchronization', '/webconsole-idm/provisioning/synchronizationlist.html');
INSERT INTO resource_prop (resource_prop_id, resource_id, name, prop_value) VALUES ('SYNCUSER_DESC', 'SYNCUSER_MENU_ITEM', 'MENU_DISPLAY_NAME', 'Synchronization');
INSERT INTO resource_prop (resource_prop_id, resource_id, name, prop_value) VALUES ('SYNCUSER_PUB', 'SYNCUSER_MENU_ITEM', 'MENU_IS_PUBLIC', 'true');
INSERT INTO res (resource_id, resource_type_id, name, description, url) VALUES ('SYNCUSER_NEW', 'MENU_ITEM', 'SYNCUSER_NEW', 'Create Synchronization', '/webconsole-idm/provisioning/synchronization.html');
INSERT INTO resource_prop (resource_prop_id, resource_id, name, prop_value) VALUES ('SYNCUSER_NEW_DESC', 'SYNCUSER_NEW', 'MENU_DISPLAY_NAME', 'Create Synchronization');
INSERT INTO resource_prop (resource_prop_id, resource_id, name, prop_value) VALUES ('SYNCUSER_NEW_PUB', 'SYNCUSER_NEW', 'MENU_IS_PUBLIC', 'true');
INSERT INTO res_to_res_membership (resource_id, member_resource_id) VALUES ('SYNCUSER_MENU_ITEM', 'SYNCUSER_NEW');
