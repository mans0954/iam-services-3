package org.openiam.idm.srvc.org.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.cache.CacheKeyEvictions;
import org.openiam.cache.OrgAttributeToOrganizationKeyGenerator;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.LocationDozerConverter;
import org.openiam.dozer.converter.OrganizationAttributeDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.elasticsearch.dao.OrganizationElasticSearchRepository;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.loc.service.LocationDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.org.domain.OrgToOrgMembershipXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserToOrganizationMembershipXrefEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl extends AbstractBaseService implements OrganizationService, InitializingBean, Sweepable, ApplicationContextAware {
    private static final Log log = LogFactory.getLog(OrganizationServiceImpl.class);
	@Autowired
	private OrganizationTypeDAO orgTypeDAO;

    @Autowired
    private LocationDozerConverter locationDozerConverter;
    @Autowired
    private LocationDAO locationDao;

    @Autowired
    private MetadataElementDAO metadataDAO;

    @Autowired
    private ApproverAssociationDAO approverAssociationDAO;

    @Autowired
    private OrganizationDAO orgDao;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OrganizationAttributeDAO orgAttrDao;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private OrganizationTypeService organizationTypeService;
    
    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;

    @Autowired
    private OrganizationAttributeDozerConverter organizationAttributeDozerConverter;
    
    @Autowired
    private MetadataElementDAO metadataElementDAO;
	
	@Autowired
    private ResourceTypeDAO resourceTypeDao;
	
    @Autowired
    private MetadataTypeDAO typeDAO;
    
    @Autowired
    private GroupDAO groupDAO;
    
    @Autowired
    private RoleDAO roleDAO;
    
    @Autowired
    private OrganizationElasticSearchRepository organizationElasticSearchRepository;

    private Map<String, Set<String>> organizationTree;
    private Map<String, String> organizationInvertedTree;


    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Value("${org.openiam.idm.preProcessorOrganization.groovy.script}")
    protected String preProcessorOrganization;

    @Value("${org.openiam.idm.postProcessorOrganization.groovy.script}")
    protected String postProcessorOrganization;
    
    @Autowired
    private AccessRightDAO accessRightDAO;
    
    @Autowired
    private ResourceDAO resourceDAO;
    
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

    @Autowired
    private LanguageDozerConverter languageConverter;


    private ApplicationContext ac;


    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganizationLocalized(String orgId, final LanguageEntity langauge) {
        return getOrganizationLocalized(orgId, null, langauge);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public OrganizationEntity getOrganizationLocalized(String orgId, String requesterId, final LanguageEntity langauge) {
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId, false))) {
            return orgDao.findById(orgId);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public Organization getOrganizationLocalizedDto(String orgId, String requesterId, final LanguageEntity language) {
        //OrganizationService bean = (OrganizationService)ac.getBean("organizationService");

        OrganizationEntity organizationEntity = this.getProxyService().getOrganizationLocalized(orgId, requesterId, language);

        if (organizationEntity != null) {
            return organizationDozerConverter.convertToDTO(organizationEntity, true);
        }
        return null;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganizationByName(final String name, String requesterId, final LanguageEntity language) {
        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setNameToken(new SearchParam(name, MatchType.EXACT));
        searchBean.setLanguage(language);
        final List<OrganizationEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

/*    @Override
    //@LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Location> getLocationListByPageForUser(String userId, Integer from, Integer size) {

        Set<String> orgsId = new HashSet<String>();
        List<OrganizationEntity> orgList = this.getOrganizationsForUser(userId, null, from, size, languageConverter.convertToEntity(getDefaultLanguageDto(), false));
        for (OrganizationEntity org : orgList) {
            orgsId.add(org.getId());
        }

        if (orgsId == null) {
            return null;
        }
        List<LocationEntity> listOrgEntity = this.getLocationListByOrganizationId(orgsId, from, size);
        if (listOrgEntity == null) {
            return null;
        }

        List<Location> result = new ArrayList<Location>();
        for (LocationEntity org : listOrgEntity) {
            result.add(locationDozerConverter.convertToDTO(org, false));
        }

        return result;
    }

    private Language getDefaultLanguageDto() {
        Language lang = new Language();
        lang.setId("1");
        return lang;
    }*/

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getUserAffiliationsByType(String userId, String typeId, String requesterId, final int from, final int size, final LanguageEntity language) {
        List<OrganizationEntity> organizationEntityList = orgDao.getUserAffiliationsByType(userId, typeId, getDelegationFilter(requesterId), from, size);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }


    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsDtoForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity language) {
        //List<OrganizationEntity> organizationEntityList = orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId), from, size);
        List<OrganizationEntity> organizationEntityList = this.getProxyService().getOrganizationsForUser(userId, requesterId, from, size, language);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }

    @Deprecated
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size) {
        return this.getOrganizationsForUser(userId, requesterId, from, size, getDefaultLanguage());
    }

/*    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Organization> findOrganizationsDtoByAttributeValue(final String attrName, String attrValue, final LanguageEntity lang) {
        List<OrganizationEntity> organizationEntityList = orgDao.findOrganizationsByAttributeValue(attrName, attrValue);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, true);
    }*/

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity language) {
        return orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId, false), from, size);
    }

    private Set<String> getDelegationFilter(String requesterId, boolean isUncoverParents) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, isUncoverParents);

        }
        return filterData;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    /*AM-851 */
    //@Cacheable(value = "organizationEntities", key = "{ #searchBean,#requesterId,#from,#size,#lang}", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId, false);
        if(searchBean != null) {
        	if (StringUtils.isBlank(searchBean.getKey())) {
        		searchBean.setKeys(filter);
        	} else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
        		return new ArrayList<OrganizationEntity>(0);
        	}
        }
        if(searchBean != null && searchBean.isUseElasticSearch()) {
        	if(organizationElasticSearchRepository.isValidSearchBean(searchBean)) {
        		return organizationElasticSearchRepository.findBeans(searchBean, from, size);
        	} else {
        		return organizationElasticSearchRepository.findAll(organizationElasticSearchRepository.getPageable(searchBean, from, size)).getContent();
        	}
        } else {
        	return orgDao.getByExample(searchBean, from, size);
        }
    }

    /**
     * We cache the results of this method, as it has a heavy memory footprint when called many (thousands of) times
     * We cache *only* if the findInCache parameter is true
     * The proper @EvictCache annotations are present on methods which perform CRUD on organizations.
     * 
     * We have no 'key' parameter set for @Cacheable, as the default implementation will use a composite key of
     * all parameter, which is what we want in this case.  
     * See <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html">here</a>
     */
    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    @Cacheable(value = "organizations", key = "{ #searchBean,#requesterId,#from,#size,#language}", condition="{#searchBean != null and #searchBean.findInCache}")
    public List<Organization> findBeansDto(final OrganizationSearchBean searchBean, String requesterId, int from, int size, final LanguageEntity language) {
        /*final boolean isUncoverParents = Boolean.TRUE.equals(searchBean.getUncoverParents());
        Set<String> filter = getDelegationFilter(requesterId, isUncoverParents);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<Organization>(0);
        }*/

        // Temporary solution
        final StopWatch sw = new StopWatch();
        sw.start();
        //List<OrganizationEntity> organizationEntityList = orgDao.getByExample(searchBean, from, size);
        List<OrganizationEntity> organizationEntityList = this.getProxyService().findBeans(searchBean, requesterId, from, size);
        if (CollectionUtils.isNotEmpty(organizationEntityList) && searchBean.isDeepCopy() && searchBean.isForCurrentUsersOnly() && CollectionUtils.isNotEmpty(searchBean.getUserIdSet())) {
        	UserToOrganizationMembershipXrefEntity organizationUserEntity = null;
            Iterator<UserToOrganizationMembershipXrefEntity> organizationUserEntityIterator = null;
            for (OrganizationEntity organizationEntity : organizationEntityList) {
                organizationUserEntityIterator = organizationEntity.getUsers().iterator();
                while (organizationUserEntityIterator.hasNext()) {
                    organizationUserEntity = organizationUserEntityIterator.next();
                    if (!searchBean.getUserIdSet().contains(organizationUserEntity.getMemberEntity().getId())) {
                        organizationUserEntityIterator.remove();
                    }
                }
            }
        }
        sw.stop();
        if(log.isDebugEnabled()) {
        	log.debug(String.format("FINISH TIME = %s", sw.getTime()));
        }
        return organizationDozerConverter.convertToDTOList(organizationEntityList, searchBean.isDeepCopy());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getParentOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity language) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getParentOrganizationsDto(String orgId, String requesterId, int from, int size, final LanguageEntity language) {
        //List<OrganizationEntity> organizationEntityList = orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId), from, size);
        List<OrganizationEntity> organizationEntityList = this.getProxyService().getParentOrganizations(orgId, requesterId, from, size, language);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }


    @Override
    @Transactional(readOnly = true)
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        final boolean isUncoverParents = Boolean.TRUE.equals(searchBean.getUncoverParents());
        Set<String> filter = getDelegationFilter(requesterId, isUncoverParents);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return 0;
        }

        return orgDao.count(searchBean);
    }

