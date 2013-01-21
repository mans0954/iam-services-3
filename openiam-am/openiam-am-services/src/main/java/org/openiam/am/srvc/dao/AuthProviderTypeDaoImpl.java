package org.openiam.am.srvc.dao;

import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("authProviderTypeDao")
public class AuthProviderTypeDaoImpl extends BaseDaoImpl<AuthProviderTypeEntity, String> implements AuthProviderTypeDao {
    @Override
    protected String getPKfieldName() {
        return "providerType";
    }
}
