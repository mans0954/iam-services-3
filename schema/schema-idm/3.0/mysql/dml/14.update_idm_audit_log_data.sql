use openiam;

update IDM_AUDIT_LOG set ACTION_DATETIME = now() where ACTION_DATETIME is null;