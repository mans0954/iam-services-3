package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authProviderAttributeDao")
public class AuthProviderAttributeDaoImpl extends BaseDaoImpl<AuthProviderAttributeEntity, String> implements AuthProviderAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthProviderAttributeEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getId()));
        } else {
            if (StringUtils.isNotEmpty(attribute.getId())) {
                criteria.add(Restrictions.eq("provider.id", attribute.getId()));
            }
        }
        return criteria;
    }
}
