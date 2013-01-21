package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
