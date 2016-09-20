package org.openiam.idm.srvc.access.service;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.KeyDTO;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.entitlements.AbstractEntitlementsDTO;
import org.openiam.idm.srvc.membership.domain.AbstractMembershipXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.springframework.stereotype.Component;

import java.util.*;

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
 * <p/>
 * Access Rights are uni-directional, and are enforced FROM the parent TO the child.
 * <p/>
 * The meaning of the access rights depends on how you request it.
 * It is up to the caller to interpret the meaning
 *
 * @author lbornova
 */
@Component
public class AccessRightProcessor {

    private <T extends KeyDTO> Map<String, T> transform(final List<T> dtoList) {
        Map<String, T> retVal = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dtoList)) {
            for (T t : dtoList) {
                retVal.put(t.getId(), t);
            }
        }
        return retVal;

    }

    private <T extends AbstractEntitlementsDTO> void setRights(final Map<String, T> dtoMap, final AbstractMembershipXrefEntity<?, ?> xref, final KeyEntity entity) {
        if (xref != null && xref.getRights() != null) {
            final List<String> rightIds = new ArrayList<>();
            for (AccessRightEntity e : xref.getRights()) {
                rightIds.add(e.getId());
            }
            dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
        }
    }

    //
//	private void setRightsForUser(final Map<String, User> dtoMap, final AbstractMembershipXrefEntity<?, ?> xref, final KeyEntity entity) {
//		if(xref != null && xref.getRights() != null) {
//			final List<String> rightIds = xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toList());
//			dtoMap.get(entity.getId()).setAccessRightIds(rightIds);
//			dtoMap.get(entity.getId()).setAccessRightStartDate(xref.getStartDate());
//			dtoMap.get(entity.getId()).setAccessRightEndDate(xref.getEndDate());
//		}
//	}
//
    private void assertLength(final Set<String> set) {
        if (CollectionUtils.size(set) > 1) {
            throw new IllegalArgumentException("When querying for rights, you can only query by one object at a time.  See stacktrace for details as to where this happens");
        }
    }

    //
//	public void process(final UserSearchBean searchBean, final List<User> dtoList, final List<UserEntity> entityList) {
//		if(searchBean != null) {
//        	if(searchBean.isIncludeAccessRights()) {
//        		if(CollectionUtils.isNotEmpty(entityList)) {
//        			final Map<String, User> dtoMap = transform(dtoList);
//        			entityList.forEach(entity -> {
//        				if(CollectionUtils.isNotEmpty(searchBean.getResourceIdSet())) {
//        					assertLength(searchBean.getResourceIdSet());
//        					final String entityId = searchBean.getResourceIdSet().iterator().next();
//        					final AbstractMembershipXrefEntity xref = entity.getResource(entityId);
//        					setRightsForUser(dtoMap, xref, entity);
//        				} else if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
//        					assertLength(searchBean.getGroupIdSet());
//        					final String entityId = searchBean.getGroupIdSet().iterator().next();
//        					final AbstractMembershipXrefEntity xref = entity.getGroup(entityId);
//        					setRightsForUser(dtoMap, xref, entity);
//        				} else if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
//        					assertLength(searchBean.getRoleIdSet());
//        					final String entityId = searchBean.getRoleIdSet().iterator().next();
//        					final AbstractMembershipXrefEntity xref = entity.getRole(entityId);
//        					setRightsForUser(dtoMap, xref, entity);
//        				} else if(CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
//        					assertLength(searchBean.getOrganizationIdSet());
//        					final String entityId = searchBean.getOrganizationIdSet().iterator().next();
//        					final AbstractMembershipXrefEntity xref = entity.getAffiliation(entityId);
//        					setRightsForUser(dtoMap, xref, entity);
//        				}
//        			});
//        		}
//        	}
//		}
//	}
//
//	public void process(final OrganizationSearchBean searchBean, final List<Organization> dtoList, final List<OrganizationEntity> entityList) {
//		if(searchBean != null) {
//        	if(searchBean.isIncludeAccessRights()) {
//        		if(CollectionUtils.isNotEmpty(entityList)) {
//        			final Map<String, Organization> dtoMap = transform(dtoList);
//        			entityList.forEach(entity -> {
//		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
//		        			assertLength(searchBean.getParentIdSet());
//		        			final String queryId = searchBean.getParentIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getParent(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
//		        			assertLength(searchBean.getChildIdSet());
//		        			final String queryId = searchBean.getChildIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getChild(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
//		        			assertLength(searchBean.getGroupIdSet());
//		        			final String queryId = searchBean.getGroupIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getGroup(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
//		        			assertLength(searchBean.getRoleIdSet());
//		        			final String queryId = searchBean.getRoleIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getRole(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getResourceIdSet())) {
//		        			assertLength(searchBean.getResourceIdSet());
//		        			final String queryId = searchBean.getResourceIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getResource(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getUserIdSet())) {
//		        			assertLength(searchBean.getUserIdSet());
//		        			final String entityId = searchBean.getUserIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getUser(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		}
//			        });
//	        	}
//        	}
//        }
//	}
//
    public void process(final RoleSearchBean searchBean, final List<Role> dtoList, final List<RoleEntity> entityList) {
        if (searchBean != null) {
            if (searchBean.isIncludeAccessRights()) {
                if (CollectionUtils.isNotEmpty(entityList)) {
                    final Map<String, Role> dtoMap = transform(dtoList);
                    for (RoleEntity entity : entityList) {
                        if (CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
                            assertLength(searchBean.getParentIdSet());
                            final String entityId = searchBean.getParentIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
                            setRights(dtoMap, xref, entity);
                        } else if (CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
                            assertLength(searchBean.getChildIdSet());
                            final String entityId = searchBean.getChildIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
                            setRights(dtoMap, xref, entity);
                        }
//		        		else if(CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
//		        			assertLength(searchBean.getOrganizationIdSet());
//		        			final String queryId = searchBean.getOrganizationIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getOrganization(queryId);
//		        			setRights(dtoMap, xref, entity);
//		        		}
                        else if (CollectionUtils.isNotEmpty(searchBean.getResourceIdSet())) {
                            assertLength(searchBean.getResourceIdSet());
                            final String entityId = searchBean.getResourceIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getResource(entityId);
                            setRights(dtoMap, xref, entity);
                        }
//                        else if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
//                            assertLength(searchBean.getGroupIdSet());
//                            final String entityId = searchBean.getGroupIdSet().iterator().next();
//                            final AbstractMembershipXrefEntity xref = entity.getGroup(entityId);
//                            setRights(dtoMap, xref, entity);
//                        } else if (CollectionUtils.isNotEmpty(searchBean.getUserIdSet())) {
//                            assertLength(searchBean.getUserIdSet());
//                            final String entityId = searchBean.getUserIdSet().iterator().next();
//                            final AbstractMembershipXrefEntity xref = entity.getUser(entityId);
//                            setRights(dtoMap, xref, entity);
//                        }
                    }
                }
            }
        }
    }
