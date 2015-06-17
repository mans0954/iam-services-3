package org.openiam.idm.srvc.grp.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.role.domain.RoleEntity;

import java.util.List;
import java.util.Set;

/**
 * <code>GroupDataService</code> provides a service to manage groups as well
 * as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 *
 * @author Suneet Shah
 * @version 2.0
 */

public interface GroupDataService {
	@Deprecated
    GroupEntity getGroup(final String id);
	
	@Deprecated
    GroupEntity getGroup(final String id, final String requesterId);
    GroupEntity getGroupByName(final String groupName, final String requesterId);
    List<Group> getCompiledGroupsForUser(final String userId);

    GroupEntity getGroupLocalize(final String id, final LanguageEntity language);
    GroupEntity getGroupLocalize(final String id, final String requesterId, final LanguageEntity language);
    GroupEntity getGroupByNameLocalize(final String groupName, final String requesterId, final LanguageEntity language);
    List<Group> getCompiledGroupsForUserLocalize(final String userId, final LanguageEntity language);
    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param searchBean
     * @return
     */
    List<GroupEntity> findBeans(final GroupSearchBean searchBean, String requesterId, final int from, final int size);
    List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size);
    List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size);
    List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size);

    List<Group> getGroupsDtoForUser(final String userId, String requesterId, final int from, final int size);

    List<GroupEntity> getGroupsForUser(final String userId, String requesterId, final int from, final int size);
    List<GroupEntity> getGroupsForRole(final String roleId, String requesterId, final int from, final int size);

    List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, String requesterId, final int from, final int size, final LanguageEntity language);
    
    @Deprecated
    List<GroupEntity> getChildGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language);
    
    @Deprecated
    List<GroupEntity> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language);
    
    @Deprecated
    List<GroupEntity> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, final LanguageEntity language);
    
    @Deprecated
    List<GroupEntity> getGroupsForUserLocalize(final String userId, String requesterId, final int from, final int size, final LanguageEntity language);
    
    @Deprecated
    List<GroupEntity> getGroupsForRoleLocalize(final String roleId, String requesterId, final int from, final int size, final LanguageEntity language);

    Set<String> getGroupIdList();

    @Deprecated
    int getNumOfGroupsForRole(final String roleId, final String requesterId);
    
    @Deprecated
    int getNumOfGroupsForUser(final String userId, final String requesterId);
    @Deprecated
    int getNumOfGroupsForResource(final String resourceId, final String requesterId);
    
    @Deprecated
    int getNumOfChildGroups(final String groupId, final String requesterId);
    
    @Deprecated
    int getNumOfParentGroups(final String groupId, final String requesterId);
    int countBeans(final GroupSearchBean searchBean, final String requesterId);


	void saveGroup(final GroupEntity group, final String requestorId) throws BasicDataServiceException;
    void saveGroup(final GroupEntity group, final GroupOwner groupOwner, final String requestorId) throws BasicDataServiceException;
    void addRequiredAttributes(GroupEntity group);
	void deleteGroup(final String groupId);

    /**
     * Returns true or false depending on whether a user belongs to a particular
     * group or not. If a group has been marked as "Inherits from Parent", then
     * the system will check to see if the user belongs to one of the parent
     * group objects.
     *
     * @param groupId
     * @param userId
     * @return
     */
    boolean isUserInCompiledGroupList(String groupId, String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute
     */
    void saveAttribute(GroupAttributeEntity attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId
     */
    void removeAttribute(final String attributeId);

    void addChildGroup(final String groupId, final String childGroupId, final Set<String> rights);
    void removeChildGroup(final String groupId, final String childGroupId);
    
    void validateGroup2GroupAddition(final String parentId, final String memberId, final Set<String> rights) throws BasicDataServiceException;
    
    Group getGroupDTO(final String groupId);
    List<GroupEntity> findGroupsByAttributeValue(String attrName, String attrValue);

    Group getGroupDTOLocalize(final String groupId, LanguageEntity language);
    List<GroupEntity> findGroupsByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language);

    int countGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId);
    List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, int from, int size, LanguageEntity languageEntity);
    
    boolean hasAttachedEntities(String groupId);
}