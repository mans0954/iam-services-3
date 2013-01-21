package org.openiam.am.srvc.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openiam.am.srvc.domain.AuthProviderAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("authProviderAttributeDao")
public class AuthProviderAttributeDaoImpl extends BaseDaoImpl<AuthProviderAttributeEntity, String> implements AuthProviderAttributeDao {
    @Override
    protected String getPKfieldName() {
        return "providerAttributeId";
    }

    @Override
    @Transactional
    public void deleteByProviderList(List<String> pkList) {
        if(pkList!=null && !pkList.isEmpty()) {
            Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.providerId in (:pkList) ");
            qry.setParameter("pkList", pkList);
            qry.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteByAttributeList(List<String> pkList){
        if(pkList!=null && !pkList.isEmpty()) {
            Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.attributeId in (:pkList) ");
            qry.setParameter("pkList", pkList);
            qry.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteByAttribute(String providerId, String attributeId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " p where p.providerId = :providerId and p.attributeId=:attributeId ");
        qry.setString("providerId", providerId);
        qry.setString("attributeId", attributeId);
        qry.executeUpdate();
    }


    @Override
    public AuthProviderAttributeEntity getAuthProviderAttribute(String providerId, String attributeId) {
        Criteria criteria = getCriteria();
        criteria.add(Restrictions.eq("providerId", providerId)).add(Restrictions.eq("attributeId",attributeId));
        List<AuthProviderAttributeEntity> result = criteria.list();
        if(result==null || result.isEmpty())
            return null;
        return result.get(0);
    }
}
