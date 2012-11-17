package org.openiam.idm.srvc.role.service;


import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;

import static org.hibernate.criterion.Example.create;

/**
 * DAO implementation for the UserRole. Manages the relationship between user and role.
 * @see org.openiam.idm.srvc.role.dto.UserRole
 * @author Hibernate Tools
 */
public class UserRoleDAOImpl extends BaseDaoImpl<UserRoleEntity, String> implements UserRoleDAO {

	private static final Log log = LogFactory.getLog(UserRoleDAOImpl.class);

	public void setSessionFactory(SessionFactory session) {
		   this.sessionFactory = session;
	}

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public List<UserRoleEntity> findUserRoleByUser(String userId) {
		
		
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("select ur from UserRoleEntity ur " +
						" where ur.userId = :userId " +
						" order by ur.roleId ");
		
		qry.setString("userId", userId);
		List<UserRoleEntity> result = (List<UserRoleEntity>)qry.list();
		if (result == null || result.size() == 0)
			return null;
		return result;			
	}
	
	public List<UserEntity> findUserByRole(String roleId) {
		log.debug("findUserByRole: roleId=" + roleId);
		
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("select usr from org.openiam.idm.srvc.user.domain.UserEntity as usr, UserRoleEntity ur " +
						" where ur.roleId = :roleId and ur.userId = usr.userId " +
						" order by usr.lastName, usr.firstName ");
		
		qry.setString("roleId",roleId);
		List<UserEntity> result = (List<UserEntity>)qry.list();
		if (result == null || result.size() == 0)
			return null;
		return result;			
	}
	
	public void removeUserFromRole(String roleId,	String userId) {
		log.debug("removeUserFromRole: userId=" + userId);
		log.debug("removeUserFromRole: roleId=" + roleId);
		
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("delete org.openiam.idm.srvc.role.domain.UserRoleEntity ur " +
					" where ur.roleId = :roleId and ur.userId = :userId ");
		qry.setString("roleId", roleId);
		qry.setString("userId", userId);
		qry.executeUpdate();	
	}

	public void removeAllUsersInRole(String roleId) {
		log.debug("removeUserFromRole: roleId=" + roleId);
		
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("delete org.openiam.idm.srvc.role.domain.UserRoleEntity ur " +
					" where ur.roleId = :roleId");
		qry.setString("roleId", roleId);
		qry.executeUpdate();			
	}

    @Override
    protected String getPKfieldName() {
        return "userRoleId";
    }
}
