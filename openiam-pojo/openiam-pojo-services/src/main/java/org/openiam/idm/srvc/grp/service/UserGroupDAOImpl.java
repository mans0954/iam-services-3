package org.openiam.idm.srvc.grp.service;

// Generated Jul 18, 2009 8:49:10 AM by Hibernate Tools 3.2.2.GA

import java.util.List;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.UserGroup;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

@Repository("userGroupDAO")
public class UserGroupDAOImpl extends BaseDaoImpl<UserGroupEntity, String> implements UserGroupDAO {

	private static final Log log = LogFactory.getLog(UserGroupDAOImpl.class);
	
	private static String DELETE_BY_GROUP_ID = "DELETE FROM %s ug WHERE rg.groupId = :groupId";
	private static String DELETE_BY_USER_ID = "DELETE FROM %s ug WHERE rg.userId = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
		DELETE_BY_GROUP_ID = String.format(DELETE_BY_GROUP_ID, domainClass.getSimpleName());
	}
	
	public List<UserEntity> findUserByGroup(final String groupId, final int from, final int size) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("select usr from org.openiam.idm.srvc.user.domain.UserEntity as usr, UserGroupEntity ug " +
						" where ug.grpId = :groupId and ug.userId = usr.userId " +
						" order by usr.lastName, usr.firstName ");
		

		qry.setString("groupId", groupId);
		if(from > -1) {
			qry.setFirstResult(from);
		}
		if(size > 0) {
			qry.setMaxResults(size);
		}
		return (List<UserEntity>)qry.list();					
	}

    @Override
    protected String getPKfieldName() {
        return "userGrpId";
    }

	@Override
	public void deleteByGroupId(String groupId) {
		final Query query = getSession().createQuery(DELETE_BY_GROUP_ID);
		query.setParameter("groupId", groupId);
		query.executeUpdate();
	}

	@Override
	public void deleteByUserId(String userId) {
		final Query query = getSession().createQuery(DELETE_BY_USER_ID);
		query.setParameter("userId", userId);
		query.executeUpdate();
	}
}
