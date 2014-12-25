package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.LocationDozerConverter;
import org.openiam.dozer.converter.OrganizationAttributeDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.openiam.idm.srvc.loc.dto.Location;
import org.openiam.idm.srvc.loc.service.LocationDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.searchbean.converter.LocationSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl extends AbstractBaseService implements OrganizationService, InitializingBean {
    private static final Log log = LogFactory.getLog(OrganizationServiceImpl.class);
	@Autowired
	private OrganizationTypeDAO orgTypeDAO;

    @Autowired
    private LocationDozerConverter locationDozerConverter;
    @Autowired
    private LocationDAO locationDao;

    @Autowired
    private LocationSearchBeanConverter locationSearchBeanConverter;

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
    
	@Value("${org.openiam.resource.admin.resource.type.id}")
	private String adminResourceTypeId;
	
	@Autowired
    private ResourceTypeDAO resourceTypeDao;
	
    @Autowired
    private MetadataTypeDAO typeDAO;
    
    @Autowired
    private GroupDAO groupDAO;

    private Map<String, Set<String>> organizationTree;

    @Value("${org.openiam.organization.type.id}")
    private String organizationTypeId;
    @Value("${org.openiam.division.type.id}")
    private String divisionTypeId;
    @Value("${org.openiam.department.type.id}")
    private String departmentTypeId;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Autowired
    protected String preProcessorOrganization;

    @Autowired
    protected String postProcessorOrganization;

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
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId, null))) {
            return orgDao.findById(orgId);
        }
        return null;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganizationByName(final String name, String requesterId, final LanguageEntity langauge) {
        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setName(name);
        final List<OrganizationEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1, null);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getNumOfOrganizationsForUser(final String userId, final String requesterId) {
    	return orgDao.getNumOfOrganizationsForUser(userId, getDelegationFilter(requesterId, null));
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge) {
    	return orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size, final LanguageEntity langauge) {
        Set<String> filter = getDelegationFilter(requesterId, null);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<OrganizationEntity>(0);
        }
        return orgDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getParentOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getChildOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        return orgDao.getChildOrganizations(orgId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId, searchBean.getOrganizationTypeId());
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return 0;
        }

        return orgDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfParentOrganizations(orgId, getDelegationFilter(requesterId, null));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfChildOrganizations(orgId, getDelegationFilter(requesterId, null));
    }

    @Override
    @Transactional
    public void addUserToOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        user.getAffiliations().add(organization);
    }

    @Override
    @Transactional
    public void removeUserFromOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        user.getAffiliations().remove(organization);
    }

    @Override
    @Transactional
    public void removeAttribute(String attributeId) {
        final OrganizationAttributeEntity entity = orgAttrDao.findById(attributeId);
        if (entity != null) {
            orgAttrDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public Organization save(final Organization organization, final String requestorId) throws BasicDataServiceException {
        return save(organization, requestorId, false);
    }

    @Override
    @Transactional
    public Organization save(final Organization organization, final String requestorId, final boolean skipPrePostProcessors) throws BasicDataServiceException {

        // Audit Log -----------------------------------------------------------------------------------
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
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
                curEntity.setAdminResource(getNewAdminResource(curEntity, requestorId));
                curEntity.setCreateDate(Calendar.getInstance().getTime());
                curEntity.setCreatedBy(requestorId);
                curEntity.addApproverAssociation(createDefaultApproverAssociations(curEntity, requestorId));
                addRequiredAttributes(curEntity);

            } else {
                curEntity = orgDao.findById(organization.getId());
                mergeOrgProperties(curEntity, newEntity);
                mergeAttributes(curEntity, newEntity);
                mergeParents(curEntity, newEntity);
                mergeChildren(curEntity, newEntity);
                mergeUsers(curEntity, newEntity);
                mergeApproverAssociations(curEntity, newEntity);

                if(curEntity.getAdminResource() == null) {
                    curEntity.setAdminResource(getNewAdminResource(curEntity, requestorId));
                }

                curEntity.getAdminResource().setCoorelatedName(curEntity.getName());
                curEntity.setLstUpdate(Calendar.getInstance().getTime());
                curEntity.setLstUpdatedBy(requestorId);

            }

            if (newEntity.getOrganizationType() == null) {
                curEntity.setOrganizationType(null);
            } else if (curEntity.getOrganizationType() == null || StringUtils.equals(curEntity.getOrganizationType().getId(), newEntity.getOrganizationType().getId())) {
                curEntity.setOrganizationType(orgTypeDAO.findById(newEntity.getOrganizationType().getId()));
            }

            if (newEntity.getType() == null) {
                curEntity.setType(null);
            } else if (curEntity.getType() == null || StringUtils.equals(curEntity.getType().getId(), newEntity.getType().getId())) {
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

    private ResourceEntity getNewAdminResource(final OrganizationEntity entity, final String requestorId) {
		final ResourceEntity adminResource = new ResourceEntity();
		adminResource.setName(String.format("ORG_ADMIN_%s_%s", entity.getName(), RandomStringUtils.randomAlphanumeric(2)));
		adminResource.setResourceType(resourceTypeDao.findById(adminResourceTypeId));
		adminResource.addUser(userDAO.findById(requestorId));
		adminResource.setCoorelatedName(entity.getName());
		return adminResource;
	}
    
    private ApproverAssociationEntity createDefaultApproverAssociations(final OrganizationEntity entity, final String requestorId) {
		final ApproverAssociationEntity association = new ApproverAssociationEntity();
		association.setAssociationEntityId(entity.getId());
		association.setAssociationType(AssociationType.ORGANIZATION);
		association.setApproverLevel(Integer.valueOf(0));
		association.setApproverEntityId(requestorId);
		association.setApproverEntityType(AssociationType.USER);
		return association;
	}

    private void mergeParents(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getParentOrganizations() == null) {
            curEntity.setParentOrganizations(new HashSet<OrganizationEntity>());
        }
        if (newEntity != null && newEntity.getParentOrganizations() != null) {
            List<String> currIds = new ArrayList<String>();
            for (OrganizationEntity cpo : curEntity.getParentOrganizations()) {
                currIds.add(cpo.getId());
            }
            final Set<OrganizationEntity> toAdd = new HashSet<OrganizationEntity>();
            final Set<OrganizationEntity> toRemove = new HashSet<OrganizationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getParentOrganizations())) {
                Iterator<OrganizationEntity> iterator = newEntity.getParentOrganizations().iterator();
                while (iterator.hasNext()) {
                    OrganizationEntity nop = iterator.next();
                    if (currIds.contains(nop.getId())) {
                        currIds.remove(nop.getId());
                        // parent org exists
                    } else {
                        // add
                        toAdd.add(orgDao.findById(nop.getId()));
                    }
                    //remove
                    for (OrganizationEntity cop : curEntity.getParentOrganizations()) {
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
                "adminResource", "organizationType", "type", "lstUpdate", "lstUpdatedBy", "createDate", "createdBy"});
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
                        noa.setElement(getEntity(noa.getElement()));
                        toAdd.add(noa);

                    } else if (currIds.contains(noa.getId())) {
                        currIds.remove(noa.getId()); // least ids will be deleted
                        //update
                        for (OrganizationAttributeEntity oae : curEntity.getAttributes()) {
                            if (StringUtils.equals(oae.getId(), noa.getId())) {
                                oae.setValue(noa.getValue());
                                oae.setElement(getEntity(noa.getElement()));
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
    
    private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
    	if(bean != null && StringUtils.isNotBlank(bean.getId())) {
    		return metadataElementDAO.findById(bean.getId());
    	} else {
    		return null;
    	}
    }
    
    private void setMetadataTypeOnOrgAttribute(final OrganizationAttributeEntity bean) {
    	if(bean.getElement() != null && bean.getElement().getId() != null) {
    		bean.setElement(metadataElementDAO.findById(bean.getElement().getId()));
		} else {
			bean.setElement(null);
		}
    }

    @Override
    @Transactional
    public void save(OrganizationAttributeEntity attribute) {
    	attribute.setElement(metadataDAO.findById(attribute.getElement().getId()));
    	attribute.setOrganization(orgDao.findById(attribute.getOrganization().getId()));
    	
        if (StringUtils.isNotBlank(attribute.getId())) {
            orgAttrDao.merge(attribute);
        } else {
            orgAttrDao.save(attribute);
        }
    }

    @Override
    @Transactional
    public void removeChildOrganization(String organizationId, String childOrganizationId) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.removeChildOrganization(childOrganizationId);
            orgDao.update(parent);
        }
    }

    @Override
    @Transactional
    public void addChildOrganization(String organizationId, String childOrganizationId) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.addChildOrganization(child);
            orgDao.update(parent);
        }
    }

    @Override
    @Transactional
    public void deleteOrganization(String orgId) throws BasicDataServiceException {
        deleteOrganization(orgId, false);
    }

    @Override
    @Transactional
    public void deleteOrganization(String orgId, boolean skipPrePostProcessors) throws BasicDataServiceException {

        // Audit Log -----------------------------------------------------------------------------------
        final IdmAuditLog idmAuditLog = new IdmAuditLog();
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
                final GroupEntity example = new GroupEntity();
                example.setCompany(entity);
                final List<GroupEntity> groups = groupDAO.getByExample(example);
                if(groups != null) {
                    for(final GroupEntity group : groups) {
                        group.setCompany(null);
                        groupDAO.update(group);
                    }
                }
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
    public Set<String> getDelegationFilter(String requesterId, String organizationTypeId) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, organizationTypeId);

        }

        return filterData;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, String organizationTypeId) {
        Set<String> filterData = new HashSet<String>();
        if(attrMap!=null && !attrMap.isEmpty()){
            boolean isUseOrgInhFlag = DelegationFilterHelper.isUseOrgInhFilterSet(attrMap);

            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag));
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
            filterData = getDelegationFilter(requesterAttributes, organizationTypeId);
        }
        allowedOrgTypes = organizationTypeService.getAllowedParentsIds(orgTypeId, requesterAttributes);
