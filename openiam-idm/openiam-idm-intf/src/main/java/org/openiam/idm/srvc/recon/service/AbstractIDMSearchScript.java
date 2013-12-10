package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.searchbeans.UserSearchBean;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Map;

public abstract class AbstractIDMSearchScript implements IDMSearchScript {

    protected String searchFilter;
    protected Date updatedSince;
    protected String managedSysId;
    protected ApplicationContext context;

    @Override
    public UserSearchBean createUserSearchBean(Map<String, Object> bindingMap) {
        return new UserSearchBean();
    }
}
