package org.openiam.idm.srvc.role.service;


import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.role.domain.UserRoleEntity;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Example.create;

@Repository("userRoleDAO")
public class UserRoleDAOImpl extends BaseDaoImpl<UserRoleEntity, String> implements UserRoleDAO {

	private static final Log log = LogFactory.getLog(UserRoleDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(final UserRoleEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getUserRoleId())) {
				criteria.add(Restrictions.eq("userRoleId", entity.getUserRoleId()));
			} else {
				if(StringUtils.isNotBlank(entity.getUserId())) {
					criteria.add(Restrictions.eq("userId", entity.getUserId()));
				}
				
				if(StringUtils.isNotBlank(entity.getRoleId())) {
					criteria.add(Restrictions.eq("roleId", entity.getRoleId()));
				}
			}
		}
		return criteria;
	}
	
	@Override
	public UserRoleEntity getRecord(final String userId, final String roleId) {
		final UserRoleEntity example = new UserRoleEntity();
		example.setUserId(userId);
		example.setRoleId(roleId);
		
		final List<UserRoleEntity> resultList = getByExample(example, 0, 1);
		return (CollectionUtils.isNotEmpty(resultList)) ? resultList.get(0) : null;
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

    @Override
    protected String getPKfieldName() {
        return "userRoleId";
    }
}
