package org.openiam.mq.constants;

/**
 * Created by alexander on 15/09/16.
 */
public enum AMManagerAPI  implements OpenIAMAPI  {
    IsMemberOfGroup,
    IsMemberOfRole,
    IsMemberOfOrganization,
    IsUserEntitledToResourceWithRight,
    IsMemberOfGroupWithRight,
    IsMemberOfRoleWithRight,
    IsMemberOfOrganizationWithRight,
    GetResourcesForUser,
    GetGroupsForUser,
    GetRolesForUser,
    GetOrganizationsForUser,
    RefreshCache,
    IsUserEntitledToResource
}
