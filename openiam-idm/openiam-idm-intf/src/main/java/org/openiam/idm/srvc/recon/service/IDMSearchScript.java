package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.searchbeans.UserSearchBean;

import java.util.Map;

public interface IDMSearchScript {
    UserSearchBean createUserSearchBean(Map<String, Object> bindingMap);
}
