package org.openiam.am.srvc.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authProviderDao")
public class AuthProviderDaoImpl extends BaseDaoImpl<AuthProviderEntity, String> implements AuthProviderDao {
    @Override
    protected String getPKfieldName() {
        return "providerId";
    }

    @Override
    protected Criteria getExampleCriteria(final AuthProviderEntity attribute) {
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(attribute.getProviderId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), attribute.getProviderId()));
        } else {
            if (StringUtils.isNotEmpty(attribute.getProviderType())) {
                criteria.add(Restrictions.eq("providerType", attribute.getProviderType()));
            }
            if (StringUtils.isNotEmpty(attribute.getManagedSysId())) {
                criteria.add(Restrictions.eq("managedSysId", attribute.getManagedSysId()));
            }

            if (StringUtils.isNotEmpty(attribute.getName())) {
                String name = attribute.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", name));
                    }
                }
            }
        }
        return criteria;
    }

    @Override
    public List<String> getPkListByType(String providerType) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("providerType",providerType)).setProjection(Projections.property(getPKfieldName()));
        return criteria.list();
    }

    @Override
    @Transactional
    public void deleteByPkList(List<String> pkList) {
        if(pkList!=null && !pkList.isEmpty()) {
            Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.providerId in (:pkList) ");
            qry.setParameter("pkList", pkList);
            qry.executeUpdate();
        }
    }
}
