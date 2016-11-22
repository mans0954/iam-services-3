package org.openiam.idm.srvc.grp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.response.SetStringResponse;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.elasticsearch.converter.GroupDocumentToEntityConverter;
import org.openiam.elasticsearch.converter.RoleDocumentToEntityConverter;
import org.openiam.elasticsearch.dao.GroupElasticSearchRepository;
import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityDAO;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.grp.dto.GroupRequestModel;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.exception.PageTemplateException;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.InternationalizationProvider;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.openiam.util.AuditLogHelper;
import org.openiam.util.SpringContextProvider;
import org.openiam.util.UserUtils;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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
    protected AuditLogHelper auditLogHelper;
    
    @Autowired
    private AccessRightDAO accessRightDAO;

    @Autowired
    private LanguageDozerConverter languageConverter;
    
    @Autowired
    private GroupElasticSearchRepository groupElasticSearchRepo;

    @Autowired
    private MetadataElementTemplateService pageTemplateService;
    
    @Autowired
    private ApproverAssociationDAO approverAssociationDao;
    
    @Autowired
	private InternationalizationProvider internationalizationProvider;
    
    @Autowired
    private GroupDocumentToEntityConverter groupDocConverter;
    
    private ApplicationContext ac;
    
    @Autowired
    private PolicyDAO policyDAO;

    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @Autowired
    @Qualifier("authorizationManagerAdminService")
    private AuthorizationManagerAdminService authorizationManagerAdminService;
    
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

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
        if(DelegationFilterHelper.isAllowed(id, getDelegationFilter(requesterId))){
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

        if(groupEntity != null){
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
        searchBean.setNameToken(new SearchParam(groupName, MatchType.EXACT));
        final List<GroupEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

    @Override
    @LocalizedServiceGet
    public GroupEntity getGroupByNameAndManagedSystem(final String groupName, final String managedSystemId, final String requesterId, final LanguageEntity language) {
        final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setNameToken(new SearchParam(groupName, MatchType.EXACT));
        searchBean.setManagedSysId(managedSystemId);
        searchBean.setLanguage(language);
        
        /* can only ever return 1 due to DB constraint */
        final List<GroupEntity> foundList = findBeans(searchBean, requesterId, 0, Integer.MAX_VALUE);
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
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final  String requesterId, int from, int size) {
        return getGroupEntities(searchBean, requesterId, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> findDtoBeans(final GroupSearchBean searchBean, final  String requesterId, int from, int size) {

        //GroupDataService bean = (GroupDataService)ac.getBean("groupManager");
        List<GroupEntity> groupEntities = this.getProxyService().findBeans(searchBean, requesterId, from, size);

        //List<GroupEntity> groupEntities = getGroupEntities(searchBean, requesterId, from, size);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    private List<GroupEntity> getGroupEntities(GroupSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(CollectionUtils.isEmpty(searchBean.getKeySet())) {
            searchBean.setKeySet(filter);
        } else if(!DelegationFilterHelper.isAllowed(searchBean.getKeySet(), filter)){
            return new ArrayList<GroupEntity>(0);
        }
        if(searchBean != null && searchBean.isUseElasticSearch()) {
        	List<GroupDoc> docs = null;
        	if(groupElasticSearchRepo.isValidSearchBean(searchBean)) {
        		docs = groupElasticSearchRepo.findBeans(searchBean, from, size);
        	} else {
        		docs = groupElasticSearchRepo.findAll(groupElasticSearchRepo.getPageable(searchBean, from, size)).getContent();
        	}
        	final List<GroupEntity> entities = groupDocConverter.convertToEntityList(docs);
        	internationalizationProvider.doDatabaseGet(entities);
        	return entities;
        } else {
        	return groupDao.getByExample(searchBean, from, size);
        }
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, final  String requesterId, int from, int size, final LanguageEntity language) {
        return getGroupEntities(searchBean, requesterId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, int from, int size){
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId);

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
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> findGroupsDtoForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, int from, int size){
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
        List<GroupEntity> finalizedGroups = this.getProxyService().findGroupsForOwner(searchBean, requesterId, ownerId, from, size);

        return groupDozerConverter.convertToDTOList(finalizedGroups, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForUser(String userId, String requesterId, int from, int size) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
        final List<GroupEntity> groupEntities = findBeans(sb, requesterId, from, size);
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
    public Set<String> getGroupIdList(){
        List<String> groupIds = groupDao.getAllIds();
        if(CollectionUtils.isNotEmpty(groupIds))
            return new HashSet<String>(groupIds);
        return Collections.EMPTY_SET;
    }

    @Override
    @Transactional(readOnly = true)
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if (CollectionUtils.isEmpty(searchBean.getKeySet())) {
            searchBean.setKeySet(filter);
        } else if(!DelegationFilterHelper.isAllowed(searchBean.getKeySet(), filter)){
            return 0;
        }
        if(searchBean != null && searchBean.isUseElasticSearch()) {
        	return groupElasticSearchRepo.count(searchBean);
        } else {
        	return groupDao.count(searchBean);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int countGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId){
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId);
        return finalizedGroups.size();
    }
/*
       HOW TO DO IT???

   @Override
    @Transactional(readOnly = true)
    public List<GroupOwner> getOwnersBeansForGroup(String groupId){
        List<GroupOwner> result = new ArrayList<>();

        if(StringUtils.isNotBlank(groupId)){
            GroupEntity groupEntity = groupDao.findById(groupId);
            result = getOwnersBeansForGroup(groupEntity);
        }
        return result;
    }
    private List<GroupOwner> getOwnersBeansForGroup(GroupEntity groupEntity){
        List<GroupOwner> result = new ArrayList<>();

        if(groupEntity!=null){
            ResourceEntity adminResource = groupEntity.getAdminResource();
            if(adminResource!=null){
                if(CollectionUtils.isNotEmpty(adminResource.getUsers())){
                    for (UserToResourceMembershipXrefEntity usr: adminResource.getUsers()){
                        GroupOwner owner = new GroupOwner();
                        owner.setType("user");
                        owner.setId(usr.getMemberEntity().getId());
                        result.add(owner);
                    }
                }
                if(CollectionUtils.isNotEmpty(adminResource.getGroups())){
                    for (GroupToResourceMembershipXrefEntity grp: adminResource.getGroups()){
                        GroupOwner owner = new GroupOwner();
                        owner.setType("group");
                        owner.setId(owner.getId());
                        owner.setName(grp.getMemberEntity().getName());
                        result.add(owner);
                    }
                }
            }
        }
        return result;
    }*/

    private List<GroupEntity> getGroupListForOwner(GroupSearchBean searchBean, String requesterId, String ownerId){
        List<GroupEntity> foundGroups = findBeans(searchBean, requesterId, -1, -1);
        List<GroupEntity> finalizedGroups = new ArrayList<>();

        Set<String> foundGroupsId = new HashSet<>();
        if(CollectionUtils.isNotEmpty(foundGroups)){
            for (GroupEntity grp: foundGroups){
                foundGroupsId.add(grp.getId());
            }
        }
        HashMap<String, SetStringResponse> ownersMap = authorizationManagerAdminService.getOwnerIdsForGroupSet(foundGroupsId, new Date());
        for (GroupEntity grp: foundGroups){
            SetStringResponse idsResp = ownersMap.get(grp.getId());
            if(idsResp!=null && CollectionUtils.isNotEmpty(idsResp.getSetString()) && idsResp.getSetString().contains(ownerId)){
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
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
    	sb.setLanguage(language);
        final List<GroupEntity> groupList = findBeans(sb, null, 0, Integer.MAX_VALUE);
        final Set<GroupEntity> visitedSet = new HashSet<GroupEntity>();
        if(CollectionUtils.isNotEmpty(groupList)) {
            for(final GroupEntity group : groupList) {
                visitGroups(group, visitedSet);
            }
        }
        return groupDozerConverter.convertToDTOList(new ArrayList<GroupEntity>(visitedSet), true);
    }

    @Transactional(readOnly = true)
	public boolean isUserInCompiledGroupList(String groupId, String userId) {
		if(groupId != null) {
			final List<Group> userGroupList =  getCompiledGroupsForUser(userId);
			if(CollectionUtils.isNotEmpty(userGroupList)) {
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

    public boolean isValid(final GroupEntity group) throws BasicDataServiceException{
        return entityValidator.isValid(group);
    }

    @Override
    public Response saveGroup(final Group group, final String requesterId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {

            final GroupEntity entity = groupDozerConverter.convertToEntity(group, true);
            validate(entity);
            this.saveGroup(entity, group.getOwner(), requesterId);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            log.error("Error save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
            response.addErrorToken(new EsbErrorToken(e.getMessage()));
        }
        return response;
    }

    @Override
    @Transactional
    public void saveGroup(final GroupEntity group, final GroupOwner groupOwner, final String requestorId) throws BasicDataServiceException{
        if(group != null && isValid(group)) {

            if(group.getManagedSystem() != null && StringUtils.isNotBlank(group.getManagedSystem().getId())) {
                final ManagedSysEntity mngSys = managedSysDAO.findById(group.getManagedSystem().getId());
                if(mngSys != null) {
                	group.setManagedSystem(managedSysDAO.findById(group.getManagedSystem().getId()));
                	if(mngSys.getResource() != null){
                		group.addResource(mngSys.getResource(), accessRightDAO.findAll(), null, null);
                	}
                }

            } else {
                group.setManagedSystem(null);
            }
            
            if(group.getPolicy() != null && StringUtils.isNotBlank(group.getPolicy().getId())) {
            	group.setPolicy(policyDAO.findById(group.getPolicy().getId()));
            } else {
            	group.setPolicy(null);
            }

            if(CollectionUtils.isNotEmpty(group.getOrganizations())) {
            	group.getOrganizations().forEach(xref -> {
            		xref.setMemberEntity(group);
            		xref.setEntity(organizationDAO.findById(xref.getEntity().getId()));
            		final Set<String> rightIds = (xref.getRights() != null) ? xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet()) : null;
            		final List<AccessRightEntity> accessRightList = accessRightDAO.findByIds(rightIds);
            		xref.setRights((accessRightList != null) ? new HashSet<AccessRightEntity>(accessRightList) : null);
            	});
            } else {
                group.setOrganizations(null);
            }

            if(group.getType() != null && StringUtils.isNotBlank(group.getType().getId())) {
                group.setType(typeDAO.findById(group.getType().getId()));
            } else {
                group.setType(null);
            }

            if(group.getClassification() != null && StringUtils.isNotBlank(group.getClassification().getId())) {
                group.setClassification(typeDAO.findById(group.getClassification().getId()));
            } else {
                group.setClassification(null);
            }
            if(group.getAdGroupType() != null && StringUtils.isNotBlank(group.getAdGroupType().getId())) {
                group.setAdGroupType(typeDAO.findById(group.getAdGroupType().getId()));
            } else {
                group.setAdGroupType(null);
            }
            if(group.getAdGroupScope() != null && StringUtils.isNotBlank(group.getAdGroupScope().getId())) {
                group.setAdGroupScope(typeDAO.findById(group.getAdGroupScope().getId()));
            } else {
                group.setAdGroupScope(null);
            }
            if(group.getRisk() != null && StringUtils.isNotBlank(group.getRisk().getId())) {
                group.setRisk(typeDAO.findById(group.getRisk().getId()));
            } else {
                group.setRisk(null);
            }

            if(StringUtils.isNotBlank(group.getId())) {
                final GroupEntity dbGroup = groupDao.findById(group.getId());
                if(dbGroup != null) {
                	
                    group.setApproverAssociations(dbGroup.getApproverAssociations());

                    mergeAttribute(group, dbGroup, requestorId);
                    group.setChildGroups(dbGroup.getChildGroups());
                    group.setParentGroups(dbGroup.getParentGroups());
                    group.setResources(dbGroup.getResources());
                    group.setRoles(dbGroup.getRoles());
                    group.setUsers(dbGroup.getUsers());
                    group.setLastUpdatedBy(requestorId);
                    group.setLastUpdate(Calendar.getInstance().getTime());
                    
                    /* hibernate fails you just null out the PersistentSet.  As of Hibernate 4 */
                    if(CollectionUtils.isEmpty(group.getOrganizations())) {
                    	dbGroup.getOrganizations().clear();
                    } else {
                    	/* basically, we need to remove entries from the DB perstent set tha we don't need */
                    	final Set<String> incomingOrganizationIds = (group.getOrganizations() != null) ? 
                    			group.getOrganizations().stream().map(e -> e.getEntity().getId()).collect(Collectors.toSet()) : null;
                    	group.getOrganizations().forEach(xref -> {
                    		dbGroup.getOrganizations().removeIf(e -> {
                    			return !incomingOrganizationIds.contains(e.getEntity().getId());
                    		});
                    		dbGroup.addOrganization(xref.getEntity(), xref.getRights(), null, null);
                    	});
                    }
                    
                    /* now set the persistent set  on the transient object */
                    group.setOrganizations(dbGroup.getOrganizations());
                    group.getOrganizations().forEach(xref -> {
                    	xref.setMemberEntity(group);
                    	xref.setEntity(organizationDAO.findById(xref.getEntity().getId()));
                    });
                } else {
                    return;
                }
                groupDao.merge(group);

            } else {
            	if(groupOwner != null) {
            		if("user".equals(groupOwner.getType())){
            			group.addUser(userDAO.findById(groupOwner.getId()), accessRightDAO.findById(adminRightId), null, null);
            		} else if("group".equals(groupOwner.getType())){
            			group.addChildGroup(groupDao.findById(groupOwner.getId()), accessRightDAO.findById(adminRightId), null, null);
            		} else {
            			group.addUser(userDAO.findById(requestorId), accessRightDAO.findById(adminRightId), null, null);
            		}
            	}
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
        if(group!=null && group.getType()!=null && StringUtils.isNotBlank(group.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(group.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        groupAttrDao.save(AttributeUtil.buildGroupAttribute(group, element));
                    }
                }
            }
        }
    }
/*
    private void mergeGroupOwner(GroupEntity group, GroupOwner groupOwner, String requestorId) {
        // get data from DB
        List<GroupOwner> owners = getOwnersBeansForGroup(group);
        if(owners==null || !owners.contains(groupOwner)) {
            // get admin resource
            ResourceEntity adminResource = group.getAdminResource();
            // clean users and groups
            adminResource.getUsers().clear();
            adminResource.getGroups().clear();
            // add new Owner;
            addOwner(adminResource, groupOwner, requestorId);
        }
    }*/

    private ApproverAssociationEntity createDefaultApproverAssociations(final GroupEntity entity, final String requestorId) {
    	if(requestorId != null) {
			final ApproverAssociationEntity association = new ApproverAssociationEntity();
			association.setAssociationEntityId(entity.getId());
			association.setAssociationType(AssociationType.GROUP);
			association.setApproverLevel(Integer.valueOf(0));
			association.setApproverEntityId(requestorId);
			association.setApproverEntityType(AssociationType.USER);
			return association;
    	} else {
    		return null;
    	}
	}

/*    private void addOwner(ResourceEntity adminResource, GroupOwner groupOwner, final String requestorId){
        if(groupOwner!=null && StringUtils.isNotBlank(groupOwner.getId())){
            if("user".equals(groupOwner.getType())){
                adminResource.addUser(userDAO.findById(groupOwner.getId()));
            } else if("group".equals(groupOwner.getType())){
                adminResource.addGroup(groupDao.findById(groupOwner.getId()));
            } else {
                adminResource.addUser(userDAO.findById(requestorId));
            }
        } else {
            adminResource.addUser(userDAO.findById(requestorId));
        }
    }*/

/*
    NEW TEMPLATE HOW???
    private void mergeParent(final GroupEntity bean, final GroupEntity dbObject, final String requesterId) {
        Set<GroupToGroupMembershipXrefEntity> beanParents = (bean.getParentGroups() != null) ? bean.getParentGroups() : new HashSet<GroupToGroupMembershipXrefEntity>();
        Set<GroupToGroupMembershipXrefEntity> dbParents = (dbObject.getParentGroups() != null) ? dbObject.getParentGroups() : new HashSet<GroupToGroupMembershipXrefEntity>();

        *//* update *//*
        Iterator<GroupToGroupMembershipXrefEntity> dbIterator = dbParents.iterator();
        while(dbIterator.hasNext()) {
            final GroupToGroupMembershipXrefEntity dbParent = dbIterator.next();

            boolean contains = false;
            for (final GroupToGroupMembershipXrefEntity beanParent : beanParents) {
                if (StringUtils.equals(dbParent.getEntity().getId(), beanParent.getEntity().getId())) {
                    contains = true;
                    break;
                }
            }
            *//* remove *//*
            if(!contains) {
                auditLogRemoveParent(bean, dbParent, requesterId);
                dbIterator.remove();
            }
        }

        *//* add *//*
        final Set<String> toAddIds = new HashSet<>();
        for (final GroupToGroupMembershipXrefEntity beanParent : beanParents) {
            boolean contains = false;
            dbIterator = dbParents.iterator();
            while(dbIterator.hasNext()) {
                final GroupToGroupMembershipXrefEntity dbParent = dbIterator.next();
                if (StringUtils.equals(dbParent.getId(), beanParent.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                toAddIds.add(beanParent.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(toAddIds)){
            dbParents.addAll(groupDao.findByIds(toAddIds));
        }
        bean.setParentGroups(dbParents);
    }*/
	
	private void mergeAttribute(final GroupEntity bean, final GroupEntity dbObject, final String requesterId) {
		Set<GroupAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<GroupAttributeEntity>();
        Set<GroupAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? new HashSet<GroupAttributeEntity>(dbObject.getAttributes()) : new HashSet<GroupAttributeEntity>();

        /* update */
        Iterator<GroupAttributeEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final GroupAttributeEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final GroupAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setMetadataElementId(beanProp.getMetadataElementId());
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
                auditLogRemoveAttribute(bean, dbProp, requesterId);
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<GroupAttributeEntity> toAdd = new HashSet<>();
        for (final GroupAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final GroupAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setGroup(bean);
                beanProp.setMetadataElementId(beanProp.getMetadataElementId());
                auditLogAddAttribute(bean, beanProp, requesterId);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setAttributes(dbProps);
	}

    private void auditLogRemoveParent(final GroupEntity group, final GroupToGroupMembershipXrefEntity parent, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setAction(AuditAction.REMOVE_PARENT_GROUP.value());
        auditLog.put(parent.getEntity().getName(), parent.getId());
        auditLogHelper.enqueue(auditLog);
    }


    private void auditLogRemoveAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
        auditLog.put(groupAttr.getName(), groupAttr.getValue());
        auditLogHelper.enqueue(auditLog);
    }

    private void auditLogAddAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
        auditLog.put(groupAttr.getName(), groupAttr.getValue());
        auditLogHelper.enqueue(auditLog);
    }

	@Override
	@Transactional
	public void deleteGroup(String groupId) {
		final GroupEntity entity = groupDao.findById(groupId);
		if(entity != null) {
			groupDao.delete(entity);
            List<IdentityEntity> systemGroupIdentityList = identityDAO.findByReferredId(groupId);
            for(IdentityEntity identityEntity : systemGroupIdentityList) {
                identityDAO.delete(identityEntity);
            }
		}
	}
    @Override
    public Response deleteGroup(final String groupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            validateDeleteInternal(groupId);

            this.deleteGroup(groupId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't delete", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }

        return response;
    }
    private void validateDeleteInternal(final String groupId) throws BasicDataServiceException {
        if (StringUtils.isBlank(groupId)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId is null or empty");
        }
    }

	private void visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		if(entity != null) {
			if(!visitedSet.contains(entity)) {
				visitedSet.add(entity);
				final Set<GroupToGroupMembershipXrefEntity> children = entity.getChildGroups();
				if(CollectionUtils.isNotEmpty(children)) {
					children.stream().map(e -> e.getMemberEntity()).forEach(child -> {
						visitGroups(child, visitedSet);
					});
				}
			}
		}
	}


	public void saveAttribute(final GroupAttributeEntity attribute) {
		if(StringUtils.isNotBlank(attribute.getId())) {
			groupAttrDao.update(attribute);
		} else {
			groupAttrDao.save(attribute);
		}
	}

	@Override
    @Transactional
	public void removeAttribute(String attributeId) {
		final GroupAttributeEntity entity = groupAttrDao.findById(attributeId);
		if(entity != null) {
			groupAttrDao.delete(entity);
		}
	}

	@Override
	public void addChildGroup(String groupId, String childGroupId, final Set<String> rights, final Date startDate, final Date endDate) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			final GroupEntity child = groupDao.findById(childGroupId);
			if(group != null && child != null) {
				group.addChildGroup(child, accessRightDAO.findByIds(rights), startDate, endDate);
			}
		}
	}

	@Override
    @Transactional
	public void removeChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity child = groupDao.findById(childGroupId);
			final GroupEntity parent = groupDao.findById(groupId);
			if(parent != null && child != null) {
				parent.removeChildGroup(child);
			}
		}
	}

    private Set<String> getDelegationFilter(String requesterId){
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            boolean isFiltered = false;
            Map<String, UserAttribute> attrbutes =  userDataService.getUserAttributesDto(requesterId);
            filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(attrbutes));
            List<String> rolesFromDelegation = DelegationFilterHelper.getRoleFilterFromString(attrbutes);
            System.out.println("================================== GroupDataService.getDelegationFilter==== rolesFromDelegation= "+rolesFromDelegation+", requesterId="+requesterId);
            if(rolesFromDelegation != null && rolesFromDelegation.size() > 0){
                GroupSearchBean groupSearchBean = new GroupSearchBean();
                groupSearchBean.setRoleIdSet(new HashSet<String>(rolesFromDelegation));
                List<String> groupIds = groupDao.getIDsByExample(groupSearchBean, 0, Integer.MAX_VALUE);
                System.out.println("================================== GroupDataService.getDelegationFilter==== groupIds= "+groupIds+", requesterId="+requesterId);
                // TODO check: CONJUNCTION
                // IF we can't find any groups by Role Delegation filter we add default skip ID to remove all others groups
                if(groupIds == null || groupIds.size() == 0) {
                    filterData.add("NOTEXIST");
                } else {
                    filterData.addAll(groupIds);
                }
            }
            Set<String> organizationFromDelegation = new HashSet<>();
            organizationFromDelegation.addAll(DelegationFilterHelper.getOrgIdFilterFromString(attrbutes));
            organizationFromDelegation.addAll(DelegationFilterHelper.getDeptFilterFromString(attrbutes));
            organizationFromDelegation.addAll(DelegationFilterHelper.getDivisionFilterFromString(attrbutes));
            if(organizationFromDelegation.size() > 0){
                GroupSearchBean groupSearchBean = new GroupSearchBean();
                groupSearchBean.setOrganizationIdSet(organizationFromDelegation);
                List<String> groupIds = groupDao.getIDsByExample(groupSearchBean, 0, Integer.MAX_VALUE);
                filterData.addAll(groupIds);
                if(groupIds.size() == 0) {
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
	public void validateGroup2GroupAddition(String parentId, String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException {
		final GroupEntity parent = groupDao.findById(parentId);
		final GroupEntity child = groupDao.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<GroupEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(startDate != null && endDate != null && startDate.after(endDate)) {
        	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
        }
		
		/*
		if(parent.hasChildGroup(child.getId())) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		*/
		
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
	}
	
	private boolean causesCircularDependency(final GroupEntity parent, final GroupEntity child, final Set<GroupEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentGroups())) {
                    for (final GroupToGroupMembershipXrefEntity xref : parent.getParentGroups()) {
                    	final GroupEntity entity = xref.getEntity();
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
    	final GroupSearchBean searchBean = new GroupSearchBean();
    	searchBean.addAttribute(attrName, attrValue);
        return groupDao.getByExample(searchBean);
    }

	@Override
	@Transactional(readOnly = true)
	public boolean hasAttachedEntities(String groupId) {
		final GroupEntity group = groupDao.findById(groupId);
		if(group != null) {
			return CollectionUtils.isNotEmpty(group.getChildGroups()) ||
				   CollectionUtils.isNotEmpty(group.getRoles()) ||
				   CollectionUtils.isNotEmpty(group.getResources());
		} else {
			return false;
		}
	}

/*    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Group> findGroupsDtoByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity lang) {
        List<GroupEntity> groupEntities = groupDao.findGroupsByAttributeValue(attrName, attrValue);
        return groupDozerConverter.convertToDTOList(groupEntities, true);
    }*/

    private GroupDataService getProxyService() {
        GroupDataService service = (GroupDataService) SpringContextProvider.getBean("groupManager");
        return service;
    }

    @Override
    @Transactional
    public void removeRoleFromGroup(String roleId, String groupId) {
        if (roleId != null && groupId != null) {
            final RoleEntity role = roleDao.findById(roleId);
            final GroupEntity group = groupDao.findById(groupId);
            if (role != null && group != null) {
                role.removeGroup(group);
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

        if(token != null) {
            if(CollectionUtils.isNotEmpty(token.getSaveList())) {
                for(final GroupAttributeEntity entity : (List<GroupAttributeEntity>)token.getSaveList()) {
                    groupEntity.addAttribute(entity);
                }
            }
            if(CollectionUtils.isNotEmpty(token.getUpdateList())) {
                for(final GroupAttributeEntity entity : (List<GroupAttributeEntity>)token.getUpdateList()) {
                    groupEntity.addAttribute(entity);
                }
            }

            if(CollectionUtils.isNotEmpty(token.getDeleteList())) {
                for(final GroupAttributeEntity entity : (List<GroupAttributeEntity>)token.getDeleteList()) {
                    groupEntity.removeAttribute(entity.getId());
                }
            }
            if(CollectionUtils.isNotEmpty(token.getNonChangedList())) {
                for(final GroupAttributeEntity entity : (List<GroupAttributeEntity>)token.getNonChangedList()) {
                    groupEntity.addAttribute(entity);
                }
            }
        }
        this.saveGroup(groupEntity, request.getTargetObject().getOwner(), request.getRequesterId());
        request.getTargetObject().setId(groupEntity.getId()) ;
    }

    public SaveTemplateProfileResponse saveGroupRequestWeb(final GroupRequestModel request){
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
        try {
            if(request == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
            }

            getProxyService().saveGroupRequest(request);
            response.setResponseValue(request.getTargetObject().getId());
        } catch (PageTemplateException e){
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch(BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        }catch(Throwable e) {
            log.error("Exception", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    public void validateGroupRequest(final GroupRequestModel request) throws Exception {
        pageTemplateService.validate(request);
    }

    @Override
    @Transactional
    public Response addUserToGroup(final String groupId, final String userId, final String requesterId, final Set<String> rightIds,
                                   final Date startDate, final Date endDate){
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_USER_TO_GROUP.value());
        UserEntity user = userDataService.getUser(userId);
        LoginEntity userPrimaryIdentity =  UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        auditLog.setTargetUser(userId,userPrimaryIdentity.getLogin());
        GroupEntity groupEntity = this.getGroup(groupId);
        auditLog.setTargetGroup(groupId, groupEntity.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add user %s to group: %s", userId, groupId));
        try {
            if (groupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }

            if(startDate != null && endDate != null && startDate.after(endDate)) {
                throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }

            userDataService.addUserToGroup(userId, groupId, rightIds, startDate, endDate);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("Error while adding user to group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogHelper.enqueue(auditLog);
        }
        return response;
    }
    @Override
    public Response removeUserFromGroup(final String groupId, final String userId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        final IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        try {
            if (groupId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Group Id is null or empty");
            }

            final GroupEntity groupEntity = this.getGroupLocalize(groupId, null);
            if(groupEntity != null) {
                auditLog.setRequestorUserId(requesterId);
                auditLog.setAction(AuditAction.REMOVE_USER_FROM_GROUP.value());
                auditLog.setTargetUser(userId, null);
                auditLog.setTargetGroup(groupId, groupEntity.getName());
                auditLog.setAuditDescription(String.format("Remove user %s from group: %s", userId, groupId));

                userDataService.removeUserFromGroup(userId, groupId);
            }
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("Error while remove user from group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogHelper.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response addChildGroup(final String groupId, final String childGroupId, final String requesterId,
                                  final Set<String> rights, final Date startDate, final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.ADD_CHILD_GROUP.value());
        //GroupEntity groupEntity = groupManager.getGroup(groupId);
        //auditLog.setTargetGroup(groupId, groupEntity.getName());
        //GroupEntity groupEntityChild = groupManager.getGroup(childGroupId);
        //auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Add child group: %s to group: %s", childGroupId, groupId));

        try {
            if(startDate != null && endDate != null && startDate.after(endDate)) {
                throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }

            GroupEntity groupEntity = this.getGroupLocalize(groupId, null);
            GroupEntity groupEntityChild = this.getGroupLocalize(childGroupId, null);
            if(groupEntity == null || groupEntityChild == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            auditLog.setTargetGroup(groupId, groupEntity.getName());
            auditLog.setTargetGroup(childGroupId, groupEntityChild.getName());

            if (groupId == null || childGroupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
            }

            if (groupId.equals(childGroupId)) {
                throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD,
                        "Cannot add group itself as child");
            }

            getProxyService().validateGroup2GroupAddition(groupId, childGroupId, rights, startDate, endDate);
            getProxyService().addChildGroup(groupId, childGroupId, rights, startDate, endDate);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            log.error("can't add child group", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogHelper.enqueue(auditLog);
        }
        return response;
    }

    @Override
    public Response removeChildGroup(final String groupId, final String childGroupId, final String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setAction(AuditAction.REMOVE_CHILD_GROUP.value());
        Group groupDto = this.getGroupDTO(groupId);
        auditLog.setTargetGroup(groupId, groupDto.getName());
        Group groupChild = this.getGroupDTO(childGroupId);
        auditLog.setTargetGroup(childGroupId, groupChild.getName());
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAuditDescription(String.format("Remove child group: %s from group: %s", childGroupId, groupId));

        try {
            if (groupId == null || childGroupId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or child groupId is null");
            }

            getProxyService().removeChildGroup(groupId, childGroupId);
            auditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            auditLog.fail();
            auditLog.setFailureReason(e.getCode());
            auditLog.setException(e);
        } catch (Throwable e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            auditLog.fail();
            auditLog.setException(e);
        } finally {
            auditLogHelper.enqueue(auditLog);
        }
        return response;
    }

    private void validate(final GroupEntity entity) throws BasicDataServiceException {
        if (entity == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }

        if (StringUtils.isBlank(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME);
        }

        final GroupEntity nameEntity = this.getGroupByNameAndManagedSystem(entity.getName(), entity.getManagedSystem().getId(), null, null);
        if(nameEntity != null) {
            if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
                throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Role Name + Managed Sys combination taken");
            }
        }

        this.isValid(entity);
    }
}
