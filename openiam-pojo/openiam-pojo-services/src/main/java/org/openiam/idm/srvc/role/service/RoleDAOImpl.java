package org.openiam.idm.srvc.role.service;

// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import static org.hibernate.criterion.Example.create;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository("roleDAO")
public class RoleDAOImpl extends BaseDaoImpl<RoleEntity, String> implements RoleDAO {

	private static final Log log = LogFactory.getLog(RoleDAOImpl.class);

	
	
	@Override
	protected Criteria getExampleCriteria(final RoleEntity entity) {
		final Criteria criteria = super.getCriteria();
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getRoleId())) {
				criteria.add(Restrictions.eq("roleId", entity.getRoleId()));
			} else {
				if(StringUtils.isNotBlank(entity.getRoleName())) {
					criteria.add(Restrictions.eq("roleName", entity.getRoleName()));
				}
			}
		}
		return criteria;
	}

	@Override
	public List<RoleEntity> findUserRoles(String userId) {
		final Query qry = getSession().createQuery("select role from Role role, UserRoleEntity ur " +
				" where ur.userId = :userId and " +
				"       ur.roleId = role.roleId" + 
				" order by role.roleName ");
		
	
		qry.setString("userId", userId);
		return qry.list();
	}

	@Override
	public List<RoleEntity> findRolesInGroup(String groupId) {
		final Criteria criteria = super.getCriteria();
		criteria.createAlias("groups", "groups").add( Restrictions.in("groups.grpId", new String[] {groupId}));
		return criteria.list();
	}
	

	@Override
	protected String getPKfieldName() {
		return "roleId";
	}
}