//
//	public void process(final GroupSearchBean searchBean, final List<Group> dtoList, final List<GroupEntity> entityList) {
//		if(searchBean != null) {
//        	if(searchBean.isIncludeAccessRights()) {
//        		if(CollectionUtils.isNotEmpty(entityList)) {
//        			final Map<String, Group> dtoMap = transform(dtoList);
//        			entityList.forEach(entity -> {
//		        		if(CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
//		        			assertLength(searchBean.getParentIdSet());
//		        			final String entityId = searchBean.getParentIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
//		        			assertLength(searchBean.getChildIdSet());
//		        			final String entityId = searchBean.getChildIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
//		        			assertLength(searchBean.getOrganizationIdSet());
//		        			final String entityId = searchBean.getOrganizationIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getOrganization(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getResourceIdSet())) {
//		        			assertLength(searchBean.getResourceIdSet());
//		        			final String entityId = searchBean.getResourceIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getResource(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
//		        			assertLength(searchBean.getRoleIdSet());
//		        			final String entityId = searchBean.getRoleIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getRole(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		} else if(CollectionUtils.isNotEmpty(searchBean.getUserIdSet())) {
//		        			assertLength(searchBean.getUserIdSet());
//		        			final String entityId = searchBean.getUserIdSet().iterator().next();
//		        			final AbstractMembershipXrefEntity xref = entity.getUser(entityId);
//		        			setRights(dtoMap, xref, entity);
//		        		}
//			        });
//	        	}
//        	}
//        }
//	}
//

    public void process(final ResourceSearchBean searchBean, final List<Resource> dtoList, final List<ResourceEntity> entityList) {
        if (searchBean != null) {
            if (searchBean.isIncludeAccessRights()) {
                if (CollectionUtils.isNotEmpty(entityList)) {
                    final Map<String, Resource> dtoMap = transform(dtoList);
                    for (ResourceEntity entity : entityList) {
                        if (CollectionUtils.isNotEmpty(searchBean.getParentIdSet())) {
                            assertLength(searchBean.getParentIdSet());
                            final String entityId = searchBean.getParentIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getParent(entityId);
                            setRights(dtoMap, xref, entity);
                        } else if (CollectionUtils.isNotEmpty(searchBean.getChildIdSet())) {
                            assertLength(searchBean.getChildIdSet());
                            final String entityId = searchBean.getChildIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getChild(entityId);
                            setRights(dtoMap, xref, entity);
                        }

//                        else if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
//                            assertLength(searchBean.getOrganizationIdSet());
//                            final String entityId = searchBean.getOrganizationIdSet().iterator().next();
//                            final AbstractMembershipXrefEntity xref = entity.getOrganization(entityId);
//                            setRights(dtoMap, xref, entity);
//                        } else if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
//                            assertLength(searchBean.getGroupIdSet());
//                            final String entityId = searchBean.getGroupIdSet().iterator().next();
//                            final AbstractMembershipXrefEntity xref = entity.getGroup(entityId);
//                            setRights(dtoMap, xref, entity);
//                        }
                        else if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
                            assertLength(searchBean.getRoleIdSet());
                            final String entityId = searchBean.getRoleIdSet().iterator().next();
                            final AbstractMembershipXrefEntity xref = entity.getRole(entityId);
                            setRights(dtoMap, xref, entity);
                        }
// else if (CollectionUtils.isNotEmpty(searchBean.getUserIdSet())) {
//                            assertLength(searchBean.getUserIdSet());
//                            final String entityId = searchBean.getUserIdSet().iterator().next();
//                            final AbstractMembershipXrefEntity xref = entity.getUser(entityId);
//                            setRights(dtoMap, xref, entity);
//                        }
                    }
                }
            }
        }
    }
}
