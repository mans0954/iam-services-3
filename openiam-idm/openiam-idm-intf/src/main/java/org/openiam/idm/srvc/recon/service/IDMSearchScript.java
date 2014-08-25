package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;

import java.util.Map;

public interface IDMSearchScript {
    UserSearchBean createUserSearchBean(Map<String, Object> bindingMap);
    GroupSearchBean createGroupSearchBean(Map<String, Object> bindingMap);
    RoleSearchBean createRoleSearchBean(Map<String, Object> bindingMap);
    OrganizationSearchBean createOrgSearchBean(Map<String, Object> bindingMap);
}