/*    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfParentOrganizations(orgId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfChildOrganizations(orgId, getDelegationFilter(requesterId));
    }*/

    @Override
    @Transactional
    public void addUserToOrg(final String orgId, final String userId, final Set<String> rightIds, final Date startDate, final Date endDate) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        if(organization != null && user != null) {
        	user.addAffiliation(organization, accessRightDAO.findByIds(rightIds), startDate, endDate);
        }
    }

    @Override
    @Transactional
    public void removeUserFromOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        if(user != null && organization != null) {
        	user.removeAffiliation(organization);
        }
    }

    @Override
    @Transactional
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("organizations"),
            @CacheKeyEvict("organizationEntities")
        }
    )
    public Organization save(final Organization organization, final String requestorId) throws BasicDataServiceException {
        return save(organization, requestorId, false);
    }
    
    private void setOrganizationType(final OrganizationEntity newEntity, final OrganizationEntity curEntity) {
    	if (newEntity.getOrganizationType() == null || StringUtils.isBlank(newEntity.getOrganizationType().getId())) {
            curEntity.setOrganizationType(null);
        } else if (curEntity.getOrganizationType() == null || !StringUtils.equals(curEntity.getOrganizationType().getId(), newEntity.getOrganizationType().getId())) {
            curEntity.setOrganizationType(orgTypeDAO.findById(newEntity.getOrganizationType().getId()));
        }

        if (newEntity.getType() == null || StringUtils.isBlank(newEntity.getType().getId())) {
            curEntity.setType(null);
        } else if (curEntity.getType() == null || !StringUtils.equals(curEntity.getType().getId(), newEntity.getType().getId())) {
            curEntity.setType(typeDAO.findById(newEntity.getType().getId()));
        }
    }

    @Override
    @Transactional
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("organizations"),
            @CacheKeyEvict("organizationEntities")
        }
    )
    public Organization save(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) throws BasicDataServiceException {

        // Audit Log -----------------------------------------------------------------------------------
        final IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setRequestorUserId(requestorId);
        if (StringUtils.isNotBlank(organization.getId())) {
            idmAuditLog.setAction(AuditAction.EDIT_ORG.value());
            idmAuditLog.setTargetOrg(organization.getId(), organization.getName());
        } else {
            idmAuditLog.setAction(AuditAction.ADD_ORG.value());
        }

        try {
            Map<String, Object> bindingMap = new HashMap<String, Object>();
            if (!skipPrePostProcessors) {
                OrganizationServicePrePostProcessor preProcessor = getPreProcessScript();
                if (preProcessor != null &&  preProcessor.save(organization, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                    idmAuditLog.fail();
                    idmAuditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                    throw new BasicDataServiceException(ResponseCode.FAIL_PREPROCESSOR);
                }
            }

            OrganizationEntity newEntity = organizationDozerConverter.convertToEntity(organization, true);
            validateEntity(newEntity);
            OrganizationEntity curEntity;
            if (StringUtils.isBlank(organization.getId())) {
                curEntity = newEntity;
                curEntity.addUser(userDAO.findById(requestorId), accessRightDAO.findById(adminRightId), null, null);
                curEntity.setCreateDate(Calendar.getInstance().getTime());
                curEntity.setCreatedBy(requestorId);
                curEntity.addApproverAssociation(createDefaultApproverAssociations(curEntity, requestorId));
                addRequiredAttributes(curEntity);
                if (StringUtils.isNotBlank(newEntity.getOrganizationType().getId())) {
                    curEntity.setOrganizationType(orgTypeDAO.findById(newEntity.getOrganizationType().getId()));
                }
                if (StringUtils.isNotBlank(newEntity.getType().getId())) {
                    curEntity.setType(typeDAO.findById(newEntity.getType().getId()));
                }
                setOrganizationType(newEntity, curEntity);
                orgDao.save(curEntity);
            } else {
                curEntity = orgDao.findById(organization.getId());
                mergeOrgProperties(curEntity, newEntity);
                mergeAttributes(curEntity, newEntity);
                /*
                newEntity.setResources(dbEntity.getResources());
                newEntity.setUsers(dbEntity.getUsers());
                newEntity.setGroups(dbEntity.getGroups());
                newEntity.setRoles(dbEntity.getRoles());
                newEntity.setApproverAssociations(dbEntity.getApproverAssociations());
                newEntity.setChildOrganizations(childOrganizations);
                */
                /*
                if(CollectionUtils.isNotEmpty(curEntity.getParentOrganizations())) {
                	curEntity.getParentOrganizations().forEach(e -> {
                		e.setEntity(curEntity);
                	});
                }
                if(CollectionUtils.isNotEmpty(curEntity.getChildOrganizations())) {
                	
                }
                if(CollectionUtils.isNotEmpty(curEntity.getRoles())) {
                	
                }
                if(CollectionUtils.isNotEmpty(curEntity.getGroups())) {
                	
                }
                if(CollectionUtils.isNotEmpty(curEntity.getResources())) {
                	
                }
                */
                //newEntity.setParentOrganizations(curEntity.getParentOrganizations());
                //newEntity.setChildOrganizations(curEntity.getChildOrganizations());
                //newEntity.setUsers(curEntity.getUsers());
                //newEntity.setGroups(curEntity.getGroups());
                //newEntity.setResources(curEntity.getResources());
                //mergeParents(curEntity, newEntity);
                //mergeChildren(curEntity, newEntity);
                //mergeUsers(curEntity, newEntity);
                //mergeGroups(curEntity, newEntity);
                mergeLocations(curEntity, newEntity);
                mergeApproverAssociations(curEntity, newEntity);
                curEntity.setLstUpdate(Calendar.getInstance().getTime());
                curEntity.setLstUpdatedBy(requestorId);
                setOrganizationType(newEntity, curEntity);
                orgDao.merge(curEntity);
            }

            if (newEntity.getOrganizationType() == null || StringUtils.isBlank(newEntity.getOrganizationType().getId())) {
                curEntity.setOrganizationType(null);
            } else if (curEntity.getOrganizationType() == null || !StringUtils.equals(curEntity.getOrganizationType().getId(), newEntity.getOrganizationType().getId())) {
                curEntity.setOrganizationType(orgTypeDAO.findById(newEntity.getOrganizationType().getId()));
            }

            if (newEntity.getType() == null || StringUtils.isBlank(newEntity.getType().getId())) {
                curEntity.setType(null);
            } else if (curEntity.getType() == null || !StringUtils.equals(curEntity.getType().getId(), newEntity.getType().getId())) {
                curEntity.setType(typeDAO.findById(newEntity.getType().getId()));
            }

            orgDao.save(curEntity);
            final Organization org = organizationDozerConverter.convertToDTO(curEntity, false);

            if (!skipPrePostProcessors) {
                OrganizationServicePrePostProcessor postProcessor = getPostProcessScript();
                if (postProcessor != null) {
                    if (postProcessor.save(org, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                        idmAuditLog.fail();
                        idmAuditLog.setFailureReason(ResponseCode.FAIL_POSTPROCESSOR);
                        throw new BasicDataServiceException(ResponseCode.FAIL_POSTPROCESSOR);
                    }
                }
            }

            idmAuditLog.succeed();
            organization.setId(org.getId());
            return org;

        } finally {
            if(StringUtils.isBlank(idmAuditLog.getResult())) {
                idmAuditLog.fail();
            }
            auditLogService.enqueue(idmAuditLog);
        }
    }

    @Override
    @Transactional
    public void addRequiredAttributes(OrganizationEntity organization) {
        if(organization!=null && organization.getType()!=null && StringUtils.isNotBlank(organization.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(organization.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        orgAttrDao.save(AttributeUtil.buildOrgAttribute(organization, element));
                    }
                }
            }
        }
    }

    private ApproverAssociationEntity createDefaultApproverAssociations(final OrganizationEntity entity, final String requestorId) {
    	if(requestorId != null) {
			final ApproverAssociationEntity association = new ApproverAssociationEntity();
			association.setAssociationEntityId(entity.getId());
			association.setAssociationType(AssociationType.ORGANIZATION);
			association.setApproverLevel(Integer.valueOf(0));
			association.setApproverEntityId(requestorId);
			association.setApproverEntityType(AssociationType.USER);
			return association;
    	} else {
    		return null;
    	}
	}

    /*
	* This can be done better
    private void mergeParents(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getParentOrganizations() == null) {
            curEntity.setParentOrganizations(new HashSet<OrgToOrgMembershipXrefEntity>());
        }
        if (newEntity != null && newEntity.getParentOrganizations() != null) {
            final List<String> currIds = curEntity.getParentOrganizations().stream().map(e -> e.getEntity().getId()).collect(Collectors.toList());
            final Set<OrganizationEntity> toAdd = new HashSet<OrganizationEntity>();
            final Set<OrganizationEntity> toRemove = new HashSet<OrganizationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getParentOrganizations())) {
                Iterator<OrgToOrgMembershipXrefEntity> iterator = newEntity.getParentOrganizations().iterator();
                while (iterator.hasNext()) {
                	OrgToOrgMembershipXrefEntity xref = iterator.next();
                	final OrganizationEntity nop = xref.getEntity();
                    if (currIds.contains(nop.getId())) {
                        currIds.remove(nop.getId());
                        // parent org exists
                    } else {
                        // add
                        toAdd.add(orgDao.findById(nop.getId()));
                    }
                    //remove
                    for (OrganizationEntity cop : curEntity.getParentOrganizations().stream().map(mapper)) {
                        if (currIds.contains(cop.getId())) {
                            toRemove.add(cop);
                            break;
                        }
                    }
                    curEntity.getParentOrganizations().removeAll(toRemove);
                    curEntity.getParentOrganizations().addAll(toAdd);
                }

            } else {
                curEntity.getParentOrganizations().clear();
            }
        }
    }

    private void mergeChildren(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getChildOrganizations() == null) {
            curEntity.setChildOrganizations(new HashSet<OrganizationEntity>());
        }
        if (newEntity != null && newEntity.getChildOrganizations() != null) {
            List<String> currIds = new ArrayList<String>();
            for (OrganizationEntity coc : curEntity.getChildOrganizations()) {
                currIds.add(coc.getId());
            }
            final Set<OrganizationEntity> toAdd = new HashSet<OrganizationEntity>();
            final Set<OrganizationEntity> toRemove = new HashSet<OrganizationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getChildOrganizations())) {
                Iterator<OrganizationEntity> iterator = newEntity.getChildOrganizations().iterator();
                while (iterator.hasNext()) {
                    OrganizationEntity noc = iterator.next();
                    if (currIds.contains(noc.getId())) {
                        currIds.remove(noc.getId());
                        // child org exists
                    } else {
                        // add
                        toAdd.add(orgDao.findById(noc.getId()));
                    }
                    //remove
                    for (OrganizationEntity coc : curEntity.getChildOrganizations()) {
                        if (currIds.contains(coc.getId())) {
                            toRemove.add(coc);
                            break;
                        }
                    }
                    curEntity.getChildOrganizations().removeAll(toRemove);
                    curEntity.getChildOrganizations().addAll(toAdd);
                }

            } else {
                curEntity.getChildOrganizations().clear();
            }
        }
    }
    */

    private void mergeLocations(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getLocations() == null) {
            curEntity.setLocations(new HashSet<LocationEntity>());
        }
        if (newEntity != null && newEntity.getLocations() != null) {
            List<String> currIds = new ArrayList<String>();
            for (LocationEntity loc : curEntity.getLocations()) {
                currIds.add(loc.getId());
            }
            final Set<LocationEntity> toAdd = new HashSet<LocationEntity>();
            final Set<LocationEntity> toRemove = new HashSet<LocationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getLocations())) {
                Iterator<LocationEntity> iterator = newEntity.getLocations().iterator();
                while (iterator.hasNext()) {
                    LocationEntity nloc = iterator.next();
                    if (currIds.contains(nloc.getId())) {
                        currIds.remove(nloc.getId());
                        // location exists
                    } else {
                        // add
                        toAdd.add(locationDao.findById(nloc.getId()));
                    }
                    //remove
                    for (LocationEntity cloc : curEntity.getLocations()) {
                        if (currIds.contains(cloc.getId())) {
                            toRemove.add(cloc);
                            break;
                        }
                    }
                    curEntity.getLocations().removeAll(toRemove);
                    curEntity.getLocations().addAll(toAdd);
                }

            } else {
                curEntity.getLocations().clear();
            }
        }
    }

    /*
     * Can be done better
    private void mergeGroups(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getGroups() == null) {
            curEntity.setGroups(new HashSet<GroupEntity>());
        }
        if (newEntity != null && newEntity.getGroups() != null) {
            List<String> currIds = new ArrayList<String>();
            for (GroupEntity group : curEntity.getGroups()) {
                currIds.add(group.getId());
            }
            final Set<GroupEntity> toAdd = new HashSet<GroupEntity>();
            final Set<GroupEntity> toRemove = new HashSet<GroupEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getGroups())) {
                Iterator<GroupEntity> iterator = newEntity.getGroups().iterator();
                while (iterator.hasNext()) {
                    GroupEntity ngroup = iterator.next();
                    if (currIds.contains(ngroup.getId())) {
                        currIds.remove(ngroup.getId());
                        // group exists
                    } else {
                        // add
                        toAdd.add(groupDAO.findById(ngroup.getId()));
                    }
                    //remove
                    for (GroupEntity cgroup : curEntity.getGroups()) {
                        if (currIds.contains(cgroup.getId())) {
                            toRemove.add(cgroup);
                            break;
                        }
                    }
                    curEntity.getGroups().removeAll(toRemove);
                    curEntity.getGroups().addAll(toAdd);
                }

            } else {
                curEntity.getGroups().clear();
            }
        }
    }

    private void mergeUsers(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getUsers() == null) {
            curEntity.setUsers(new HashSet<UserEntity>());
        }
        if (newEntity != null && newEntity.getUsers() != null) {
            List<String> currIds = new ArrayList<String>();
            for (UserEntity cou : curEntity.getUsers()) {
                currIds.add(cou.getId());
            }
            final Set<UserEntity> toAdd = new HashSet<UserEntity>();
            final Set<UserEntity> toRemove = new HashSet<UserEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getUsers())) {
                Iterator<UserEntity> iterator = newEntity.getUsers().iterator();
                while (iterator.hasNext()) {
                    UserEntity nou = iterator.next();
                    if (currIds.contains(nou.getId())) {
                        currIds.remove(nou.getId());
                        // user exists
                    } else {
                        // add
                        toAdd.add(userDAO.findById(nou.getId()));
                    }
                    //remove
                    for (UserEntity cou : curEntity.getUsers()) {
                        if (currIds.contains(cou.getId())) {
                            toRemove.add(cou);
                            break;
                        }
                    }
                    curEntity.getUsers().removeAll(toRemove);
                    curEntity.getUsers().addAll(toAdd);
                }

            } else {
                curEntity.getUsers().clear();
            }
        }
    }
    */

    private void mergeApproverAssociations(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getApproverAssociations() == null) {
            curEntity.setApproverAssociations(new HashSet<ApproverAssociationEntity>());
        }
        if (newEntity != null && newEntity.getApproverAssociations() != null) {
            List<String> currIds = new ArrayList<String>();
            for (ApproverAssociationEntity caa : curEntity.getApproverAssociations()) {
                currIds.add(caa.getId());
            }
            final Set<ApproverAssociationEntity> toAdd = new HashSet<ApproverAssociationEntity>();
            final Set<ApproverAssociationEntity> toRemove = new HashSet<ApproverAssociationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getApproverAssociations())) {
                Iterator<ApproverAssociationEntity> iterator = newEntity.getApproverAssociations().iterator();
                while (iterator.hasNext()) {
                    ApproverAssociationEntity naa = iterator.next();
                    if (currIds.contains(naa.getId())) {
                        currIds.remove(naa.getId());
                        // approver association exists
                    } else {
                        // add
                        toAdd.add(approverAssociationDAO.findById(naa.getId()));
                    }
                    //remove
                    for (ApproverAssociationEntity cou : curEntity.getApproverAssociations()) {
                        if (currIds.contains(cou.getId())) {
                            toRemove.add(cou);
                            break;
                        }
                    }
                    curEntity.getApproverAssociations().removeAll(toRemove);
                    curEntity.getApproverAssociations().addAll(toAdd);
                }

            } else {
                curEntity.getApproverAssociations().clear();
            }
        }
    }

    private void mergeOrgProperties(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        BeanUtils.copyProperties(newEntity, curEntity,
                new String[] {"attributes", "parentOrganizations", "childOrganizations", "users", "approverAssociations",
                "groups", "locations", "organizationType", "type", "lstUpdate", "lstUpdatedBy", "createDate", "createdBy", "resources", "roles"});
    }

    private void mergeAttributes(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getAttributes() == null) {
            curEntity.setAttributes(new HashSet<OrganizationAttributeEntity>());
        }
        if (newEntity != null && newEntity.getAttributes() != null) {
            final List<String> currIds = new ArrayList<String>();
            for (OrganizationAttributeEntity oa : curEntity.getAttributes()) {
                currIds.add(oa.getId());
            }
            final Set<OrganizationAttributeEntity> toAdd = new HashSet<OrganizationAttributeEntity>();
            final Set<OrganizationAttributeEntity> toRemove = new HashSet<OrganizationAttributeEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getAttributes())) {
                Iterator<OrganizationAttributeEntity> iterator = newEntity.getAttributes().iterator();
                while (iterator.hasNext()) {
                    OrganizationAttributeEntity noa = iterator.next();
                    if (StringUtils.isBlank(noa.getId())) {
                        //add
                        noa.setOrganization(curEntity);
                        noa.setMetadataElementId(noa.getMetadataElementId());
                        toAdd.add(noa);

                    } else if (currIds.contains(noa.getId())) {
                        currIds.remove(noa.getId()); // least ids will be deleted
                        //update
                        for (OrganizationAttributeEntity oae : curEntity.getAttributes()) {
                            if (StringUtils.equals(oae.getId(), noa.getId())) {
                                oae.setValue(noa.getValue());
                                oae.setMetadataElementId(noa.getMetadataElementId());
                                oae.setName(noa.getName());
                                oae.setIsMultivalued(noa.getIsMultivalued());
                                oae.setValues(noa.getValues());
                                break;
                            }
                        }
                    }
                }
                //remove
                for (OrganizationAttributeEntity oae : curEntity.getAttributes()) {
                    if (currIds.contains(oae.getId())) {
                        toRemove.add(oae);
                    }
                }
                curEntity.getAttributes().removeAll(toRemove);
                curEntity.getAttributes().addAll(toAdd);

            } else {
                curEntity.getAttributes().clear();
            }
        }
	}
    
    @Override
    @Transactional
    @CacheKeyEvictions({
    	@CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("organizations"),
                @CacheKeyEvict("organizationEntities")
            },
            parameterIndex=0
        ),
        @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("organizations"),
                @CacheKeyEvict("organizationEntities")
            },
            parameterIndex=1
        )
    })
    public void removeChildOrganization(final String organizationId, 
    									final String childOrganizationId) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.removeChild(child);
            orgDao.update(parent);
        }
    }

    @Override
    @Transactional
    @CacheKeyEvictions(value={
    	@CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("organizations"),
                @CacheKeyEvict("organizationEntities")
            },
            parameterIndex=0
        ),
        @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("organizations"),
                @CacheKeyEvict("organizationEntities")
            },
            parameterIndex=1
        )
    })
    public void addChildOrganization(final String organizationId, 
    								 final String childOrganizationId, 
    								 final Set<String> rightIds, 
    								 final Date startDate, 
    								 final Date endDate) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.addChild(child, accessRightDAO.findByIds(rightIds), startDate, endDate);
            orgDao.update(parent);
        }
    }

    @Override
    @Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("organizations"),
            @CacheKeyEvict("organizationEntities")
        }
    )
    public void deleteOrganization(final String orgId) throws BasicDataServiceException {
        deleteOrganization(orgId, false);
    }

    @Override
    @Transactional
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("organizations"),
            @CacheKeyEvict("organizationEntities")
        }
    )
    public void deleteOrganization(final String orgId, boolean skipPrePostProcessors) throws BasicDataServiceException {

        // Audit Log -----------------------------------------------------------------------------------
        final IdmAuditLogEntity idmAuditLog = new IdmAuditLogEntity();
        idmAuditLog.setAction(AuditAction.DELETE_ORG.value());

        try {
            if (orgId == null) {
                idmAuditLog.setFailureReason(ResponseCode.INVALID_ARGUMENTS);
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final OrganizationEntity entity = orgDao.findById(orgId);
            if (entity != null) {
                idmAuditLog.setTargetOrg(orgId, entity.getName());
            }

            Map<String, Object> bindingMap = new HashMap<String, Object>();
            if (!skipPrePostProcessors) {
                OrganizationServicePrePostProcessor preProcessor = getPreProcessScript();
                if (preProcessor != null &&  preProcessor.delete(orgId, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                    idmAuditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                    throw new BasicDataServiceException(ResponseCode.FAIL_PREPROCESSOR);
                }
            }

            if (entity != null) {
                orgDao.delete(entity);
            }

            if (!skipPrePostProcessors) {
                OrganizationServicePrePostProcessor postProcessor = getPostProcessScript();
                if (postProcessor != null &&  postProcessor.delete(orgId, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                    idmAuditLog.setFailureReason(ResponseCode.FAIL_POSTPROCESSOR);
                    throw new BasicDataServiceException(ResponseCode.FAIL_POSTPROCESSOR);
                }
            }

            idmAuditLog.succeed();

        } finally {
            if(StringUtils.isBlank(idmAuditLog.getResult())) {
                idmAuditLog.fail();
            }
            auditLogService.enqueue(idmAuditLog);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getDelegationFilter(String requesterId) {
        return getDelegationFilter(requesterId, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap) {
        return getDelegationFilter(attrMap, false);
    }


    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap,  boolean isUncoverParents) {
        Set<String> filterData = new HashSet<String>();
        if(attrMap!=null && !attrMap.isEmpty()){
            boolean isUseOrgInhFlag = DelegationFilterHelper.isUseOrgInhFilterSet(attrMap);

            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag, false));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag, isUncoverParents));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag, isUncoverParents));
        }
        return filterData;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId, final LanguageEntity langauge){
        Set<String> filterData = null;
        Set<String> allowedOrgTypes = null;
        Map<String, UserAttribute> requesterAttributes = null;
        if (StringUtils.isNotBlank(requesterId)) {
            requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, false);
        }
        allowedOrgTypes = organizationTypeService.getAllowedParentsIds(orgTypeId, requesterAttributes);
