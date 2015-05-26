package org.openiam.idm.srvc.access.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
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
import org.openiam.idm.srvc.role.dto.Role;
import org.springframework.stereotype.Component;

@Component
public class AccessRightProcessor {

	public void process(final OrganizationSearchBean searchBean, final List<Organization> dtoList, final List<OrganizationEntity> entityList) {
		if(searchBean != null) {
        	if(searchBean.isIncludeAccessRights()) {
        		if(CollectionUtils.isNotEmpty(entityList)) {
        			final Map<String, Organization> dtoMap = new HashMap<String, Organization>();
        			dtoList.forEach(e -> {
        				dtoMap.put(e.getId(), e);
        			});
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			/* it makes no logical sense if you're asking for multiple parentIds, and wanting access rights */
		        			if(CollectionUtils.size(searchBean.getParentIdSet()) > 1) {
		        				throw new IllegalArgumentException("Can only have one parent ID if including access rights");
		        			}
		        			final String parentId = searchBean.getParentIdSet().iterator().next();
		        			final OrgToOrgMembershipXrefEntity xref = entity.getParent(parentId);
		        			if(xref != null && xref.getRights() != null) {
		        				final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
		        				dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
		        			}
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
        			final Map<String, Role> dtoMap = new HashMap<String, Role>();
        			dtoList.forEach(e -> {
        				dtoMap.put(e.getId(), e);
        			});
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			/* it makes no logical sense if you're asking for multiple parentIds, and wanting access rights */
		        			if(CollectionUtils.size(searchBean.getParentIdSet()) > 1) {
		        				throw new IllegalArgumentException("Can only have one parent ID if including access rights");
		        			}
		        			final String parentId = searchBean.getParentIdSet().iterator().next();
		        			final RoleToRoleMembershipXrefEntity xref = entity.getParent(parentId);
		        			if(xref != null && xref.getRights() != null) {
		        				final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
		        				dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
		        			}
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
        			final Map<String, Group> dtoMap = new HashMap<String, Group>();
        			dtoList.forEach(e -> {
        				dtoMap.put(e.getId(), e);
        			});
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			/* it makes no logical sense if you're asking for multiple parentIds, and wanting access rights */
		        			if(CollectionUtils.size(searchBean.getParentIdSet()) > 1) {
		        				throw new IllegalArgumentException("Can only have one parent ID if including access rights");
		        			}
		        			final String parentId = searchBean.getParentIdSet().iterator().next();
		        			final GroupToGroupMembershipXrefEntity xref = entity.getParent(parentId);
		        			if(xref != null && xref.getRights() != null) {
		        				final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
		        				dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
		        			}
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
        			final Map<String, Resource> resourceMap = new HashMap<String, Resource>();
        			dtoList.forEach(e -> {
        				resourceMap.put(e.getId(), e);
        			});
        			entityList.forEach(entity -> {
		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
		        			/* it makes no logical sense if you're asking for multiple parentIds, and wanting access rights */
		        			if(CollectionUtils.size(searchBean.getParentIdSet()) > 1) {
		        				throw new IllegalArgumentException("Can only have one parent ID if including access rights");
		        			}
		        			final String parentId = searchBean.getParentIdSet().iterator().next();
		        			final ResourceToResourceMembershipXrefEntity xref = entity.getParent(parentId);
		        			if(xref != null && xref.getRights() != null) {
		        				final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
		        				resourceMap.get(entity.getId()).setAccessRightIds(rightIds);
		        			}
		        		}
			        });
	        	}
        	}
        }
	}
}
