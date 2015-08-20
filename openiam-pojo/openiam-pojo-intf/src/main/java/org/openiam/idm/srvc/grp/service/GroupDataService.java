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
    public GroupEntity getGroup(final String id);
    public GroupEntity getGroup(final String id, final String requesterId);
    public GroupEntity getGroupByName(final String groupName, final String requesterId);
    public List<Group> getCompiledGroupsForUser(final String userId);

    public GroupEntity getGroupLocalize(final String id, final LanguageEntity language);
    public GroupEntity getGroupLocalize(final String id, final String requesterId, final LanguageEntity language);
    public Group getGroupDtoLocalize(final String id, final String requesterId, final Language language);

    public GroupEntity getGroupByNameLocalize(final String groupName, final String requesterId, final LanguageEntity language);
    public List<Group> getCompiledGroupsForUserLocalize(final String userId, final LanguageEntity language);
    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param searchBean
     * @return
     */
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, String requesterId, final int from, final int size);
    public List<Group> findDtoBeans(final GroupSearchBean searchBean, String requesterId, final int from, final int size);

    public List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size);
    public List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size);
    public List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size);

    public List<Group> getGroupsDtoForUser(final String userId, String requesterId, final int from, final int size);

    public List<GroupEntity> getGroupsForUser(final String userId, String requesterId, final int from, final int size);
    public List<GroupEntity> getGroupsForRole(final String roleId, String requesterId, final int from, final int size);

    public List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> findBeansDtoLocalize(final GroupSearchBean searchBean, String requesterId, final int from, final int size, final Language language);
    public List<GroupEntity> getChildGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> getChildGroupsDtoLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language);
    public List<GroupEntity> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> getParentGroupsDtoLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language);
    public List<GroupEntity> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> getGroupsDtoForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, final Language language);
    public List<GroupEntity> getGroupsForUserLocalize(final String userId, String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> getGroupsDtoForUserLocalize(final String userId, String requesterId, final int from, final int size, final Language language);
    public List<GroupEntity> getGroupsForRoleLocalize(final String roleId, String requesterId, final int from, final int size, final LanguageEntity language);
    public List<Group> getGroupsDtoForRoleLocalize(final String roleId, String requesterId, final int from, final int size, final Language language, boolean deepFlag);

    public Set<String> getGroupIdList();

    public int getNumOfGroupsForRole(final String roleId, final String requesterId);
    public int getNumOfGroupsForUser(final String userId, final String requesterId);
    public int getNumOfGroupsForResource(final String resourceId, final String requesterId);
    public int getNumOfChildGroups(final String groupId, final String requesterId);
    public int getNumOfParentGroups(final String groupId, final String requesterId);
    public int countBeans(final GroupSearchBean searchBean, final String requesterId);


	public void saveGroup(final GroupEntity group, final String requestorId) throws BasicDataServiceException;
    public void saveGroup(final GroupEntity group, final GroupOwner groupOwner, final String requestorId) throws BasicDataServiceException;
    public void addRequiredAttributes(GroupEntity group);
	public void deleteGroup(final String groupId);

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
    public boolean isUserInCompiledGroupList(String groupId, String userId);

    /**
     * Adds an attribute to the Group object.
     *
     * @param attribute
     */
    public void saveAttribute(GroupAttributeEntity attribute);

    /**
     * Removes a GroupAttribute specified by the attribute.
     *
     * @param attributeId
     */
    public void removeAttribute(final String attributeId);

    public void addChildGroup(final String groupId, final String childGroupId);
    public void removeChildGroup(final String groupId, final String childGroupId);
    
    public void validateGroup2GroupAddition(final String parentId, final String memberId) throws BasicDataServiceException;
    
    public Group getGroupDTO(final String groupId);
    public List<GroupEntity> findGroupsByAttributeValue(String attrName, String attrValue);

    public Group getGroupDTOLocalize(final String groupId, LanguageEntity language);
    public List<GroupEntity> findGroupsByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language);
    public List<Group> findGroupsDtoByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language);

    public int countGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId);
    public List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, int from, int size, LanguageEntity languageEntity);

    public List<Group> findGroupsDtoForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, int from, int size, Language language);
}