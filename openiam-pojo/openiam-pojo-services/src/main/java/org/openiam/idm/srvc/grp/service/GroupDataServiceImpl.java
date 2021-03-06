package org.openiam.idm.srvc.grp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityDAO;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.openiam.util.ws.collection.StringUtil;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <code>GroupDataServiceImpl</code> provides a service to manage groups as
 * well as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 *
 * @author Suneet Shah
 * @version 2.0
 */

@Service("groupManager")
public class GroupDataServiceImpl implements GroupDataService, ApplicationContextAware {
    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private GroupDAO groupDao;

    @Autowired
    private RoleDAO roleDao;

    @Autowired
    private ResourceDAO resourceDao;

    @Autowired
    private GroupAttributeDAO groupAttrDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;

    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Value("${org.openiam.resource.admin.resource.type.id}")
    private String adminResourceTypeId;

    @Autowired
    private ResourceTypeDAO resourceTypeDAO;

    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private MetadataTypeDAO typeDAO;

    @Autowired
    private MetadataElementDAO metadataElementDAO;

    @Autowired
    private IdentityDAO identityDAO;

    @Autowired
    protected LanguageDAO languageDAO;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    private LanguageDozerConverter languageConverter;

    @Autowired
    private MetadataElementTemplateService pageTemplateService;
    @Autowired
    private ApproverAssociationDAO approverAssociationDao;
    private ApplicationContext ac;


    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @Autowired
    @Qualifier("authorizationManagerAdminService")
    private AuthorizationManagerAdminService authorizationManagerAdminService;

    private static final Log log = LogFactory.getLog(GroupDataServiceImpl.class);

    public GroupDataServiceImpl() {

    }

