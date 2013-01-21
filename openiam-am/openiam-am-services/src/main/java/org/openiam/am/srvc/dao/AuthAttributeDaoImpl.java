package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
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
            qry.setParameter("pkList", pkList);
            qry.executeUpdate();
        }
    }
}
