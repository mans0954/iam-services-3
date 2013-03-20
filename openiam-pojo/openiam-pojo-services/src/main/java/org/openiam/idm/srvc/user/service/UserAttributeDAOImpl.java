package org.openiam.idm.srvc.user.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Home object for domain model class UserAttribute.
 * @see org.openiam.idm.srvc.user.dto.UserAttribute
 * @author Hibernate Tools
 */
@Repository("userAttributeDAO")
public class UserAttributeDAOImpl extends BaseDaoImpl<UserAttributeEntity, String> implements UserAttributeDAO {
    @Override
    protected String getPKfieldName() {
        return "id";
    }

    public List<UserAttributeEntity> findUserAttributes(String userId) {
		return (List<UserAttributeEntity>)getCriteria().add(Restrictions.eq("userId",userId)).addOrder(Order.asc("name")).list();
	}
	@Transactional
	public void deleteUserAttributes(String userId) {
		Query qry = getSession().createQuery("delete "+this.domainClass.getName()+ " ua where ua.userId = :userId ");
		qry.setString("userId", userId);
		qry.executeUpdate();
	}

}