    protected LanguageEntity getDefaultLanguage() {
        return languageDAO.getDefaultLanguage();
    }


    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public GroupEntity getGroupLocalize(String id, LanguageEntity languageEntity) {
        return getGroupLocalize(id, null, languageEntity);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public GroupEntity getGroupLocalize(String id, String requesterId, LanguageEntity language) {
        if (DelegationFilterHelper.isAllowed(id, getDelegationFilter(requesterId))) {
            return groupDao.findById(id);
        }
        return null;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public Group getGroupDtoLocalize(final String id, final String requesterId, final Language language) {
        /*if(DelegationFilterHelper.isAllowed(id, getDelegationFilter(requesterId))){*/

        //GroupDataService bean = (GroupDataService)ac.getBean("groupManager");
        GroupEntity groupEntity = this.getProxyService().getGroupLocalize(id, requesterId, languageConverter.convertToEntity(language, false));

        if (groupEntity != null) {
            //GroupEntity groupEntity = groupDao.findById(id);
            return groupDozerConverter.convertToDTO(groupEntity, true);
        }
        return null;
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public GroupEntity getGroup(final String id) {
        return getGroupLocalize(id, null, getDefaultLanguage());
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public GroupEntity getGroup(final String id, final String requesterId) {
        return getGroupLocalize(id, requesterId, getDefaultLanguage());
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public GroupEntity getGroupByName(final String groupName, final String requesterId) {
        return getGroupByNameLocalize(groupName, requesterId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public GroupEntity getGroupByNameLocalize(final String groupName, final String requesterId, final LanguageEntity language) {
        final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setName(groupName);
        final List<GroupEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getChildGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> getChildGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language) {
        return groupDao.getChildGroups(groupId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getChildGroupsDtoLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language) {
        List<GroupEntity> groupEntities = groupDao.getChildGroups(groupId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getParentGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language) {
        return groupDao.getParentGroups(groupId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getParentGroupsDtoLocalize(final String groupId, final String requesterId, final int from, final int size, final Language language) {
        List<GroupEntity> groupEntities = groupDao.getParentGroups(groupId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    /**
     * Without @localization for internal use only
     */
    @Transactional(readOnly = true)
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final String requesterId, int from, int size) {
        return getGroupEntities(searchBean, requesterId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findDtoBeans(final GroupSearchBean searchBean, final String requesterId, int from, int size) {

        //GroupDataService bean = (GroupDataService)ac.getBean("groupManager");
        List<GroupEntity> groupEntities = this.getProxyService().findBeans(searchBean, requesterId, from, size);

        //List<GroupEntity> groupEntities = getGroupEntities(searchBean, requesterId, from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    private List<GroupEntity> getGroupEntities(GroupSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<GroupEntity>(0);
        }
        return groupDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, final String requesterId, int from, int size, final LanguageEntity language) {
        return getGroupEntities(searchBean, requesterId, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> findBeansDtoLocalize(final GroupSearchBean searchBean, final String requesterId, int from, int size, final Language language) {
        //List<GroupEntity> groupEntities = getGroupEntities(searchBean, requesterId, from, size);
        List<GroupEntity> groupEntities = this.getProxyService().findBeansLocalize(searchBean, requesterId, from, size, languageConverter.convertToEntity(language, false));
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId,
                                                int from, int size, LanguageEntity languageEntity) {
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId, getDefaultLanguage());

        if (from > -1 && size > -1) {
            if (finalizedGroups != null && finalizedGroups.size() >= from) {
                int to = from + size;
                if (to > finalizedGroups.size()) {
                    to = finalizedGroups.size();
                }
                finalizedGroups = new ArrayList<GroupEntity>(finalizedGroups.subList(from, to));
            }
        }
        return finalizedGroups;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findGroupsDtoForOwner(GroupSearchBean searchBean, String requesterId, String ownerId,
                                             int from, int size, Language language) {
        /*List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId, getDefaultLanguage());

        if (from > -1 && size > -1) {
            if (finalizedGroups != null && finalizedGroups.size() >= from) {
                int to = from + size;
                if (to > finalizedGroups.size()) {
                    to = finalizedGroups.size();
                }
                finalizedGroups = new ArrayList<GroupEntity>(finalizedGroups.subList(from, to));
            }
        }*/
        List<GroupEntity> finalizedGroups = this.getProxyService().findGroupsForOwner(searchBean, requesterId, ownerId, from, size, languageConverter.convertToEntity(language, false));

        return groupDozerConverter.convertToDTOList(finalizedGroups, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForUser(String userId, String requesterId, int from, int size) {
        List<GroupEntity> groupEntities = groupDao.getGroupsForUser(userId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    /**
     * without localization, for internal use only
     */
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsForUser(final String userId, final String requesterId, int from, int size) {
        return getGroupsForUserLocalize(userId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsForUserLocalize(final String userId, final String requesterId, int from, int size, LanguageEntity language) {
        return groupDao.getGroupsForUser(userId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForUserLocalize(final String userId, final String requesterId, int from, int size, Language language) {
        List<GroupEntity> groupEntities = groupDao.getGroupsForUser(userId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    @Deprecated
    public List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size) {
        return getGroupsForResourceLocalize(resourceId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, LanguageEntity language) {
        return groupDao.getGroupsForResource(resourceId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, Language language) {
        List<GroupEntity> groupEntities = groupDao.getGroupsForResource(resourceId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsForRole(final String roleId, final String requesterId, int from, int size) {
        return getGroupsForRoleLocalize(roleId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> getGroupsForRoleLocalize(final String roleId, final String requesterId, int from, int size, LanguageEntity language) {
        return groupDao.getGroupsForRole(roleId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForRoleLocalize(final String roleId, final String requesterId, int from, int size, boolean deepFlag, Language language) {
        List<GroupEntity> groupEntities = groupDao.getGroupsForRole(roleId, getDelegationFilter(requesterId), from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, deepFlag);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        return groupDao.getNumOfGroupsForRole(roleId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfGroupsForResource(final String resourceId, final String requesterId) {
        return groupDao.getNumOfGroupsForResource(resourceId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        return groupDao.getNumOfGroupsForUser(userId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getGroupIdList() {
        List<String> groupIds = groupDao.getAllIds();
        if (CollectionUtils.isNotEmpty(groupIds))
            return new HashSet<String>(groupIds);
        return Collections.EMPTY_SET;
    }

    @Override
    @Transactional(readOnly = true)
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return 0;
        }
        return groupDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public int countGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId) {
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId, getDefaultLanguage());
        return finalizedGroups.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupOwner> getOwnersBeansForGroup(String groupId) {
        List<GroupOwner> result = new ArrayList<>();

        if (StringUtils.isNotBlank(groupId)) {
            GroupEntity groupEntity = groupDao.findById(groupId);
            result = getOwnersBeansForGroup(groupEntity);
        }
        return result;
    }

    private List<GroupOwner> getOwnersBeansForGroup(GroupEntity groupEntity) {
        List<GroupOwner> result = new ArrayList<>();

        if (groupEntity != null) {
            ResourceEntity adminResource = groupEntity.getAdminResource();
            if (adminResource != null) {
                if (CollectionUtils.isNotEmpty(adminResource.getUsers())) {
                    for (UserEntity usr : adminResource.getUsers()) {
                        GroupOwner owner = new GroupOwner();
                        owner.setType("user");
                        owner.setId(usr.getId());
                        result.add(owner);
                    }
                }
                if (CollectionUtils.isNotEmpty(adminResource.getGroups())) {
                    for (GroupEntity grp : adminResource.getGroups()) {
                        GroupOwner owner = new GroupOwner();
                        owner.setType("group");
                        owner.setId(owner.getId());
                        owner.setName(grp.getName());
                        result.add(owner);
                    }
                }
            }
        }
        return result;
    }

    private List<GroupEntity> getGroupListForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, LanguageEntity languageEntity) {
        List<GroupEntity> foundGroups = findBeansLocalize(searchBean, requesterId, -1, -1, languageEntity);
        List<GroupEntity> finalizedGroups = new ArrayList<>();

        Set<String> foundGroupsId = new HashSet<>();
        if (CollectionUtils.isNotEmpty(foundGroups)) {
            for (GroupEntity grp : foundGroups) {
                foundGroupsId.add(grp.getId());
            }
        }
        HashMap<String, Set<String>> ownersMap = authorizationManagerAdminService.getOwnerIdsForGroupSet(foundGroupsId);
        for (GroupEntity grp : foundGroups) {
            Set<String> idsResp = ownersMap.get(grp.getId());
            if (idsResp != null && CollectionUtils.isNotEmpty(idsResp) && idsResp.contains(ownerId)) {
                finalizedGroups.add(grp);
            }
        }
        return finalizedGroups;
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfChildGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfParentGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<Group> getCompiledGroupsForUser(final String userId) {
        return getCompiledGroupsForUserLocalize(userId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getCompiledGroupsForUserLocalize(final String userId, final LanguageEntity language) {
        final List<GroupEntity> groupList = this.getGroupsForUser(userId, null, 0, Integer.MAX_VALUE);
        final Set<GroupEntity> visitedSet = new HashSet<GroupEntity>();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (final GroupEntity group : groupList) {
                visitGroups(group, visitedSet);
            }
        }
        return groupDozerConverter.convertToDTOList(new ArrayList<GroupEntity>(visitedSet), true);
    }

    @Transactional(readOnly = true)
    public boolean isUserInCompiledGroupList(String groupId, String userId) {
        if (groupId != null) {
            final List<Group> userGroupList = getCompiledGroupsForUser(userId);
            if (CollectionUtils.isNotEmpty(userGroupList)) {
                for (Group grp : userGroupList) {
                    if (grp.getId().equalsIgnoreCase(groupId)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    @Override
    @Transactional
    public void saveGroup(final GroupEntity group, final String requestorId) throws BasicDataServiceException {
        saveGroup(group, null, requestorId);
    }

    @Override
    @Transactional
    public void saveGroup(final GroupEntity group, final GroupOwner groupOwner, final String requestorId) throws BasicDataServiceException {
        if (group != null && entityValidator.isValid(group)) {

            if (group.getManagedSystem() != null && group.getManagedSystem().getId() != null) {
                ManagedSysEntity mngSys = managedSysDAO.findById(group.getManagedSystem().getId());
                group.setManagedSystem(managedSysDAO.findById(group.getManagedSystem().getId()));
                if (StringUtils.isNotBlank(mngSys.getResourceId())) {
                    group.addResource(resourceDao.findById(mngSys.getResourceId()));
                }

            } else {
                group.setManagedSystem(null);
            }

            if (CollectionUtils.isNotEmpty(group.getOrganizationSet())) {
                Set<String> ids = new HashSet<>();
                for (OrganizationEntity org : group.getOrganizationSet()) {
                    if (StringUtils.isNotBlank(org.getId()))
                        ids.add(org.getId());
                }
                if (CollectionUtils.isNotEmpty(ids)) {
                    group.setOrganizationSet(new HashSet<>(organizationDAO.findByIds(ids)));
                }
            } else {
                group.setOrganizationSet(null);
            }

            if (group.getType() != null && StringUtils.isNotBlank(group.getType().getId())) {
                group.setType(typeDAO.findById(group.getType().getId()));
            } else {
                group.setType(null);
            }

            if (group.getClassification() != null && StringUtils.isNotBlank(group.getClassification().getId())) {
                group.setClassification(typeDAO.findById(group.getClassification().getId()));
            } else {
                group.setClassification(null);
            }
            if (group.getAdGroupType() != null && StringUtils.isNotBlank(group.getAdGroupType().getId())) {
                group.setAdGroupType(typeDAO.findById(group.getAdGroupType().getId()));
            } else {
                group.setAdGroupType(null);
            }
            if (group.getAdGroupScope() != null && StringUtils.isNotBlank(group.getAdGroupScope().getId())) {
                group.setAdGroupScope(typeDAO.findById(group.getAdGroupScope().getId()));
            } else {
                group.setAdGroupScope(null);
            }
            if (group.getRisk() != null && StringUtils.isNotBlank(group.getRisk().getId())) {
                group.setRisk(typeDAO.findById(group.getRisk().getId()));
            } else {
                group.setRisk(null);
            }
            if (StringUtils.isNotBlank(group.getId())) {
                final GroupEntity dbGroup = groupDao.findById(group.getId());
                if (dbGroup != null) {
                    group.setApproverAssociations(dbGroup.getApproverAssociations());

                    mergeAttribute(group, dbGroup, requestorId);
                    group.setChildGroups(dbGroup.getChildGroups());
                    mergeParent(group, dbGroup, requestorId);
                    group.setResources(dbGroup.getResources());
                    group.setRoles(dbGroup.getRoles());
                    group.setUsers(dbGroup.getUsers());
                    group.setAdminResource(dbGroup.getAdminResource());
                    group.setLastUpdatedBy(requestorId);
                    group.setLastUpdate(Calendar.getInstance().getTime());
                    if (group.getAdminResource() == null) {
                        group.setAdminResource(getNewAdminResource(group, groupOwner, requestorId));
                    }
                    group.getAdminResource().setCoorelatedName(group.getName());
                    mergeGroupOwner(group, groupOwner, requestorId);
                } else {
                    return;
                }
                groupDao.merge(group);
            } else {
                if (CollectionUtils.isNotEmpty(group.getParentGroups())) {
                    Set<String> ids = new HashSet<>();
                    for (GroupEntity grp : group.getParentGroups()) {
                        if (StringUtils.isNotBlank(grp.getId()))
                            ids.add(grp.getId());
                    }
                    if (CollectionUtils.isNotEmpty(ids)) {
                        group.setParentGroups(new HashSet<>(groupDao.findByIds(ids)));
                    }
                } else {
                    group.setParentGroups(null);
                }
                group.setAdminResource(getNewAdminResource(group, groupOwner, requestorId));
                group.setCreatedBy(requestorId);
                group.setCreateDate(Calendar.getInstance().getTime());
                groupDao.save(group);

                IdentityEntity groupDefaultEntity = new IdentityEntity(IdentityTypeEnum.GROUP);
                groupDefaultEntity.setCreateDate(new Date());
                groupDefaultEntity.setCreatedBy(requestorId);
                groupDefaultEntity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
                groupDefaultEntity.setReferredObjectId(group.getId());
                groupDefaultEntity.setStatus(LoginStatusEnum.ACTIVE);
                groupDefaultEntity.setIdentity(group.getName());
                identityDAO.save(groupDefaultEntity);

                group.addApproverAssociation(createDefaultApproverAssociations(group, requestorId));

                addRequiredAttributes(group);
            }
        }
    }

    @Override
    @Transactional
    public void addRequiredAttributes(GroupEntity group) {
        if (group != null && group.getType() != null && StringUtils.isNotBlank(group.getType().getId())) {
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(group.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if (CollectionUtils.isNotEmpty(elementList)) {
                for (MetadataElementEntity element : elementList) {
                    //if(element.isRequired()){
                    groupAttrDao.save(AttributeUtil.buildGroupAttribute(group, element));
                    //}
                }
            }
        }
    }

    private void mergeGroupOwner(GroupEntity group, GroupOwner groupOwner, String requestorId) {
        // get data from DB
        List<GroupOwner> owners = getOwnersBeansForGroup(group);
        if (owners == null || !owners.contains(groupOwner)) {
            // get admin resource
            ResourceEntity adminResource = group.getAdminResource();
            // clean users and groups
            adminResource.getUsers().clear();
            adminResource.getGroups().clear();
            // add new Owner;
            addOwner(adminResource, groupOwner, requestorId);
        }
    }

    private ApproverAssociationEntity createDefaultApproverAssociations(final GroupEntity entity, final String requestorId) {
        final ApproverAssociationEntity association = new ApproverAssociationEntity();
        association.setAssociationEntityId(entity.getId());
        association.setAssociationType(AssociationType.GROUP);
        association.setApproverLevel(Integer.valueOf(0));
        association.setApproverEntityId(requestorId);
        association.setApproverEntityType(AssociationType.USER);
        return association;
    }

    private ResourceEntity getNewAdminResource(final GroupEntity entity, final GroupOwner groupOwner, final String requestorId) {
        final ResourceEntity adminResource = new ResourceEntity();
        adminResource.setName(String.format("GRP_ADMIN_%s_%s", entity.getName(), RandomStringUtils.randomAlphanumeric(2)));
        adminResource.setResourceType(resourceTypeDAO.findById(adminResourceTypeId));

        adminResource.setCoorelatedName(entity.getName());

        addOwner(adminResource, groupOwner, requestorId);
        return adminResource;
    }

    private void addOwner(ResourceEntity adminResource, GroupOwner groupOwner, final String requestorId) {
        if (groupOwner != null && StringUtils.isNotBlank(groupOwner.getId())) {
            if ("user".equals(groupOwner.getType())) {
                adminResource.addUser(userDAO.findById(groupOwner.getId()));
            } else if ("group".equals(groupOwner.getType())) {
                adminResource.addGroup(groupDao.findById(groupOwner.getId()));
            } else {
                adminResource.addUser(userDAO.findById(requestorId));
            }
        } else {
            adminResource.addUser(userDAO.findById(requestorId));
        }
    }

    private void mergeParent(final GroupEntity bean, final GroupEntity dbObject, final String requesterId) {
        Set<GroupEntity> beanParents = (bean.getParentGroups() != null) ? bean.getParentGroups() : new HashSet<GroupEntity>();
        Set<GroupEntity> dbParents = (dbObject.getParentGroups() != null) ? dbObject.getParentGroups() : new HashSet<GroupEntity>();

        /* update */
        Iterator<GroupEntity> dbIterator = dbParents.iterator();
        while (dbIterator.hasNext()) {
            final GroupEntity dbParent = dbIterator.next();

            boolean contains = false;
            for (final GroupEntity beanParent : beanParents) {
                if (StringUtils.equals(dbParent.getId(), beanParent.getId())) {
                    contains = true;
                    break;
                }
            }
            /* remove */
            if (!contains) {
                auditLogRemoveParent(bean, dbParent, requesterId);
                dbIterator.remove();
            }
        }

        /* add */
        final Set<String> toAddIds = new HashSet<>();
        for (final GroupEntity beanParent : beanParents) {
            boolean contains = false;
            dbIterator = dbParents.iterator();
            while (dbIterator.hasNext()) {
                final GroupEntity dbParent = dbIterator.next();
                if (StringUtils.equals(dbParent.getId(), beanParent.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                toAddIds.add(beanParent.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(toAddIds)) {
            dbParents.addAll(groupDao.findByIds(toAddIds));
        }
        bean.setParentGroups(dbParents);
    }

    private void mergeAttribute(final GroupEntity bean, final GroupEntity dbObject, final String requesterId) {
        Set<GroupAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<GroupAttributeEntity>();
        Set<GroupAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? new HashSet<GroupAttributeEntity>(dbObject.getAttributes()) : new HashSet<GroupAttributeEntity>();

        /* update */
        Iterator<GroupAttributeEntity> dbIteroator = dbProps.iterator();
        while (dbIteroator.hasNext()) {
            final GroupAttributeEntity dbProp = dbIteroator.next();

            boolean contains = false;
            for (final GroupAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setElement(getEntity(beanProp.getElement()));
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if (!contains) {
                auditLogRemoveAttribute(bean, dbProp, requesterId);
                dbIteroator.remove();
            }
        }

        /* add */
        final Set<GroupAttributeEntity> toAdd = new HashSet<>();
        for (final GroupAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while (dbIteroator.hasNext()) {
                final GroupAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setGroup(bean);
                beanProp.setElement(getEntity(beanProp.getElement()));
                auditLogAddAttribute(bean, beanProp, requesterId);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);

        bean.setAttributes(dbProps);
    }

    private void auditLogRemoveParent(final GroupEntity group, final GroupEntity parent, final String requesterId) {
        // Audit Log -----------------------------------------------------------------------------------
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setAction(AuditAction.REMOVE_PARENT_GROUP.value());
        auditLog.addCustomRecord(parent.getName(), parent.getId());
        auditLogService.enqueue(auditLog);
    }


    private void auditLogRemoveAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId) {
        // Audit Log -----------------------------------------------------------------------------------
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
        auditLog.addCustomRecord(groupAttr.getName(), groupAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

    private void auditLogAddAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId) {
        // Audit Log -----------------------------------------------------------------------------------
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
        auditLog.addCustomRecord(groupAttr.getName(), groupAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

    private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
        if (bean != null && StringUtils.isNotBlank(bean.getId())) {
            return metadataElementDAO.findById(bean.getId());
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteGroup(String groupId) {
        final GroupEntity entity = groupDao.findById(groupId);
        if (entity != null) {
            groupDao.delete(entity);
            List<IdentityEntity> systemGroupIdentityList = identityDAO.findByReferredId(groupId);
            for (IdentityEntity identityEntity : systemGroupIdentityList) {
                identityDAO.delete(identityEntity);
            }
        }
    }

    private void visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
        if (entity != null) {
            if (!visitedSet.contains(entity)) {
                visitedSet.add(entity);
                final Set<GroupEntity> children = entity.getChildGroups();
                if (CollectionUtils.isNotEmpty(children)) {
                    for (final GroupEntity child : children) {
                        visitGroups(child, visitedSet);
                    }
                }
            }
        }
    }

    @Transactional
    public void saveAttribute(final GroupAttributeEntity attribute) {
        if (StringUtils.isNotBlank(attribute.getId())) {
            groupAttrDao.update(attribute);
        } else {
            groupAttrDao.save(attribute);
        }
    }

    @Override
    @Transactional
    public void removeAttribute(String attributeId) {
        final GroupAttributeEntity entity = groupAttrDao.findById(attributeId);
        if (entity != null) {
            groupAttrDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public void addChildGroup(String groupId, String childGroupId) {
        if (groupId != null && childGroupId != null) {
            final GroupEntity group = groupDao.findById(groupId);
            final GroupEntity child = groupDao.findById(childGroupId);
            if (group != null && child != null) {
                if (!group.hasChildGroup(childGroupId)) {
                    group.addChildGroup(child);
                    groupDao.update(group);
                    groupDao.evictCache();
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeChildGroup(String groupId, String childGroupId) {
        if (groupId != null && childGroupId != null) {
            final GroupEntity childGroup = groupDao.findById(childGroupId);
            if (childGroup != null) {
                childGroup.removeParentGroup(groupId);
                groupDao.evictCache();
            }
        }
    }

    @Override
    @Transactional
    public void bulkAddChildGroup(String groupId, List<String> childGroupIds) throws BasicDataServiceException {
        if (groupId != null) {
            final GroupEntity group = groupDao.findById(groupId);
            final List<GroupEntity> childList = groupDao.findByIds(childGroupIds);
            if (group != null)
                for (GroupEntity childGroup : childList) {
                    validateGroup2GroupAddition(groupId, childGroup.getId());
                    if (!group.hasChildGroup(childGroup.getId())) {
                        group.addChildGroup(childGroup);
                    }
                }
        groupDao.update(group);
        groupDao.evictCache();
    }

}

    private Set<String> getDelegationFilter(String requesterId) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            boolean isFiltered = false;
            Map<String, UserAttribute> attrbutes = userDataService.getUserAttributesDto(requesterId);
            filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(attrbutes));
            List<String> rolesFromDelegation = DelegationFilterHelper.getRoleFilterFromString(attrbutes);
            if (rolesFromDelegation != null && rolesFromDelegation.size() > 0) {
                GroupSearchBean groupSearchBean = new GroupSearchBean();
                groupSearchBean.setRoleIdSet(new HashSet<>(rolesFromDelegation));
                List<String> groupIds = groupDao.getIDsByExample(groupSearchBean, 0, Integer.MAX_VALUE);
                filterData.addAll(groupIds);
                if (groupIds.size() == 0) {
                    isFiltered = true;
                }
            }
            Set<String> organizationFromDelegation = new HashSet<>();
            organizationFromDelegation.addAll(DelegationFilterHelper.getOrgIdFilterFromString(attrbutes));
            organizationFromDelegation.addAll(DelegationFilterHelper.getDeptFilterFromString(attrbutes));
            organizationFromDelegation.addAll(DelegationFilterHelper.getDivisionFilterFromString(attrbutes));
            if (organizationFromDelegation.size() > 0) {
                GroupSearchBean groupSearchBean = new GroupSearchBean();
                groupSearchBean.setOrganizationIdSet(organizationFromDelegation);
                List<String> groupIds = groupDao.getIDsByExample(groupSearchBean, 0, Integer.MAX_VALUE);
                filterData.addAll(groupIds);
                if (groupIds.size() == 0) {
                    isFiltered = true;
                }
            }
            // IF we can't find any groups or orgs by Role Delegation filter,
            // we add default skip ID to remove all others groups
            if (filterData.size() == 0 && isFiltered) {
                filterData.add("NOTEXIST");
            }
        }
        return filterData;
    }

    @Override
    @Transactional
    public void validateGroup2GroupAddition(String parentId, String memberId) throws BasicDataServiceException {
        final GroupEntity parent = groupDao.findById(parentId);
        final GroupEntity child = groupDao.findById(memberId);

        if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (causesCircularDependency(parent, child, new HashSet<GroupEntity>())) {
            throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
        }

        if (parent.hasChildGroup(child.getId())) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }

        if (StringUtils.equals(parentId, memberId)) {
            throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
        }
    }

    private boolean causesCircularDependency(final GroupEntity parent, final GroupEntity child, final Set<GroupEntity> visitedSet) {
        boolean retval = false;
        if (parent != null && child != null) {
            if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentGroups())) {
                    for (final GroupEntity entity : parent.getParentGroups()) {
                        retval = entity.getId().equals(child.getId());
                        if (retval) {
                            break;
                        }
                        causesCircularDependency(parent, entity, visitedSet);
                    }
                }
            }
        }
        return retval;
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public Group getGroupDTO(String groupId) {
        return getGroupDTOLocalize(groupId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public Group getGroupDTOLocalize(String groupId, LanguageEntity language) {
        return groupDozerConverter.convertToDTO(groupDao.findById(groupId), true);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<GroupEntity> findGroupsByAttributeValue(String attrName, String attrValue) {
        return findGroupsByAttributeValueLocalize(attrName, attrValue, getDefaultLanguage());
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<GroupEntity> findGroupsByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language) {
        return groupDao.findGroupsByAttributeValue(attrName, attrValue);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Group> findGroupsDtoByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language) {
        List<GroupEntity> groupEntities = groupDao.findGroupsByAttributeValue(attrName, attrValue);
        return groupDozerConverter.convertToDTOList(groupEntities, true);
    }

    private GroupDataService getProxyService() {
        GroupDataService service = (GroupDataService) ac.getBean("groupManager");
        return service;
    }

    @Override
    @Transactional
    public void removeRoleFromGroup(String roleId, String groupId) {
        if (roleId != null && groupId != null) {
            final RoleEntity role = roleDao.findById(roleId);
            final GroupEntity group = groupDao.findById(groupId);
            if (role != null && group != null) {
                role.removeGroup(group.getId());
                roleDao.save(role);
                roleDao.evictCache();
            }
        }
    }

    @Override
    @Transactional
    public void saveGroupRequest(final GroupRequestModel request) throws Exception {

        validateGroupRequest(request);

        final GroupEntity groupEntity = groupDozerConverter.convertToEntity(request.getTargetObject(), true);
        final PageTemplateAttributeToken token = pageTemplateService.getAttributesFromTemplate(request);

        if (token != null) {
            if (CollectionUtils.isNotEmpty(token.getSaveList())) {
                for (final GroupAttributeEntity entity : (List<GroupAttributeEntity>) token.getSaveList()) {
                    groupEntity.addAttribute(entity);
                }
            }
            if (CollectionUtils.isNotEmpty(token.getUpdateList())) {
                for (final GroupAttributeEntity entity : (List<GroupAttributeEntity>) token.getUpdateList()) {
                    groupEntity.addAttribute(entity);
                }
            }

            if (CollectionUtils.isNotEmpty(token.getDeleteList())) {
                for (final GroupAttributeEntity entity : (List<GroupAttributeEntity>) token.getDeleteList()) {
                    groupEntity.removeAttribute(entity.getId());
                }
            }
            if (CollectionUtils.isNotEmpty(token.getNonChangedList())) {
                for (final GroupAttributeEntity entity : (List<GroupAttributeEntity>) token.getNonChangedList()) {
                    groupEntity.addAttribute(entity);
                }
            }
        }
        this.saveGroup(groupEntity, request.getTargetObject().getOwner(), request.getRequesterId());
        request.getTargetObject().setId(groupEntity.getId());
    }

    public void validateGroupRequest(final GroupRequestModel request) throws Exception {
        pageTemplateService.validate(request);
    }
}
