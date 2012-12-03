package org.openiam.idm.srvc.grp.service;

// Generated Jun 12, 2007 10:46:15 PM by Hibernate Tools 3.2.0.beta8

import static org.hibernate.criterion.Projections.rowCount;

import java.util.ArrayList;
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
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.HibernateException;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;

import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceGroupEntity;
import org.openiam.idm.srvc.res.dto.ResourceGroup;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.stereotype.Repository;

@Repository("groupDAO")
public class GroupDAOImpl extends BaseDaoImpl<GroupEntity, String> implements GroupDAO {

	@Override
	protected Criteria getExampleCriteria(GroupEntity group) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(group.getGrpId())) {
			criteria.add(Restrictions.eq("grpId", group.getGrpId()));
		} else {
			if (StringUtils.isNotEmpty(group.getGrpName())) {
                String groupName = group.getGrpName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(groupName, "*") == 0) {
                    matchMode = MatchMode.END;
                    groupName = groupName.substring(1);
                }
                if (StringUtils.isNotEmpty(groupName) && StringUtils.indexOf(groupName, "*") == groupName.length() - 1) {
                	groupName = groupName.substring(0, groupName.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(groupName)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("grpName", groupName, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("grpName", groupName));
                    }
                }
            }
			
			if(StringUtils.isNotBlank(group.getOwnerId())) {
				criteria.add(Restrictions.eq("ownerId", group.getOwnerId()));
			}
			
			if(StringUtils.isNotBlank(group.getInternalGroupId())) {
				criteria.add(Restrictions.eq("internalGroupId", group.getInternalGroupId()));
			}
            
            if(CollectionUtils.isNotEmpty(group.getResourceGroups())) {
            	final Set<String> resourceIds = new HashSet<String>();
            	for(final ResourceGroupEntity resourceGroupEntity : group.getResourceGroups()) {
            		if(resourceGroupEntity != null && StringUtils.isNotBlank(resourceGroupEntity.getResourceId())) {
            			resourceIds.add(resourceGroupEntity.getResourceId());
            		}
            	}
            	
            	if(CollectionUtils.isNotEmpty(resourceIds)) {
            		criteria.createAlias("resourceGroups", "resourceGroup").add( Restrictions.in("resourceGroup.resourceId", resourceIds));
            	}
            }
		}
		return criteria;
	}
	
	public List<GroupEntity> findRootGroups(final int from, final int size) {
		final Criteria criteria = getCriteria();
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}

		if(size > 0) {
			criteria.setMaxResults(size);
		}
		criteria.add(Restrictions.isEmpty("parentGroups"));

		return (List<GroupEntity>)criteria.list();
	}
	
	public List<GroupEntity> findGroupsForUser(final String userId, final int from, final int size) {
		final Query qry = getSession().createQuery("select grp from GroupEntity as grp, UserGroupEntity ug where ug.userId = :userId and grp.grpId = ug.grpId ");
		qry.setString("userId", userId);
		
		if(from > -1) {
			qry.setFirstResult(from);
		}
		
		if(size > -1) {
			qry.setMaxResults(size);
		}
		
		return (List<GroupEntity>)qry.list();			
	}

	@Override
	protected String getPKfieldName() {
		return "grpId";
	}

	@Override
	public List<GroupEntity> getGroupsForRole(String roleId, int from, int size) {
		final Criteria criteria = super.getCriteria();
		criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.roleId", roleId));
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}

	@Override
	public int getNumOfGroupsForRole(String roleId) {
		final Criteria criteria = super.getCriteria();
		criteria.createAlias("roles", "roles").add( Restrictions.eq("roles.roleId", roleId)).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfChildGroups(String groupId) {
		final Criteria criteria = getCriteria().createAlias("parentGroups", "group").add( Restrictions.eq("group.grpId", groupId)).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public int getNumOfParentGroups(String groupId) {
		final Criteria criteria = getCriteria().createAlias("childGroups", "group").add( Restrictions.eq("group.grpId", groupId)).setProjection(rowCount());
		return ((Number)criteria.uniqueResult()).intValue();
	}

	@Override
	public List<GroupEntity> getChildGroups(String groupId, int from, int size) {
		final Criteria criteria = getCriteria().createAlias("parentGroups", "group").add( Restrictions.eq("group.grpId", groupId));
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}

	@Override
	public List<GroupEntity> getParentGroups(String groupId, int from, int size) {
		final Criteria criteria = getCriteria().createAlias("childGroups", "group").add( Restrictions.eq("group.grpId", groupId));
		
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		return criteria.list();
	}
}


