package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthResourceAttributeMapEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authResourceAttributeMapDao")
public class AuthResourceAttributeMapDaoImpl extends BaseDaoImpl<AuthResourceAttributeMapEntity, String> implements AuthResourceAttributeMapDao {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthResourceAttributeMapEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getId()));
        } else {
            if (attribute.getProvider() != null && StringUtils.isNotEmpty(attribute.getProvider().getId())) {
                criteria.add(Restrictions.eq("provider.id", attribute.getProvider().getId()));
            }

            if (StringUtils.isNotEmpty(attribute.getName())) {
                String targetAttributeName = attribute.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(targetAttributeName, "*") == 0) {
                    matchMode = MatchMode.END;
                    targetAttributeName = targetAttributeName.substring(1);
                }
                if (StringUtils.isNotEmpty(targetAttributeName) && StringUtils.indexOf(targetAttributeName, "*") == targetAttributeName.length() - 1) {
                    targetAttributeName = targetAttributeName.substring(0, targetAttributeName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(targetAttributeName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", targetAttributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", targetAttributeName));
                    }
                }
            }

            if (attribute.getAmAttribute() != null && StringUtils.isNotEmpty(attribute.getAmAttribute().getId())) {
                criteria.add(Restrictions.eq("amAttribute.id", attribute.getAmAttribute().getId()));
            }

        }
        return criteria;
    }
}
