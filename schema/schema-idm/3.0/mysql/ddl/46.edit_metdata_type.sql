use openiam;

ALTER TABLE METADATA_TYPE ADD TEMP_ACTIVE CHAR(1) NULL DEFAULT 'N';
UPDATE METADATA_TYPE SET TEMP_ACTIVE='Y' WHERE ACTIVE=1;
UPDATE METADATA_TYPE SET TEMP_ACTIVE='N' WHERE ACTIVE=0;
ALTER TABLE METADATA_TYPE DROP COLUMN ACTIVE;
ALTER TABLE METADATA_TYPE CHANGE TEMP_ACTIVE ACTIVE CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE METADATA_TYPE ADD TEMP_SYNC_MANAGED_SYS CHAR(1) NULL DEFAULT 'N';
UPDATE METADATA_TYPE SET TEMP_SYNC_MANAGED_SYS='Y' WHERE SYNC_MANAGED_SYS=1;
UPDATE METADATA_TYPE SET TEMP_SYNC_MANAGED_SYS='N' WHERE SYNC_MANAGED_SYS=0;
ALTER TABLE METADATA_TYPE DROP COLUMN SYNC_MANAGED_SYS;
ALTER TABLE METADATA_TYPE CHANGE TEMP_SYNC_MANAGED_SYS SYNC_MANAGED_SYS CHAR(1) NOT NULL DEFAULT 'N';