package org.openiam.am.srvc.resattr.dao;

import org.openiam.am.srvc.resattr.domain.AuthProviderAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("authProviderAttributeDao")
public class AuthProviderAttributeDaoImpl extends BaseDaoImpl<AuthProviderAttributeEntity, String> implements AuthProviderAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "providerAttributeId";
    }
}
