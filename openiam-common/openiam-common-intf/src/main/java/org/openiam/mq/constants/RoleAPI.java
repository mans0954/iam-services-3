package org.openiam.mq.constants;

/**
 * Created by alexander on 24/08/16.
 */
public enum RoleAPI implements OpenIAMAPI {
    FindBeans,
    ValidateEdit,
    ValidateDelete,
    GetRoleLocalized,
    GetRoleAttributes,
    CountBeans,
    AddGroupToRole,
    SaveRole,
    RemoveRole,
    ValidateGroup2RoleAddition,
    RemoveGroupFromRole,
    AddUserToRole,
    RemoveUserFromRole;
}
