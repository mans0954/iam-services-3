package org.openiam.am.srvc.resattr.dao;

import org.openiam.am.srvc.resattr.domain.AuthAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("authAttributeDao")
public class AuthAttributeDaoImpl extends BaseDaoImpl<AuthAttributeEntity, String> implements AuthAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "attributeName";
    }
}
