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

@Repository("authResourceAttributeMapDao")
public class AuthResourceAttributeMapDaoImpl extends BaseDaoImpl<AuthResourceAttributeMapEntity, String> implements AuthResourceAttributeMapDao {
    @Override
    protected String getPKfieldName() {
        return "attributeMapId";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthResourceAttributeMapEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getAttributeMapId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getAttributeMapId()));
        } else {
            if (StringUtils.isNotEmpty(attribute.getProviderId())) {
                criteria.add(Restrictions.eq("providerId", attribute.getProviderId()));
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

            if (StringUtils.isNotEmpty(attribute.getAmAttributeId())) {
                String amAttributeName = attribute.getAmAttributeId();
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
                        criteria.add(Restrictions.ilike("amAttributeId", amAttributeName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("amAttributeId", amAttributeName));
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
    public int deleteByProviderId(String providerId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " o where o.providerId =:providerId ");
        qry.setParameter("providerId", providerId);
        return  qry.executeUpdate();
    }
}
