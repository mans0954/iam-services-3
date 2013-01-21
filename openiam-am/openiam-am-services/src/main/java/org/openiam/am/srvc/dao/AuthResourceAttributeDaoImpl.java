package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthResourceAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("authResourceAttributeDao")
public class AuthResourceAttributeDaoImpl extends BaseDaoImpl<AuthResourceAttributeEntity, String> implements AuthResourceAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "attributeMapId";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthResourceAttributeEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getAttributeMapId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getAttributeMapId()));
        } else {
            if (StringUtils.isNotEmpty(attribute.getResourceId())) {
                criteria.add(Restrictions.eq("resourceId", attribute.getResourceId()));
            }

            if (StringUtils.isNotEmpty(attribute.getTargetAttributeName())) {
                String targetAttributeName = attribute.getTargetAttributeName();
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
                        criteria.add(Restrictions.ilike("targetAttributeName", targetAttributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("targetAttributeName", targetAttributeName));
                    }
                }
            }

            if (StringUtils.isNotEmpty(attribute.getAmAttributeName())) {
                String amAttributeName = attribute.getAmAttributeName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(amAttributeName, "*") == 0) {
                    matchMode = MatchMode.END;
                    amAttributeName = amAttributeName.substring(1);
                }
                if (StringUtils.isNotEmpty(amAttributeName) && StringUtils.indexOf(amAttributeName, "*") == amAttributeName.length() - 1) {
                    amAttributeName = amAttributeName.substring(0, amAttributeName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(amAttributeName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("targetAttributeName", amAttributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("targetAttributeName", amAttributeName));
                    }
                }
            }

        }
        return criteria;
    }

    @Override
    @Transactional
    public void deleteById(String attributeId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " o where o.attributeMapId =:attributeId ");
        qry.setParameter("attributeId", attributeId);
        qry.executeUpdate();
    }

    @Override
    @Transactional
    public int deleteByResourceId(String resourceId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " o where o.resourceId =:resourceId ");
        qry.setParameter("resourceId", resourceId);
        return  qry.executeUpdate();
    }
}
