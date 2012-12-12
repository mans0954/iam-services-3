package org.openiam.idm.srvc.role.service;

// Generated Mar 4, 2008 1:12:08 AM by Hibernate Tools 3.2.0.b11

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import static org.hibernate.criterion.Example.create;
import static org.hibernate.criterion.Projections.rowCount;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.srvc.res.domain.ResourceRoleEmbeddableId;
import org.openiam.idm.srvc.res.domain.ResourceRoleEntity;
import org.openiam.idm.srvc.res.dto.ResourceRole;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.springframework.stereotype.Repository;

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

				if (StringUtils.isNotEmpty(entity.getRoleName())) {
	                String roleName = entity.getRoleName();
	                MatchMode matchMode = null;
	                if (StringUtils.indexOf(roleName, "*") == 0) {
	                    matchMode = MatchMode.END;
	                    roleName = roleName.substring(1);
	                }
	                if (StringUtils.isNotEmpty(roleName) && StringUtils.indexOf(roleName, "*") == roleName.length() - 1) {
	                	roleName = roleName.substring(0, roleName.length() - 1);
	                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
	                }

	                if (StringUtils.isNotEmpty(roleName)) {
	                    if (matchMode != null) {
	                        criteria.add(Restrictions.ilike("roleName", roleName, matchMode));
	                    } else {
	                        criteria.add(Restrictions.eq("roleName", roleName));
	                    }
	                }
	            }
				
				if(StringUtils.isNotBlank(entity.getServiceId())) {
					criteria.add(Restrictions.eq("serviceId", entity.getServiceId()));
				}
				
				if(CollectionUtils.isNotEmpty(entity.getResourceRoles())) {
					final Set<String> resourceIds = new HashSet<String>();
	            	for(final ResourceRoleEntity resourceRole : entity.getResourceRoles()) {
	            		if(resourceRole != null && StringUtils.isNotBlank(resourceRole.getId().getResourceId())) {
	            			resourceIds.add(resourceRole.getId().getResourceId());
	            		}
	            	}
	            	
	            	if(CollectionUtils.isNotEmpty(resourceIds)) {
	            		criteria.createAlias("resourceRoles", "rr").add( Restrictions.in("rr.id.resourceId", resourceIds));
	            	}
				}
			}
		}
		return criteria;
	}

	@Override
	public List<RoleEntity> findUserRoles(final String userId, final int from, final int size) {
		final Query qry = getSession().createQuery("select role from RoleEntity role, UserRoleEntity ur " +
				" where ur.userId = :userId and " +
				"       ur.roleId = role.roleId" + 
				" order by role.roleName ");
		
	
		qry.setString("userId", userId);
		if(from > -1) {
			qry.setFirstResult(from);
		}
		
		if(size > -1) {
			qry.setMaxResults(size);
		}
		return qry.list();
	}

	@Override
	public List<RoleEntity> getRolesForGroup(final String groupId, final int from, final int size) {
		final Criteria criteria = super.getCriteria();
		criteria.createAlias("groups", "groups").add( Restrictions.in("groups.grpId", new String[] {groupId}));
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
	
	@Override
	public int getNumOfRolesForGroup(String groupId) {
		final Criteria criteria = super.getCriteria();
		criteria.createAlias("groups", "groups").add( Restrictions.in("groups.grpId", new String[] {groupId})).setProjection(rowCount());
		
		return ((Number)criteria.uniqueResult()).intValue();
	}
	

	@Override
	protected String getPKfieldName() {
		return "roleId";
	}

	@Override
	public int getNumOfRolesForResource(final String resourceId) {
		ResourceRoleEntity rrEntity = new ResourceRoleEntity();
		rrEntity.setId(new ResourceRoleEmbeddableId(null, resourceId));
		
		final RoleEntity entity = new RoleEntity();
		entity.addResourceRole(rrEntity);
		return count(entity);
	}

	@Override
	public List<RoleEntity> getRolesForResource(final String resourceId, final int from, final int size) {
		ResourceRoleEntity rrEntity = new ResourceRoleEntity();
		rrEntity.setId(new ResourceRoleEmbeddableId(null, resourceId));
		
		final RoleEntity entity = new RoleEntity();
		entity.addResourceRole(rrEntity);
		return getByExample(entity, from, size);
	}

	@Override
	public List<RoleEntity> getChildRoles(String roleId, int from, int size) {
		final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add(Restrictions.eq("role.roleId", roleId));
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
	
	@Override
	public List<RoleEntity> getParentRoles(String roleId, int from, int size) {
		final Criteria criteria = getCriteria().createAlias("childRoles", "role").add(Restrictions.eq("role.roleId", roleId));
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}

	@Override
	public int getNumOfChildRoles(String roleId) {
		final Criteria criteria = getCriteria().createAlias("parentRoles", "role").add(Restrictions.eq("role.roleId", roleId)).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentRoles(String roleId) {
		final Criteria criteria = getCriteria().createAlias("childRoles", "role").add(Restrictions.eq("role.roleId", roleId)).setProjection(rowCount());
		
		
		return ((Number)criteria.uniqueResult()).intValue();
	}
	
	private Criteria getRolesForUserCriteria(final String userId) {
		return getCriteria()
	               .createAlias("userRoles", "ur")
	               .add(Restrictions.eq("ur.userId", userId));
	}

	@Override
	public List<RoleEntity> getRolesForUser(String userId, int from, int size) {
		final Criteria criteria = getRolesForUserCriteria(userId);
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return criteria.list();
	}

	@Override
	public int getNumOfRolesForUser(String userId) {
		final Criteria criteria = getRolesForUserCriteria(userId).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}
}
