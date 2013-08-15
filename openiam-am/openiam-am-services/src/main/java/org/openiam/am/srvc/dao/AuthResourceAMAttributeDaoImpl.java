package org.openiam.am.srvc.dao;

import org.hibernate.Query;
import org.openiam.am.srvc.domain.AuthResourceAMAttributeEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("authResourceAMAttributeDao")
public class AuthResourceAMAttributeDaoImpl extends BaseDaoImpl<AuthResourceAMAttributeEntity, String> implements AuthResourceAMAttributeDao{

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    @Transactional
    public void deleteById(String attributeId) {
        Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " o where o.id =:attributeId ");
        qry.setParameter("attributeId", attributeId);
        qry.executeUpdate();
    }

}
