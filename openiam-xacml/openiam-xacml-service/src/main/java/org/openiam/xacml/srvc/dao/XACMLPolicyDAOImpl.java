package org.openiam.xacml.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.xacml.srvc.domain.XACMLPolicyEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Repository("xacmlPolicyDao")
public class XACMLPolicyDAOImpl extends BaseDaoImpl<XACMLPolicyEntity, String> implements XACMLPolicyDAO {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final XACMLPolicyEntity policyEntity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(policyEntity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), policyEntity.getId()));
        } else {
            if (StringUtils.isNotEmpty(policyEntity.getIdentifier())) {
                String attributeName = policyEntity.getIdentifier();
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
                        criteria.add(Restrictions.ilike("identifier", attributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("identifier", attributeName));
                    }
                }
            }
        }
        return criteria;
    }
}
