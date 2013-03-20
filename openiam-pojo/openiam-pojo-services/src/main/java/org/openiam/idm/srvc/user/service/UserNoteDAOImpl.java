package org.openiam.idm.srvc.user.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.springframework.stereotype.Repository;

/**
 * Home object for domain model class UserNote.
 * @see org.openiam.idm.srvc.user.dto.UserNote
 * @author Hibernate Tools
 */
@Repository("userNoteDAO")
public class UserNoteDAOImpl extends BaseDaoImpl<UserNoteEntity, String> implements UserNoteDAO {

    @Override
    protected String getPKfieldName() {
        return "userNoteId";  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
	 * Delete all the notes associated with a user.
	 * @param userId
	 */
	public void deleteUserNotes(String userId) {
		Session session = getSession();
		Query qry = session.createQuery("delete "+this.domainClass.getName()+" un  where un.userId = :userId ");
		qry.setString("userId", userId);
		qry.executeUpdate();
	}

	public List<UserNoteEntity> findUserNotes(String userId) {
		return (List<UserNoteEntity>)getCriteria().add(Restrictions.eq("userId",userId))
                .addOrder(Order.asc("userNoteId")).list();
	}	
	
}
