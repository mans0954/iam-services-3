package org.openiam.am.srvc.resattr.dao;

import org.openiam.am.srvc.resattr.domain.AuthResourceAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;

@Repository("authResourceAttributeDao")
public class AuthResourceAttributeDaoImpl extends BaseDaoImpl<AuthResourceAttributeEntity, String> implements AuthResourceAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "attributeMapId";
    }
}