//        allowedOrgTypes.retainAll(allowedParentTypesIds);

        return orgDao.findAllByTypesAndIds(allowedOrgTypes, filterData);
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap, boolean isUseOrgInhFlag){
        Set<String> filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag);
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag));
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag));
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
	public void validateOrg2OrgAddition(String parentId, String memberId)
			throws BasicDataServiceException {
		final OrganizationEntity parent = orgDao.findById(parentId);
		final OrganizationEntity child = orgDao.findById(memberId);
		if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (parent.hasChildOrganization(memberId)) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }

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
        return orgDao.findOrganizationsByAttributeValue(attrName, attrValue);
    }
	
	private boolean causesCircularDependency(final OrganizationEntity parent, final OrganizationEntity child, final Set<OrganizationEntity> visitedSet) {
        boolean retval = false;
        if (parent != null && child != null) {
            if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentOrganizations())) {
                    for (final OrganizationEntity entity : parent.getParentOrganizations()) {
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

    @Transactional(readOnly = true)
    public void fireUpdateOrgMap() {
        List<Org2OrgXrefEntity> xrefList = orgDao.getOrgToOrgXrefList();

        final Map<String, Set<String>> parentOrg2ChildOrgMap = new HashMap<String, Set<String>>();
        final Map<String, String> child2ParentOrgMap = new HashMap<String, String>();

        for(final Org2OrgXrefEntity xref : xrefList) {
            final String orgId = xref.getId().getOrganizationId();
            final String memberOrgId = xref.getId().getMemberOrganizationId();

            if(!parentOrg2ChildOrgMap.containsKey(orgId)) {
                parentOrg2ChildOrgMap.put(orgId, new HashSet<String>());
            }

            child2ParentOrgMap.put(memberOrgId, orgId);
            parentOrg2ChildOrgMap.get(orgId).add(memberOrgId);
        }
        organizationTree = parentOrg2ChildOrgMap;
    }

    private Set<String> getOrgTreeFlatList(List<String> rootElementsIdList, boolean isUseOrgInhFlag){
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

    @Override
    @Transactional(readOnly = true)
    public void afterPropertiesSet() throws Exception {
        fireUpdateOrgMap();
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
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size){
        return this.getOrganizationsForUser(userId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size){
        return this.getParentOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size){
        return this.getChildOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size){
        return this.findBeans(searchBean, requesterId, from, size, getDefaultLanguage());
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

    private void validateEntity(final OrganizationEntity organization) throws BasicDataServiceException {
        if (organization == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        if (StringUtils.isBlank(organization.getName())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_NAME_NOT_SET);
        }

        final OrganizationEntity found = getOrganizationByName(organization.getName(), null, null);
        if (found != null) {
            if (StringUtils.isBlank(organization.getId()) && found != null) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
            }

            if (StringUtils.isNotBlank(organization.getId()) && !organization.getId().equals(found.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
            }
        }

        if(organization.getOrganizationType() == null) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_NOT_SET);
        }

        entityValidator.isValid(organization);
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

        if (val.getOrganizationId() == null)
            throw new NullPointerException("organizationId for the location is not defined.");


        OrganizationEntity org = orgDao.findById(val.getOrganizationId());
        val.setOrganization(org);


        locationDao.save(val);
    }


    @Override
    @Transactional
    public void updateLocation(LocationEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getLocationId() == null)
            throw new NullPointerException("LocationId is null");
        if (val.getOrganizationId() == null)
            throw new NullPointerException("organizationId for the location is not defined.");

        final LocationEntity entity = locationDao.findById(val.getLocationId());
        OrganizationEntity org = orgDao.findById(val.getOrganizationId());

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
            entity.setOrganizationId(val.getOrganizationId());
            entity.setInternalLocationId(val.getInternalLocationId());
            entity.setActive(val.isActive());
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
    public List<LocationEntity> getLocationList(String organizationId) {
        return this.getLocationList(organizationId, Integer.MAX_VALUE, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(String organizationId, boolean isDeep) {
        return locationDozerConverter.convertToDTOList(getLocationList(organizationId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(String organizationId, Integer size, Integer from) {
        if (organizationId == null)
            throw new NullPointerException("organizationId is null");

        LocationSearchBean searchBean = new LocationSearchBean();
        searchBean.setOrganizationId(organizationId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        return getLocationList(searchBean, size, from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(LocationSearchBean searchBean, Integer size, Integer from) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return locationDao.getByExample(locationSearchBeanConverter.convert(searchBean), from, size);
    }
}