//        allowedOrgTypes.retainAll(allowedParentTypesIds);

        return orgDao.findAllByTypesAndIds(allowedOrgTypes, filterData);
    }

@Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getAllowedParentOrganizationsDtoForType(final String orgTypeId, String requesterId, final LanguageEntity language) {
        Set<String> filterData = null;
        Set<String> allowedOrgTypes = null;
        Map<String, UserAttribute> requesterAttributes = null;
        if (StringUtils.isNotBlank(requesterId)) {
            requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, false);
        }
        allowedOrgTypes = organizationTypeService.getAllowedParentsIds(orgTypeId, requesterAttributes);
//        allowedOrgTypes.retainAll(allowedParentTypesIds);

        List<OrganizationEntity> organizationEntityList = orgDao.findAllByTypesAndIds(allowedOrgTypes, filterData);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap, boolean isUseOrgInhFlag){
        Set<String> filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag, false);
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag, false));
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag, false));
        return filterData;
    }

	@Override
	@LocalizedServiceGet
    @Transactional(readOnly = true)
	public Organization getOrganizationDTO(String orgId, final LanguageEntity langauge) {
		return organizationDozerConverter.convertToDTO(getOrganizationLocalized(orgId, langauge), true);
	}

	@Override
	@Transactional
	public void validateOrg2OrgAddition(String parentId, String memberId, final Set<String> rightIds)
			throws BasicDataServiceException {
		final OrganizationEntity parent = orgDao.findById(parentId);
		final OrganizationEntity child = orgDao.findById(memberId);
		if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

		/*
        if (parent.hasChildOrganization(memberId)) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }
        */

        if (causesCircularDependency(parent, child, new HashSet<OrganizationEntity>())) {
            throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
        }

        if (parentId.equals(memberId)) {
            throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
        }
	}

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue, final LanguageEntity langauge) {
    	final OrganizationSearchBean searchBean = new OrganizationSearchBean();
    	searchBean.addAttribute(attrName, attrValue);
        return orgDao.getByExample(searchBean);
    }
	
	private boolean causesCircularDependency(final OrganizationEntity parent, final OrganizationEntity child, final Set<OrganizationEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentOrganizations())) {
                    for (final OrgToOrgMembershipXrefEntity xref : parent.getParentOrganizations()) {
                    	final OrganizationEntity entity = xref.getEntity();
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
    @Transactional
    @Scheduled(fixedRateString="${org.openiam.org.manager.threadsweep}", initialDelayString="${org.openiam.org.manager.threadsweep}")
    public void sweep() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
		        final StopWatch sw = new StopWatch();
		        sw.start();
		        fireUpdateOrgMap();
		        sw.stop();
		        log.debug(String.format("Done creating orgs trees. Took: %s ms", sw.getTime()));
		        return null;
            }
        });
    }

    @Transactional(readOnly = true)
    public void fireUpdateOrgMap() {
        List<OrgToOrgMembershipXrefEntity> xrefList = orgDao.getOrg2OrgXrefs();

        final Map<String, Set<String>> parentOrg2ChildOrgMap = new HashMap<String, Set<String>>();
        final Map<String, String> child2ParentOrgMap = new HashMap<String, String>();

        for(final OrgToOrgMembershipXrefEntity xref : xrefList) {
            final String orgId = xref.getEntity().getId();
            final String memberOrgId = xref.getMemberEntity().getId();

            if(!parentOrg2ChildOrgMap.containsKey(orgId)) {
                parentOrg2ChildOrgMap.put(orgId, new HashSet<String>());
            }

            child2ParentOrgMap.put(memberOrgId, orgId);
            parentOrg2ChildOrgMap.get(orgId).add(memberOrgId);
        }
        organizationTree = parentOrg2ChildOrgMap;
        organizationInvertedTree = child2ParentOrgMap;
    }

    private Set<String> getOrgTreeFlatList(List<String> rootElementsIdList, boolean isUseOrgInhFlag, boolean isUncoverParents){
        List<String> result = new ArrayList<String>();
        if(isUseOrgInhFlag){
            if(CollectionUtils.isNotEmpty(rootElementsIdList)){
                for (String rootElementId : rootElementsIdList){
                    result.addAll(getOrgTreeFlatList(rootElementId));
                }
            }
        } else {
            result = rootElementsIdList;
        }
        if (isUncoverParents) {
            for (String elementId : rootElementsIdList) {
                result.addAll(getParentsFlatList(elementId));
            }
        }
        return new HashSet<String>(result);
    }

    private List<String> getOrgTreeFlatList(String rootId){
        List<String> result = new ArrayList<String>();
        if(StringUtils.isNotBlank(rootId)){
            result.add(rootId);
            for(int i=0; i<result.size();i++){
                String curElem = result.get(i);
                if(this.organizationTree.containsKey(curElem)){
                    result.addAll(this.organizationTree.get(curElem));
                }
            }
        }
        return result;
    }

    private List<String> getParentsFlatList(String childId) {
        List<String> result = new ArrayList<String>();
        String elementId = this.organizationInvertedTree.get(childId);
        while (StringUtils.isNotBlank(elementId)) {
            result.add(elementId);
            elementId = this.organizationInvertedTree.get(elementId);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public void afterPropertiesSet() throws Exception {
    	transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				sweep();
	    		return null;
			}
    		
		});
    }


    @Deprecated
    @Transactional(readOnly = true)
    public Organization getOrganizationDTO(final String orgId){
        return this.getOrganizationDTO(orgId, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganization(String orgId){
        return this.getOrganization(orgId, null);
    }
    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganization(final String orgId, String requesterId){
        return this.getOrganizationLocalized(orgId, requesterId, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganizationByName(final String name, String requesterId){
        return this.getOrganizationByName(name, requesterId, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId){
        return this.getAllowedParentOrganizationsForType(orgTypeId, requesterId, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue){
        return this.findOrganizationsByAttributeValue(attrName, attrValue, getDefaultLanguage());
    }

    private LanguageEntity getDefaultLanguage(){
        LanguageEntity lang = new LanguageEntity();
        lang.setId("1");
        return lang;
    }

    @Transactional(readOnly = true)
    public void validate(final Organization organization) throws BasicDataServiceException {
        validateEntity(organizationDozerConverter.convertToEntity(organization, true));
    }
    
    private OrganizationEntity getByNameAndType(final String name, final String typeId) {
    	final OrganizationSearchBean sb = new OrganizationSearchBean();
    	sb.setNameToken(new SearchParam(name, MatchType.EXACT));
        sb.addOrganizationTypeId(typeId);
        
        /* db constraing would prevent more than 1 */
        final List<OrganizationEntity> found = orgDao.getByExample(sb);
        return (CollectionUtils.isNotEmpty(found)) ? found.get(0) : null;
    }

    private void validateEntity(final OrganizationEntity entity) throws BasicDataServiceException {
        if (entity == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        if (StringUtils.isBlank(entity.getName())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_NAME_NOT_SET);
        }
        
        final OrganizationSearchBean sb = new OrganizationSearchBean();
        sb.setNameToken(new SearchParam(entity.getName(), MatchType.EXACT));
        if(entity.getType() != null && StringUtils.isNotBlank(entity.getType().getId())) {
        	sb.addOrganizationTypeId(entity.getType().getId());
        }
        

        if(entity.getOrganizationType() == null || StringUtils.isBlank(entity.getOrganizationType().getId())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_NOT_SET);
        }

        final OrganizationEntity nameEntity = getByNameAndType(entity.getName(), entity.getOrganizationType().getId());
        if(nameEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !entity.getId().equals(nameEntity.getId())) {
				throw new BasicDataServiceException(ResponseCode.CONSTRAINT_VIOLATION, "Organization Name + TypeID combination taken");
			}
		}

        entityValidator.isValid(entity);
    }

    private OrganizationServicePrePostProcessor getPreProcessScript() {
        try {
            return (OrganizationServicePrePostProcessor) scriptRunner.instantiateClass(new HashMap<String, Object>(), preProcessorOrganization);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    private OrganizationServicePrePostProcessor getPostProcessScript() {
        try {
            return (OrganizationServicePrePostProcessor) scriptRunner.instantiateClass(new HashMap<String, Object>(), postProcessorOrganization);
        } catch (Exception ce) {
            log.error(ce);
            return null;
        }
    }

    /// LOCATIONS

    @Override
    @Transactional
    public void addLocation(LocationEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getOrganization() == null || StringUtils.isBlank(val.getOrganization().getId()))
            throw new NullPointerException("organizationId for the location is not defined.");

        final OrganizationEntity org = orgDao.findById(val.getOrganization().getId());
        val.setOrganization(org);
        locationDao.save(val);
    }


    @Override
    @Transactional
    public void updateLocation(LocationEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getId() == null)
            throw new NullPointerException("LocationId is null");
        if (val.getOrganization() == null || StringUtils.isBlank(val.getOrganization().getId()))
            throw new NullPointerException("organizationId for the location is not defined.");

        final LocationEntity entity = locationDao.findById(val.getId());
        final OrganizationEntity org = orgDao.findById(val.getOrganization().getId());

        if (entity != null && org != null) {
            entity.setName(val.getName());
            entity.setDescription(val.getDescription());
            entity.setCountry(val.getCountry());
            entity.setBldgNum(val.getBldgNum());
            entity.setStreetDirection(val.getStreetDirection());
            entity.setAddress1(val.getAddress1());
            entity.setAddress2(val.getAddress2());
            entity.setAddress3(val.getAddress3());
            entity.setCity(val.getCity());
            entity.setState(val.getState());
            entity.setPostalCd(val.getPostalCd());
            entity.setOrganization(val.getOrganization());
            entity.setOrganization(org);
            entity.setInternalLocationId(val.getInternalLocationId());
            entity.setIsActive(val.getIsActive());
            entity.setSensitiveLocation(val.getSensitiveLocation());

            locationDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void removeLocation(final String locationId) {
        final LocationEntity entity = locationDao.findById(locationId);

        if(entity != null) {
            locationDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public void removeAllLocations(String organizationId) {
        if (organizationId == null)
            throw new NullPointerException("organizationId is null");

        locationDao.removeByOrganizationId(organizationId);

    }

    @Override
    @Transactional(readOnly = true)
    public LocationEntity getLocationById(String locationId) {
        if (locationId == null)
            throw new NullPointerException("locationId is null");
        return locationDao.findById(locationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Location getLocationDtoById(String locationId) {
        /*if (locationId == null)
            throw new NullPointerException("locationId is null");*/
        //LocationEntity locationEntity = locationDao.findById(locationId);
        LocationEntity locationEntity = this.getProxyService().getLocationById(locationId);
        return locationDozerConverter.convertToDTO(locationEntity, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(String organizationId) {
        return this.getLocationList(organizationId, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfLocations(LocationSearchBean searchBean) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return locationDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(String organizationId, boolean isDeep) {
        return locationDozerConverter.convertToDTOList(getLocationList(organizationId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(String organizationId, Integer from, Integer size ) {
        if (organizationId == null)
            throw new NullPointerException("organizationId is null");

        LocationSearchBean searchBean = new LocationSearchBean();
        searchBean.setOrganizationId(organizationId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        return getLocationList(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(String organizationId, Integer from, Integer size) {
        /*if (organizationId == null)
            throw new NullPointerException("organizationId is null");

        LocationSearchBean searchBean = new LocationSearchBean();
        searchBean.setOrganizationId(organizationId);*/
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        //List<LocationEntity> locationEntityList = getLocationList(searchBean, from, size);
        List<LocationEntity> locationEntityList = this.getProxyService().getLocationList(organizationId, from, size);
        return locationDozerConverter.convertToDTOList(locationEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(LocationSearchBean searchBean, Integer from, Integer size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return locationDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(LocationSearchBean searchBean, Integer from, Integer size) {
        /*if (searchBean == null)
            throw new NullPointerException("searchBean is null");*/

        //List<LocationEntity> locationEntityList = locationDao.getByExample(locationSearchBeanConverter.convert(searchBean), from, size);

        List<LocationEntity> locationEntityList = this.getProxyService().getLocationList(searchBean, from, size);

        return locationDozerConverter.convertToDTOList(locationEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfLocationsForOrganization(String organizationId) {
        return orgDao.findById(organizationId).getLocations().size();
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfLocationsForUser(String userId) {
    	final OrganizationSearchBean sb = new OrganizationSearchBean();
    	sb.addUserId(userId);
        final List<OrganizationEntity> orgList = orgDao.getByExample(sb);
        int count = 0;
        for (OrganizationEntity org : orgList) {
            count = count + org.getLocations().size();
        }
        return count;
    }

    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId, Integer from, Integer size) {
        return locationDao.findByOrganizationList(orgsId, from, size);
    }

    public List<LocationEntity> getLocationListByOrganizationId(Set<String> orgsId) {
        return locationDao.findByOrganizationList(orgsId);
    }

	@Override
	@Transactional
	public void addGroupToOrganization(final String organizationId, 
									   final String groupId,
									   final Set<String> rightIds,
									   final Date startDate, 
									   final Date endDate) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final GroupEntity group = groupDAO.findById(groupId);
		if(organization != null && group != null) {
			organization.addGroup(group, accessRightDAO.findByIds(rightIds), startDate, endDate);
			orgDao.update(organization);
		}
	}

	@Override
	@Transactional
	public void removeGroupFromOrganization(String organizationId,
			String groupId) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final GroupEntity group = groupDAO.findById(groupId);
		if(organization != null && group != null) {
			organization.removeGroup(group);
			orgDao.update(organization);
		}
	}

	@Override
	@Transactional
	public void addRoleToOrganization(final String organizationId, 
									  final String roleId, 
									  final Set<String> rightIds, 
									  final Date startDate, 
									  final Date endDate) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final RoleEntity role = roleDAO.findById(roleId);
		if(organization != null && role != null) {
			organization.addRole(role, accessRightDAO.findByIds(rightIds), startDate, endDate);
			orgDao.update(organization);
		}
	}

	@Override
	@Transactional
	public void removeRoleFromOrganization(final String organizationId, final String roleId) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final RoleEntity role = roleDAO.findById(roleId);
		if(organization != null && role != null) {
			organization.removeRole(role);
			orgDao.update(organization);
		}
	}

	@Override
	public void addResourceToOrganization(final String organizationId,
										  final String resourceId, 
										  final Set<String> rightIds, 
										  final Date startDate, 
										  final Date endDate) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final ResourceEntity resource = resourceDAO.findById(resourceId);
		if(organization != null && resource != null) {
			organization.addResource(resource, accessRightDAO.findByIds(rightIds), startDate, endDate);
			orgDao.update(organization);
		}
	}

	@Override
	public void removeResourceFromOrganization(final String organizationId, final String resourceId) {
		final OrganizationEntity organization = orgDao.findById(organizationId);
		final ResourceEntity resource = resourceDAO.findById(resourceId);
		if(organization != null && resource != null) {
			organization.removeResource(resource);
			orgDao.update(organization);
		}
	}

    @Transactional(readOnly = true)
    public Map<String, OrganizationAttribute> getOrgAttributesDto(String orgId) {
        Map<String, OrganizationAttribute> attributeMap = new HashMap<String, OrganizationAttribute>();
        if (StringUtils.isNotEmpty(orgId)) {
            List<OrganizationAttribute> orgAttributes = getOrgAttributesDtoList(orgId);
            if (CollectionUtils.isNotEmpty(orgAttributes)) {
                for (OrganizationAttribute attr : orgAttributes) {
                    attributeMap.put(attr.getName(), attr);
                }
            }
        }
        return attributeMap;
    }

    @Transactional(readOnly = true)
    public List<OrganizationAttribute> getOrgAttributesDtoList(String orgId) {
        if (StringUtils.isNotEmpty(orgId)) {
            List<OrganizationAttributeEntity> attributeEntities = orgAttrDao.findOrgAttributes(orgId);
            return organizationAttributeDozerConverter.convertToDTOList(attributeEntities, false);
        }
        return null;
    }

    private OrganizationService getProxyService() {
        OrganizationService service = (OrganizationService) ac.getBean("organizationService");
        return service;
    }

    @Override
    @Transactional
    public void saveAttribute(final OrganizationAttributeEntity attribute) {
        if(StringUtils.isNotBlank(attribute.getId())) {
            orgAttrDao.update(attribute);
        } else {
            orgAttrDao.save(attribute);
        }
    }


    @Override
    public Response saveOrganization(final Organization organization, final String requesterId) {
        return saveOrganizationWithSkipPrePostProcessors(organization, requesterId, false);
    }

    @Override
    public Response saveOrganizationWithSkipPrePostProcessors(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            Organization org = this.save(organization, requestorId, skipPrePostProcessors);
            response.setResponseValue(org.getId());

        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch (Throwable e) {
            log.error("Can't save organization", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    @Override
    public Response deleteOrganization(final String orgId, final String requestorId) {
        return deleteOrganizationWithSkipPrePostProcessors(orgId, false, requestorId);
    }

    @Override
    public Response deleteOrganizationWithSkipPrePostProcessors(final String orgId, final boolean skipPrePostProcessors, final String requestorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            this.deleteOrganization(orgId, skipPrePostProcessors);

        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());

        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response addUserToOrg(final String orgId,
                                 final String userId,
                                 final String requestorId,
                                 final Set<String> rightIds,
                                 final Date startDate,
                                 final Date endDate) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (orgId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if(startDate != null && endDate != null && startDate.after(endDate)) {
                throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
            }

            this.addUserToOrg(orgId, userId, rightIds, startDate, endDate);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response removeUserFromOrg(String orgId, String userId, final String requestorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (orgId == null || userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            this.removeUserFromOrg(orgId, userId);
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch (Throwable e) {
            log.error("Can't save resource type", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
