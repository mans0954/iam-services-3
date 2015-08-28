package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LocationSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
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
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
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
    private OrganizationUserDozerConverter organizationUserDozerConverter;

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

    @Autowired
    private LanguageDozerConverter languageConverter;

    private Map<String, Set<String>> organizationTree;
    private Map<String, String> organizationInvertedTree;

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
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId))) {
            return orgDao.findById(orgId);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public Organization getOrganizationLocalizedDto(String orgId, String requesterId, final LanguageEntity langauge) {
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId))) {
            OrganizationEntity organizationEntity = orgDao.findById(orgId);
            return organizationDozerConverter.convertToDTO(organizationEntity, true);
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
        return orgDao.getNumOfOrganizationsForUser(userId, getDelegationFilter(requesterId));
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge) {
        return orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId), from, size);
    }

    @Override
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
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getUserAffiliationsByType(String userId, String typeId, String requesterId, final int from, final int size, final LanguageEntity langauge) {
        List<OrganizationEntity> organizationEntityList = orgDao.getUserAffiliationsByType(userId, typeId, getDelegationFilter(requesterId), from, size);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }


    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationsDtoForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge) {
        List<OrganizationEntity> organizationEntityList = orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId), from, size);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size, final LanguageEntity langauge) {
        final boolean isUncoverParents = Boolean.TRUE.equals(searchBean.getUncoverParents());
        Set<String> filter = getDelegationFilter(requesterId, isUncoverParents);
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
    public List<Organization> findBeansDto(final OrganizationSearchBean searchBean, String requesterId, int from, int size, final LanguageEntity langauge) {
        final boolean isUncoverParents = Boolean.TRUE.equals(searchBean.getUncoverParents());
        Set<String> filter = getDelegationFilter(requesterId, isUncoverParents);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<Organization>(0);
        }
        List<OrganizationEntity> organizationEntityList = orgDao.getByExample(searchBean, from, size);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, searchBean.isDeepCopy());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getParentOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getParentOrganizationsDto(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        List<OrganizationEntity> organizationEntityList = orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId), from, size);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, false);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getChildOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        return orgDao.getChildOrganizations(orgId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Organization> getChildOrganizationsDto(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        List<OrganizationEntity> organizationEntityList = orgDao.getChildOrganizations(orgId, getDelegationFilter(requesterId), from, size);
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

    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfParentOrganizations(orgId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfChildOrganizations(orgId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional
    public void addUserToOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        OrganizationUserEntity organizationUserEntity = new OrganizationUserEntity();
        organizationUserEntity.setOrganization(organization);
        organizationUserEntity.setUser(user);
        user.getOrganizationUser().add(organizationUserEntity);
    }

    @Override
    @Transactional
    public void addUserToOrg(String orgId, String userId, String metadataTypeId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        final MetadataTypeEntity metadataTypeEntity = typeDAO.findById(metadataTypeId);
        OrganizationUserEntity organizationUserEntity = new OrganizationUserEntity();
        organizationUserEntity.setOrganization(organization);
        organizationUserEntity.setUser(user);
        organizationUserEntity.setMetadataTypeEntity(metadataTypeEntity);
        user.getOrganizationUser().add(organizationUserEntity);
    }

    @Override
    @Transactional
    public void removeUserFromOrg(String orgId, String userId) {
//        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        Iterator<OrganizationUserEntity> organizationUserEntityIterator = user.getOrganizationUser().iterator();
        while (organizationUserEntityIterator.hasNext()) {
            OrganizationUserEntity organizationUserEntity = organizationUserEntityIterator.next();
            if (organizationUserEntity.getOrganization() != null && organizationUserEntity.getOrganization().getId().equals(orgId)) {
                user.getOrganizationUser().remove(organizationUserEntity);
                break;
            }
        }
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
                if (preProcessor != null && preProcessor.save(organization, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
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
                if (StringUtils.isNotBlank(newEntity.getOrganizationType().getId())) {
                    curEntity.setOrganizationType(orgTypeDAO.findById(newEntity.getOrganizationType().getId()));
                }
                if (StringUtils.isNotBlank(newEntity.getType().getId())) {
                    curEntity.setType(typeDAO.findById(newEntity.getType().getId()));
                }

            } else {
                curEntity = orgDao.findById(organization.getId());
                mergeOrgProperties(curEntity, newEntity);
                mergeAttributes(curEntity, newEntity);
                mergeParents(curEntity, newEntity);
                mergeChildren(curEntity, newEntity);
                mergeUsers(curEntity, newEntity);
                mergeGroups(curEntity, newEntity);
                mergeLocations(curEntity, newEntity);
                mergeApproverAssociations(curEntity, newEntity);

                if (curEntity.getAdminResource() == null) {
                    curEntity.setAdminResource(getNewAdminResource(curEntity, requestorId));
                }

                curEntity.getAdminResource().setCoorelatedName(curEntity.getName());
                curEntity.setLstUpdate(Calendar.getInstance().getTime());
                curEntity.setLstUpdatedBy(requestorId);

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
            return org;

        } finally {
            if (StringUtils.isBlank(idmAuditLog.getResult())) {
                idmAuditLog.fail();
            }
            auditLogService.enqueue(idmAuditLog);
        }
    }

    @Override
    @Transactional
    public void addRequiredAttributes(OrganizationEntity organization) {
        if (organization != null && organization.getType() != null && StringUtils.isNotBlank(organization.getType().getId())) {
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(organization.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if (CollectionUtils.isNotEmpty(elementList)) {
                for (MetadataElementEntity element : elementList) {
                    if (element.isRequired()) {
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

    private void mergeLocations(final OrganizationEntity curEntity, final OrganizationEntity newEntity) {
        if (curEntity.getLocations() == null) {
            curEntity.setLocations(new HashSet<LocationEntity>());
        }
        if (newEntity != null && newEntity.getLocations() != null) {
            List<String> currIds = new ArrayList<String>();
            for (LocationEntity loc : curEntity.getLocations()) {
                currIds.add(loc.getLocationId());
            }
            final Set<LocationEntity> toAdd = new HashSet<LocationEntity>();
            final Set<LocationEntity> toRemove = new HashSet<LocationEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getLocations())) {
                Iterator<LocationEntity> iterator = newEntity.getLocations().iterator();
                while (iterator.hasNext()) {
                    LocationEntity nloc = iterator.next();
                    if (currIds.contains(nloc.getLocationId())) {
                        currIds.remove(nloc.getLocationId());
                        // location exists
                    } else {
                        // add
                        toAdd.add(locationDao.findById(nloc.getLocationId()));
                    }
                    //remove
                    for (LocationEntity cloc : curEntity.getLocations()) {
                        if (currIds.contains(cloc.getLocationId())) {
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
        if (curEntity.getOrganizationUser() == null) {
            curEntity.setOrganizationUser(new HashSet<OrganizationUserEntity>());
        }
        if (newEntity != null && newEntity.getOrganizationUser() != null) {
            List<String> currIds = new ArrayList<String>();
            for (OrganizationUserEntity cou : curEntity.getOrganizationUser()) {
                if (cou.getUser() != null) {
                    currIds.add(cou.getUser().getId());
                }
            }
            final Set<OrganizationUserEntity> toAdd = new HashSet<OrganizationUserEntity>();
            final Set<OrganizationUserEntity> toRemove = new HashSet<OrganizationUserEntity>();
            if (CollectionUtils.isNotEmpty(newEntity.getOrganizationUser())) {
                Iterator<OrganizationUserEntity> iterator = newEntity.getOrganizationUser().iterator();
                while (iterator.hasNext()) {
                    OrganizationUserEntity nou = iterator.next();
                    if (nou.getUser() != null && currIds.contains(nou.getUser().getId())) {
                        currIds.remove(nou.getUser().getId());
                        // user exists
                    } else if (nou.getUser() != null) {
                        // add
                        toAdd.add(nou);
                    }
                    //remove
                    for (OrganizationUserEntity cou : curEntity.getOrganizationUser()) {
                        if (cou.getUser() != null && currIds.contains(cou.getUser().getId())) {
                            toRemove.add(cou);
                            break;
                        }
                    }
                    curEntity.getOrganizationUser().removeAll(toRemove);
                    curEntity.getOrganizationUser().addAll(toAdd);
                }

            } else {
                curEntity.getOrganizationUser().clear();
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
                new String[]{"attributes", "parentOrganizations", "childOrganizations", "users", "approverAssociations",
                        "adminResource", "groups", "locations", "organizationType", "type", "lstUpdate", "lstUpdatedBy", "createDate", "createdBy"});
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
        if (bean != null && StringUtils.isNotBlank(bean.getId())) {
            return metadataElementDAO.findById(bean.getId());
        } else {
            return null;
        }
    }

    private void setMetadataTypeOnOrgAttribute(final OrganizationAttributeEntity bean) {
        if (bean.getElement() != null && bean.getElement().getId() != null) {
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
                if (preProcessor != null && preProcessor.delete(orgId, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                    idmAuditLog.setFailureReason(ResponseCode.FAIL_PREPROCESSOR);
                    throw new BasicDataServiceException(ResponseCode.FAIL_PREPROCESSOR);
                }
            }

            if (entity != null) {
                final GroupEntity example = new GroupEntity();
                example.addOrganization(entity);
                final List<GroupEntity> groups = groupDAO.getByExample(example);
                if (groups != null) {
                    for (final GroupEntity group : groups) {
                        group.removeOrganization(entity.getId());
                        groupDAO.update(group);
                    }
                }
                orgDao.delete(entity);
            }

            if (!skipPrePostProcessors) {
                OrganizationServicePrePostProcessor postProcessor = getPostProcessScript();
                if (postProcessor != null && postProcessor.delete(orgId, bindingMap, idmAuditLog) != OrganizationServicePrePostProcessor.SUCCESS) {
                    idmAuditLog.setFailureReason(ResponseCode.FAIL_POSTPROCESSOR);
                    throw new BasicDataServiceException(ResponseCode.FAIL_POSTPROCESSOR);
                }
            }

            idmAuditLog.succeed();

        } finally {
            if (StringUtils.isBlank(idmAuditLog.getResult())) {
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

    private Set<String> getDelegationFilter(String requesterId, boolean isUncoverParents) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, isUncoverParents);

        }
        return filterData;
    }

    private Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, boolean isUncoverParents) {
        Set<String> filterData = new HashSet<String>();
        if (attrMap != null && !attrMap.isEmpty()) {
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
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId, final LanguageEntity langauge) {
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
    public List<Organization> getAllowedParentOrganizationsDtoForType(final String orgTypeId, String requesterId, final LanguageEntity langauge) {
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

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap, boolean isUseOrgInhFlag) {
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

//    @Override
//    @Transactional(readOnly = true)
//    public OrganizationUserDTO getOrganizationUserDTOByOrganizationId(String orgId) {
//        return organizationUserDozerConverter.convertToDTO(this.findOrganizationUserEntitiesByOrganizationId(orgId), true);
//    }

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

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<Organization> findOrganizationsDtoByAttributeValue(final String attrName, String attrValue, final LanguageEntity langauge) {
        List<OrganizationEntity> organizationEntityList = orgDao.findOrganizationsByAttributeValue(attrName, attrValue);
        return organizationDozerConverter.convertToDTOList(organizationEntityList, true);
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

        for (final Org2OrgXrefEntity xref : xrefList) {
            final String orgId = xref.getId().getOrganizationId();
            final String memberOrgId = xref.getId().getMemberOrganizationId();

            if (!parentOrg2ChildOrgMap.containsKey(orgId)) {
                parentOrg2ChildOrgMap.put(orgId, new HashSet<String>());
            }

            child2ParentOrgMap.put(memberOrgId, orgId);
            parentOrg2ChildOrgMap.get(orgId).add(memberOrgId);
        }
        organizationTree = parentOrg2ChildOrgMap;
        organizationInvertedTree = child2ParentOrgMap;
    }

    private Set<String> getOrgTreeFlatList(final List<String> rootElementsIdList, boolean isUseOrgInhFlag, boolean isUncoverParents) {
        List<String> result = new ArrayList<String>();
        if (isUseOrgInhFlag) {
            if (CollectionUtils.isNotEmpty(rootElementsIdList)) {
                for (String rootElementId : rootElementsIdList) {
                    result.addAll(getOrgTreeFlatList(rootElementId));
                }
            }
        } else {
            result = new ArrayList<>(rootElementsIdList);
        }
        if (isUncoverParents) {
            for (String elementId : rootElementsIdList) {
                result.addAll(getParentsFlatList(elementId));
            }
        }
        return new HashSet<String>(result);
    }

    private List<String> getOrgTreeFlatList(String rootId) {
        List<String> result = new ArrayList<String>();
        if (StringUtils.isNotBlank(rootId)) {
            result.add(rootId);
            for (int i = 0; i < result.size(); i++) {
                String curElem = result.get(i);
                if (this.organizationTree.containsKey(curElem)) {
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
        fireUpdateOrgMap();
    }


    @Deprecated
    @Transactional(readOnly = true)
    public Organization getOrganizationDTO(final String orgId) {
        return this.getOrganizationDTO(orgId, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganization(String orgId) {
        return this.getOrganization(orgId, null);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganization(final String orgId, String requesterId) {
        return this.getOrganizationLocalized(orgId, requesterId, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganizationByName(final String name, String requesterId) {
        return this.getOrganizationByName(name, requesterId, getDefaultLanguage());
    }

    @Deprecated
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size) {
        return this.getOrganizationsForUser(userId, requesterId, from, size, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size) {
        return this.getParentOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size) {
        return this.getChildOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size) {
        return this.findBeans(searchBean, requesterId, from, size, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId) {
        return this.getAllowedParentOrganizationsForType(orgTypeId, requesterId, getDefaultLanguage());
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue) {
        return this.findOrganizationsByAttributeValue(attrName, attrValue, getDefaultLanguage());
    }

    private LanguageEntity getDefaultLanguage() {
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

        if (organization.getOrganizationType() == null || StringUtils.isBlank(organization.getOrganizationType().getId())) {
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
            entity.setIsActive(val.getIsActive());
            entity.setSensitiveLocation(val.getSensitiveLocation());

            locationDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void removeLocation(final String locationId) {
        final LocationEntity entity = locationDao.findById(locationId);

        if (entity != null) {
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
        if (locationId == null)
            throw new NullPointerException("locationId is null");
        LocationEntity locationEntity = locationDao.findById(locationId);
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

        return locationDao.count(locationSearchBeanConverter.convert(searchBean));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(String organizationId, boolean isDeep) {
        return locationDozerConverter.convertToDTOList(getLocationList(organizationId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(String organizationId, Integer from, Integer size) {
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
        if (organizationId == null)
            throw new NullPointerException("organizationId is null");

        LocationSearchBean searchBean = new LocationSearchBean();
        searchBean.setOrganizationId(organizationId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        List<LocationEntity> locationEntityList = getLocationList(searchBean, from, size);
        return locationDozerConverter.convertToDTOList(locationEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationEntity> getLocationList(LocationSearchBean searchBean, Integer from, Integer size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return locationDao.getByExample(locationSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> getLocationDtoList(LocationSearchBean searchBean, Integer from, Integer size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        List<LocationEntity> locationEntityList = locationDao.getByExample(locationSearchBeanConverter.convert(searchBean), from, size);
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
        List<OrganizationEntity> orgList = orgDao.getOrganizationsForUser(userId, null, 0, Integer.MAX_VALUE);
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

}
