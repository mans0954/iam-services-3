package org.openiam.idm.srvc.grp.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;

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
	
	public GroupEntity getGroupByNameAndManagedSystem(final String groupName, final String managedSystemId);
    public List<Group> getCompiledGroupsForUser(final String userId);
    public GroupEntity getGroupByName(final String groupName);

    /**
     * Returns a list of Group objects that satisfy the search criteria defined through the GroupSearch parameter.
     *
     * @param searchBean
     * @return
     */
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final int from, final int size);
    public List<Group> findDtoBeans(final GroupSearchBean searchBean, final int from, final int size);

    public List<GroupEntity> getChildGroups(final String groupId, final int from, final int size);
    public List<GroupEntity> getParentGroups(final String groupId, final int from, final int size);
    public List<GroupEntity> getGroupsForResource(final String resourceId, final int from, final int size);

    public List<Group> getGroupsDtoForUser(final String userId, final int from, final int size);

    public List<GroupEntity> getGroupsForUser(final String userId, final int from, final int size);
    public List<GroupEntity> getGroupsForRole(final String roleId, final int from, final int size);

    public List<Group> getChildGroupsDto(final String groupId, final int from, final int size);
    public List<Group> getParentGroupsDto(final String groupId, final int from, final int size);
    public List<Group> getGroupsDtoForResource(final String resourceId, final int from, final int size);
    public List<Group> getGroupsDtoForRole(final String roleId, final int from, final int size, boolean deepFlag);

    public Set<String> getGroupIdList();

    public int getNumOfGroupsForRole(final String roleId);
    public int getNumOfGroupsForUser(final String userId);
    public int getNumOfGroupsForResource(final String resourceId);
    public int getNumOfChildGroups(final String groupId);
    public int getNumOfParentGroups(final String groupId);
    public int countBeans(final GroupSearchBean searchBean);

    public boolean isValid(final GroupEntity group) throws BasicDataServiceException;
	public void saveGroup(final GroupEntity group) throws BasicDataServiceException;
    public Response saveGroup(final Group group);
    public void saveGroup(final GroupEntity group, final GroupOwner groupOwner) throws BasicDataServiceException;
    public void addRequiredAttributes(GroupEntity group);
    public Response deleteGroup(final String groupId);
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

    public void validateGroup2GroupAddition(final String parentId, final String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException;
    
    public Group getGroupDTO(final String groupId);
    public List<GroupEntity> findGroupsByAttributeValue(String attrName, String attrValue);

    //public List<Group> findGroupsDtoByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity lang);

    public int countGroupsForOwner(GroupSearchBean searchBean, String ownerId);
    public List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String ownerId, int from, int size);
    
    public boolean hasAttachedEntities(String groupId);
    public List<Group> findGroupsDtoForOwner(GroupSearchBean searchBean, String ownerId, int from, int size);
    public void removeRoleFromGroup(String roleId, String groupId);

    public void saveGroupRequest(final GroupRequestModel request) throws Exception;
    public void validateGroupRequest(final GroupRequestModel request) throws Exception;
    //public List<GroupOwner> getOwnersBeansForGroup(String groupId);


    public Response addUserToGroup(final String groupId, final String userId, final Set<String> rightIds,
                                   final Date startDate, final Date endDate);
    public Response removeUserFromGroup(final String groupId, final String userId);

    public Response addChildGroup(final String groupId, final String childGroupId,
                                  final Set<String> rights, final Date startDate, final Date endDate);
    public Response removeChildGroup(final String groupId, final String childGroupId);


    public SaveTemplateProfileResponse saveGroupRequestWeb(final GroupRequestModel request);
}