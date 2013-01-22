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
        return "authAttributeId";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthAttributeEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getAuthAttributeId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getAuthAttributeId()));
        } else {
            if (StringUtils.isNotEmpty(attribute.getProviderType())) {
                criteria.add(Restrictions.eq("providerType", attribute.getProviderType()));
            }

            if (StringUtils.isNotEmpty(attribute.getAttributeName())) {
                String attributeName = attribute.getAttributeName();
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

    @Override
    @Transactional
    public void deleteByType(String providerType) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " a where a.providerType = :providerType ");
        qry.setString("providerType", providerType);
        qry.executeUpdate();
    }

    @Override
    public List<String> getPkListByType(String providerType) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("providerType", providerType)).setProjection(Projections.property(getPKfieldName()));
        return criteria.list();
    }

    @Override
    @Transactional
    public void deleteByPkList(List<String> pkList) {
        if(pkList!=null && !pkList.isEmpty()) {
            Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.authAttributeId in (:pkList) ");
            qry.setParameterList("pkList", pkList);
            qry.executeUpdate();
        }
    }
}
