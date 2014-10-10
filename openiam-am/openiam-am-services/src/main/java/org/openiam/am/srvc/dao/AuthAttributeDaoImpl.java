package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authAttributeDao")
public class AuthAttributeDaoImpl extends BaseDaoImpl<AuthAttributeEntity, String> implements AuthAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthAttributeEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getId()));
        } else {
            if (attribute.getType() != null && StringUtils.isNotEmpty(attribute.getType().getId())) {
                criteria.add(Restrictions.eq("type.id", attribute.getType().getId()));
            }

            if (StringUtils.isNotEmpty(attribute.getName())) {
                String attributeName = attribute.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(attributeName, "*") == 0) {
                    matchMode = MatchMode.END;
                    attributeName = attributeName.substring(1);
                }
                if (StringUtils.isNotEmpty(attributeName) && StringUtils.indexOf(attributeName, "*") == attributeName.length() - 1) {
                    attributeName = attributeName.substring(0, attributeName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(attributeName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", attributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", attributeName));
                    }
                }
            }
        }
        return criteria;
    }
}
