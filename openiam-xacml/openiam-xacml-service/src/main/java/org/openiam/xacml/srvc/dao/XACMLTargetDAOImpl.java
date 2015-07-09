package org.openiam.xacml.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;
import org.springframework.stereotype.Repository;

/**
 * Created by zaporozhec on 7/10/15.
 */
@Repository("xacmlTargetDao")
public class XACMLTargetDAOImpl extends BaseDaoImpl<XACMLTargetEntity, String> implements XACMLTargetDAO {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    protected Criteria getExampleCriteria(final XACMLTargetEntity targetEntity) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(targetEntity.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), targetEntity.getId()));
        } else {
            if (StringUtils.isNotEmpty(targetEntity.getName())) {
                String attributeName = targetEntity.getName();
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
