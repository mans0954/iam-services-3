package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Map;

public class AbstractIDMSearchScript implements IDMSearchScript {

    protected String searchFilter;
    protected Date updatedSince;
    protected String managedSysId;
    protected ApplicationContext context;

    @Override
    public UserSearchBean createUserSearchBean(Map<String, Object> bindingMap) {
        return new UserSearchBean();
    }

    @Override
    public GroupSearchBean createGroupSearchBean(Map<String, Object> bindingMap) {
        return new GroupSearchBean();
    }

    @Override
    public RoleSearchBean createRoleSearchBean(Map<String, Object> bindingMap) {
        return new RoleSearchBean();
    }

    @Override
    public OrganizationSearchBean createOrgSearchBean(Map<String, Object> bindingMap) {
        return new OrganizationSearchBean();
    }
}
