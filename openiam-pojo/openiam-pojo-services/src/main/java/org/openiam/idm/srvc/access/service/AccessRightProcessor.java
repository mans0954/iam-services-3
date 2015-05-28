package org.openiam.idm.srvc.access.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.entitlements.AbstractEntitlementsDTO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.springframework.stereotype.Component;

/**
 * This class processes entitlement rights before returning them to the caller
 * By definition, we have the following definiitions:
 * Org -> Group
 * Group -> Role
 * Role -> Resource
 * Org -> Role
 * Org -> Resource
 * Group -> Resource
 * Parent Group -> Child Group
 * Parent Role -> Child Role
 * Parent Resource -> Child Resource
 * 
 * Access Rights are uni-directional, and are enforced FROM the parent TO the child.
 * 
 * The meaning of the access rights depends on how you request it.
 * It is up to the caller to interpret the meaning
 * 
 * @author lbornova
 *
 */
@Component
public class AccessRightProcessor {
	
	private <T extends AbstractEntitlementsDTO> Map<String, T> transform(final List<T> dtoList) {
		return dtoList.stream().collect(Collectors.toMap(T::getId, Function.identity()));
	}
	
	private <T extends AbstractEntitlementsDTO> void setRights(final Map<String, T> dtoMap, final AbstractMembershipXrefEntity xref, final KeyEntity entity) {
		if(xref != null && xref.getRights() != null) {
			final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
			dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
		}
	}
	
	private void assertLength(final Set<String> set) {
		if(CollectionUtils.size(set) > 1) {
			throw new IllegalArgumentException("When querying for rights, you can only query by one object at a time.  See stacktrace for details as to where this happens");
		}
	}

	public void process(final OrganizationSearchBean searchBean, final List<Organization> dtoList, final List<OrganizationEntity> entityList) {
		if(searchBean != null) {
        	if(searchBean.isIncludeAccessRights()) {
        		if(CollectionUtils.isNotEmpty(entityList)) {
        			final Map<String, Organization> dtoMap = transform(dtoList);
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			assertLength(searchBean.getParentIdSet());
		        			final String queryId = searchBean.getParentIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getParent(queryId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
		        			assertLength(searchBean.getChildIdSet());
		        			final String queryId = searchBean.getChildIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getChild(queryId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
		        			assertLength(searchBean.getGroupIdSet());
		        			final String queryId = searchBean.getGroupIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getGroup(queryId);
		        			setRights(dtoMap, xref, entity);
		        		}
			        });
	        	}
        	}
        }
	}
	
	public void process(final RoleSearchBean searchBean, final List<Role> dtoList, final List<RoleEntity> entityList) {
		if(searchBean != null) {
        	if(searchBean.isIncludeAccessRights()) {
        		if(CollectionUtils.isNotEmpty(entityList)) {
        			final Map<String, Role> dtoMap = transform(dtoList);
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			assertLength(searchBean.getParentIdSet());
		        			final String entityId = searchBean.getParentIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
		        			assertLength(searchBean.getChildIdSet());
		        			final String entityId = searchBean.getChildIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
		        			setRights(dtoMap, xref, entity);
		        		}
			        });
	        	}
        	}
        }
	}
	
	public void process(final GroupSearchBean searchBean, final List<Group> dtoList, final List<GroupEntity> entityList) {
		if(searchBean != null) {
        	if(searchBean.isIncludeAccessRights()) {
        		if(CollectionUtils.isNotEmpty(entityList)) {
        			final Map<String, Group> dtoMap = transform(dtoList);
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			assertLength(searchBean.getParentIdSet());
		        			final String entityId = searchBean.getParentIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
		        			assertLength(searchBean.getChildIdSet());
		        			final String entityId = searchBean.getChildIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
		        			assertLength(searchBean.getOrganizationIdSet());
		        			final String entityId = searchBean.getOrganizationIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getOrganization(entityId);
		        			setRights(dtoMap, xref, entity);
		        		} 
			        });
	        	}
        	}
        }
	}
	
	public void process(final ResourceSearchBean searchBean, final List<Resource> dtoList, final List<ResourceEntity> entityList) {
		if(searchBean != null) {
        	if(searchBean.isIncludeAccessRights()) {
        		if(CollectionUtils.isNotEmpty(entityList)) {
        			final Map<String, Resource> dtoMap = transform(dtoList);
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			assertLength(searchBean.getParentIdSet());
		        			final String entityId = searchBean.getParentIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
		        			setRights(dtoMap, xref, entity);
		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
		        			assertLength(searchBean.getChildIdSet());
		        			final String entityId = searchBean.getChildIdSet().iterator().next();
		        			final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
		        			setRights(dtoMap, xref, entity);
		        		}
			        });
	        	}
        	}
        }
	}
}
