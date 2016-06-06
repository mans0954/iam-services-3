package org.openiam.idm.srvc.mngsys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.OrderConstants;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SortParam;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.dozer.converter.MngSysPolicyDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.searchbeans.MngSysPolicySearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.bean.AttributeMapBean;
import org.openiam.idm.srvc.mngsys.bean.MngSysPolicyBean;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.openiam.idm.srvc.mngsys.domain.ReconciliationResourceAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManagedSystemServiceImpl implements ManagedSystemService {
    private static final Log log = LogFactory
            .getLog(ManagedSystemServiceImpl.class);
    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Autowired
    private MngSysPolicyDAO mngSysPolicyDAO;

    @Autowired
    private MngSysPolicyDozerConverter mngSysPolicyDozerConverter;

    @Autowired
    protected AttributeMapDAO attributeMapDAO;

    @Autowired
    private AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    protected ReconciliationResourceAttributeMapDAO reconciliationResourceAttributeMapDAO;

    @Autowired
    protected DefaultReconciliationAttributeMapDAO defaultReconciliationAttributeMapDAO;

    @Autowired
    protected PolicyDAO policyDAO;

    @Autowired
    private ManagedSystemObjectMatchDAO matchDAO;

    @Autowired
    private ResourceTypeDAO resourceTypeDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;

    @Autowired
    private GroupDAO groupDAO;

    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    private AuthProviderDao authProviderDao;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private ApproverAssociationDAO approverAssociationDao;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MetadataElementDAO elementDAO;

    @Autowired
    private MetadataTypeDAO metadataTypeDAO;

    private static final String resourceTypeId = "MANAGED_SYS";

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private KeyManagementService keyManagementService;
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysDto> getManagedSystemsByExample(final ManagedSysSearchBean searchBean, int from, int size) {
        List<ManagedSysEntity> sysEntities = managedSysDAO.getByExample(searchBean, from, size);
        List<ManagedSysDto> managedSysDtos = managedSysDozerConverter.convertToDTOList(sysEntities, (searchBean != null) ? searchBean.isDeepCopy() : false);
        return managedSysDtos;
    }


    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysById(String id) {
        return managedSysDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MngSysPolicyEntity getManagedSysPolicyById(String id) {
        return mngSysPolicyDAO.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getManagedSysByConnectorId(String connectorId) {
        ManagedSysSearchBean searchBean = getDefaultSearchBean();
        searchBean.setConnectorId(connectorId);
        return managedSysDAO.getByExample(searchBean);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ManagedSysEntity> getManagedSysByDomain(String domainId) {
//        return managedSysDAO.findbyDomain(domainId);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysEntity> getAllManagedSys() {
        ManagedSysSearchBean searchBean = getDefaultSearchBean();
        return managedSysDAO.getByExample(searchBean);
    }

    @Override
    @Transactional
    public void removeManagedSysById(String id) throws BasicDataServiceException {
        final ManagedSysEntity sysEntity = managedSysDAO.findById(id);
        if (sysEntity != null) {
            if (CollectionUtils.isNotEmpty(sysEntity.getAuthProviders())) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER, sysEntity.getAuthProviders().iterator().next().getName());
            }

        	/*
            if(CollectionUtils.isNotEmpty(sysEntity.getContentProviders())) {
        		throw new BasicDataServiceException(ResponseCode.LINKED_TO_ONE_OR_MORE_CONTENT_PROVIDERS);
        	}
        	*/

            for (final ManagedSystemObjectMatchEntity matchEntity : sysEntity.getMngSysObjectMatchs()) {
                matchDAO.delete(matchEntity);
            }

            if (CollectionUtils.isNotEmpty(sysEntity.getGroups())) {
                for (final GroupEntity group : sysEntity.getGroups()) {
                    group.setManagedSystem(null);
                    groupDAO.update(group);
                }
            }

            if (CollectionUtils.isNotEmpty(sysEntity.getRoles())) {
                for (final RoleEntity role : sysEntity.getRoles()) {
                    role.setManagedSystem(null);
                    roleDAO.update(role);
                }
            }

            managedSysDAO.delete(sysEntity);
            if (sysEntity.getResource() != null) {
                resourceService.deleteResource(sysEntity.getResource().getId());
            }
        }
    }

    @Override
    @Transactional
    public void saveManagedSysObjectMatch(final ManagedSystemObjectMatchEntity entity) {
    	if(entity != null && entity.getManagedSys() != null && StringUtils.isNotBlank(entity.getManagedSys().getId())) {
    		final ManagedSysEntity managedSys = managedSysDAO.findById(entity.getManagedSys().getId());
    		if(managedSys != null) {
    			entity.setManagedSys(managedSys);
    			matchDAO.save(entity);
    		}
    	}
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysByResource(String id, String status) {
        return managedSysDAO.findByResource(id, status);
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysEntity getManagedSysByName(String name) {
        return managedSysDAO.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeMapEntity getAttributeMap(String attributeMapId) {
        return attributeMapDAO.findById(attributeMapId);

    }

    @Override
    public AttributeMapEntity addAttributeMap(AttributeMapEntity attributeMap) {

        attributeMap.setReconResAttribute(this
                .saveReconResAttributeMap(attributeMap.getReconResAttribute()));
        return attributeMapDAO.add(attributeMap);
    }

    @Override
    public void updateAttributeMap(AttributeMapEntity attributeMap) {
        attributeMap.setReconResAttribute(this
                .saveReconResAttributeMap(attributeMap.getReconResAttribute()));
        attributeMapDAO.update(attributeMap);
    }

    @Override
    @Transactional
    public void removeAttributeMap(String attributeMapId) {
        AttributeMapEntity amE = attributeMapDAO.findById(attributeMapId);
        if (amE.getReconResAttribute() != null) {
            reconciliationResourceAttributeMapDAO.delete(amE
                    .getReconResAttribute());
            amE.setReconResAttribute(null);
        }
        if (amE != null)
            attributeMapDAO.delete(amE);
    }

    @Override
    @Transactional
    public void removeMngSysPolicy(String mngSysPolicyId) throws BasicDataServiceException {
        MngSysPolicyEntity entity = mngSysPolicyDAO.findById(mngSysPolicyId);
        if (entity == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        mngSysPolicyDAO.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getResourceAttributeMaps(String resourceId) {
        return attributeMapDAO.findByResourceId(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMap> getAttributeMapsByMngSysPolicyId(String mngSysPolicyId) {
        List<AttributeMapEntity> attributeMapEntities = attributeMapDAO.findByMngSysPolicyId(mngSysPolicyId);
        return attributeMapDozerConverter.convertToDTOList(attributeMapEntities,true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getResourceAttributeMaps(AttributeMapSearchBean searchBean) {
        return attributeMapDAO.getByExample(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getAllAttributeMaps() {
        return attributeMapDAO.findAllAttributeMaps();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefaultReconciliationAttributeMapEntity> getAllDefaultReconAttributeMap() {
        return defaultReconciliationAttributeMapDAO.getAll();
    }

    private ReconciliationResourceAttributeMapEntity saveReconResAttributeMap(
            ReconciliationResourceAttributeMapEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getAttributePolicy() != null) {
            entity.setAttributePolicy(policyDAO.findById(entity
                    .getAttributePolicy().getId()));
            entity.setDefaultAttributePolicy(null);
        } else if (entity.getDefaultAttributePolicy() != null) {
            entity.setDefaultAttributePolicy(defaultReconciliationAttributeMapDAO
                    .findById(entity.getDefaultAttributePolicy()
                            .getId()));
            entity.setAttributePolicy(null);
        }
        if (entity.getId() == null) {
            return reconciliationResourceAttributeMapDAO.add(entity);
        } else {
            reconciliationResourceAttributeMapDAO.update(entity);
        }

        return entity;
    }

    @Override
    @Transactional
    public List<AttributeMapEntity> saveAttributesMap(
            List<AttributeMapEntity> attrMap, String mSysPolicyId, String resId,
            String synchConfigId) throws Exception {

        if (attrMap == null) {
            return null;
        }

        MngSysPolicyEntity mngSysPolicy = getManagedSysPolicyById(mSysPolicyId);
        Map<String, AttributeMapEntity> curAttrMapsMap = new HashMap<String, AttributeMapEntity>();
        List<AttributeMapEntity> curAttrMaps =  attributeMapDAO.findByMngSysPolicyId(mSysPolicyId);
        for (AttributeMapEntity ame : curAttrMaps) {
            curAttrMapsMap.put(ame.getId(), ame);
        }
        for (AttributeMapEntity ame : attrMap) {
            ReconciliationResourceAttributeMapEntity rram = ame.getReconResAttribute();
            if (rram.getAttributePolicy() != null) {
                PolicyEntity policy = policyDAO.findById(rram.getAttributePolicy().getId());
                rram.setAttributePolicy(policy);
            } else if (rram.getDefaultAttributePolicy() != null) {
                DefaultReconciliationAttributeMapEntity drame = defaultReconciliationAttributeMapDAO.findById(rram.getDefaultAttributePolicy().getId());
                rram.setDefaultAttributePolicy(drame);
            } else {
                throw new BasicDataServiceException(ResponseCode.VALUE_REQUIRED);
            }
            ame.setReconResAttribute(rram);
            ame.setMngSysPolicy(mngSysPolicy);
            ame.setResourceId(resId);
            ame.setSynchConfigId(synchConfigId);
            if (StringUtils.isNotBlank(ame.getId())) {
                AttributeMapEntity attrMapEntity = curAttrMapsMap.get(ame.getId());
                BeanUtils.copyProperties(ame, attrMapEntity, new String[]{"reconResAttribute"});
                attrMapEntity.getReconResAttribute().setAttributePolicy(ame.getReconResAttribute().getAttributePolicy());
                attrMapEntity.getReconResAttribute().setDefaultAttributePolicy(ame.getReconResAttribute().getDefaultAttributePolicy());
            } else {
                this.addAttributeMap(ame);
            }

        }
        return new ArrayList<AttributeMapEntity>(attrMap);

    }

    @Override
    @Transactional
    public void deleteAttributesMapList(List<String> ids) throws Exception {
        attributeMapDAO.delete(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSystemObjectMatchEntity> managedSysObjectParam(
            String managedSystemId, String objectType) {
        return matchDAO.findBySystemId(managedSystemId, objectType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProviderEntity> findAuthProvidersByManagedSysId(String managedSysId) {
        return authProviderDao.getByManagedSysId(managedSysId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MngSysPolicyDto> getManagedSysPolicyByMngSysId(String mngSysId) {
        List<MngSysPolicyEntity> policyDtos = mngSysPolicyDAO.findByMngSysId(mngSysId);
        return mngSysPolicyDozerConverter.convertToDTOList(policyDtos, false);
    }

    @Override
    @Transactional(readOnly = true)
    public MngSysPolicyDto getManagedSysPolicyByMngSysIdAndMetadataType(String mngSysId, String metadataTypeId) {
        MngSysPolicyEntity mngSysPolicyEntity = mngSysPolicyDAO.findPrimaryByMngSysIdAndType(mngSysId, metadataTypeId);
        return mngSysPolicyDozerConverter.convertToDTO(mngSysPolicyEntity, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMapEntity> getAttributeMapsByManagedSysId(String managedSysId) {
        return attributeMapDAO.findByManagedSysId(managedSysId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MngSysPolicyDto> findMngSysPolicies(MngSysPolicySearchBean searchBean, Integer from, Integer size) {
        List<MngSysPolicyEntity> entities = mngSysPolicyDAO.getByExample(searchBean, from, size);
        return mngSysPolicyDozerConverter.convertToDTOList(entities, searchBean.isDeepCopy());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MngSysPolicyBean> findMngSysPolicyBeans(MngSysPolicySearchBean searchBean, Integer from, Integer size) {
        List<MngSysPolicyBean> ret = new ArrayList<>();
        List<MngSysPolicyDto> policies = findMngSysPolicies(searchBean, from, size);
        if (CollectionUtils.isNotEmpty(policies)) {
            for (MngSysPolicyDto policy : policies) {
                ret.add(new MngSysPolicyBean(policy));
            }
        }
        return ret;
    }

    @Override
    @Transactional
    public String saveMngSysPolicyBean(MngSysPolicyBean mngSysPolicy) throws BasicDataServiceException {
        if (mngSysPolicy != null) {
            Date curDate = new Date();
            MngSysPolicyEntity entity = null;
            if (StringUtils.isNotEmpty(mngSysPolicy.getId())) {
                entity = mngSysPolicyDAO.findById(mngSysPolicy.getId());
                if (entity == null) {
                    throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
                }
            } else {
                entity = new MngSysPolicyEntity();
                entity.setCreateDate(curDate);
            }
            ManagedSysEntity mSys = null;
            if (StringUtils.isNotEmpty(mngSysPolicy.getManagedSysId())) {
                mSys = managedSysDAO.findById(mngSysPolicy.getManagedSysId());
            }
            if (mSys == null) {
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
            }
            MetadataTypeEntity mdType = null;
            if(StringUtils.isNotEmpty(mngSysPolicy.getMdTypeId())) {
                mdType = metadataTypeDAO.findById(mngSysPolicy.getMdTypeId());
            }
            if (mdType == null) {
                throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
            }
            entity.setName(mngSysPolicy.getName());
            entity.setManagedSystem(mSys);
            if (mngSysPolicy.isPrimary()) {
                //TODO: make all other policies of same type for managed sys not primary
            }
            entity.setPrimary(mngSysPolicy.isPrimary());
            entity.setType(mdType);
            entity.setLastUpdate(curDate);
            if (CollectionUtils.isNotEmpty(mngSysPolicy.getAttrMaps())) {
                for (AttributeMapBean attrBean : mngSysPolicy.getAttrMaps()) {
                    List<AttributeMapEntity> toDelete = new ArrayList<>();
                    if (attrBean.getOperation()!=null && !AttributeOperationEnum.NO_CHANGE.equals(attrBean.getOperation())) {
                        AttributeMapEntity attrEntity = null;
                        if (StringUtils.isNotEmpty(attrBean.getId()) && AttributeOperationEnum.REPLACE.equals(attrBean.getOperation())) {
                            attrEntity = findAttributeMapEntityById(entity, attrBean.getId());
                        } else if (AttributeOperationEnum.ADD.equals(attrBean.getOperation())) {
                            attrEntity = new AttributeMapEntity();
                            attrEntity.setMngSysPolicy(entity);
                            entity.getAttributeMaps().add(attrEntity);
                        } else if (StringUtils.isNotEmpty(attrBean.getId()) && AttributeOperationEnum.DELETE.equals(attrBean.getOperation())) {
                            AttributeMapEntity del = findAttributeMapEntityById(entity, attrBean.getId());
                            if (del != null) {
                                toDelete.add(del);
                            }
                        }
                        if (attrEntity != null) {
                            attrEntity.setMapForObjectType(attrBean.getObjectType());
                            attrEntity.setName(attrBean.getAttributeName());

                            ReconciliationResourceAttributeMapEntity reconAttr = attrEntity.getReconResAttribute();
                            if (reconAttr == null) {
                                reconAttr = new ReconciliationResourceAttributeMapEntity();
                                reconAttr.setAttributeMap(attrEntity);
                                attrEntity.setReconResAttribute(reconAttr);
                            }
                            if ("DEFAULT_IDM".equals(attrBean.getPolicyType())) {
                                if (reconAttr.getDefaultAttributePolicy() == null ||
                                        !StringUtils.equals(reconAttr.getDefaultAttributePolicy().getId(),attrBean.getDefaultAttributePolicyId())) {
                                    DefaultReconciliationAttributeMapEntity defRecon = defaultReconciliationAttributeMapDAO.findById(attrBean.getDefaultAttributePolicyId());
                                    reconAttr.setDefaultAttributePolicy(defRecon);
                                    reconAttr.setAttributePolicy(null);
                                }
                            } else if ("POLICY".equals(attrBean.getPolicyType())) {
                                if (reconAttr.getAttributePolicy() == null ||
                                        !StringUtils.equals(reconAttr.getAttributePolicy().getId(),attrBean.getAttributePolicyId())) {
                                    PolicyEntity policy = policyDAO.findById(attrBean.getAttributePolicyId());
                                    reconAttr.setAttributePolicy(policy);
                                    reconAttr.setDefaultAttributePolicy(null);
                                }
                            }
                            attrEntity.setDataType(attrBean.getDataType());
                            attrEntity.setDefaultValue(attrBean.getDefaultValue());
                            attrEntity.setStatus(attrBean.getStatus());
                        }
                        entity.getAttributeMaps().removeAll(toDelete);
                    }
                }
            }

            mngSysPolicyDAO.save(entity);
            return entity.getId();

        } else {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
    }

    private AttributeMapEntity findAttributeMapEntityById(final MngSysPolicyEntity mngSysPolicyEntity, String id) {
        final Optional<AttributeMapEntity> entity = mngSysPolicyEntity.getAttributeMaps().stream().filter(e-> id.equals(e.getId())).findFirst();
        return entity.isPresent() ? entity.get() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public int getMngSysPoliciesCount(MngSysPolicySearchBean searchBean) {
        return mngSysPolicyDAO.count(searchBean);
    }

    @Cacheable(value = "decryptManagedSysPassword", key = "{ #managedSys.id}")
    public String getDecryptedPassword(ManagedSysDto managedSys) {
        String result = null;
        if (managedSys.getPswd() != null) {
            try {
                result = cryptor.decrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), managedSys.getPswd());
            } catch (Exception e) {
                log.error(e);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void saveApproverAssociations(List<ApproverAssociationEntity> entityList, final AssociationType type, final String entityId) {
        //final List<ApproverAssociationEntity> newList = new LinkedList<ApproverAssociationEntity>();
        //final List<ApproverAssociationEntity> updateList = new LinkedList<ApproverAssociationEntity>();
        final List<ApproverAssociationEntity> deleteList = new LinkedList<ApproverAssociationEntity>();

        if (type != null && StringUtils.isNotBlank(entityId)) {
            final List<ApproverAssociationEntity> existingList = approverAssociationDao.getByAssociation(entityId, type);
            if (CollectionUtils.isNotEmpty(existingList)) {
                for (final ApproverAssociationEntity existingEntity : existingList) {
                    boolean contains = false;
                    if (CollectionUtils.isNotEmpty(entityList)) {
                        for (final ApproverAssociationEntity incomingEntity : entityList) {
                            if (StringUtils.equals(existingEntity.getId(), incomingEntity.getId())) {
                                contains = true;
                                break;
                            }
                        }
                    }

                    if (!contains) {
                        deleteList.add(existingEntity);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(entityList)) {
            for (final ApproverAssociationEntity entity : entityList) {
                if (StringUtils.isNotBlank(entity.getId())) {
                    approverAssociationDao.merge(entity);
                } else {
                    approverAssociationDao.save(entity);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(deleteList)) {
            for (final ApproverAssociationEntity entity : deleteList) {
                approverAssociationDao.delete(entity);
            }
        }
    }

    private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
        if (bean != null && StringUtils.isNotBlank(bean.getId())) {
            return elementDAO.findById(bean.getId());
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly=true)
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("managedSysObjectParam"),
                @CacheKeyEvict("decryptManagedSysPassword"),
                @CacheKeyEvict("managedSysAttributeMaps")
            }
        )
    public void save(final ManagedSysEntity entity) throws BasicDataServiceException {
        if (entity.getResource() == null || StringUtils.isBlank(entity.getResource().getId())) {
            final ResourceEntity resource = new ResourceEntity();
            resource.setName(String.format("%s_%S", entity.getName(), System.currentTimeMillis()));
            resource.setResourceType(resourceTypeDAO.findById(resourceTypeId));
            resource.setIsPublic(false);
            resource.setCoorelatedName(entity.getName());
            if(entity.getResource() != null) {
            	resource.setResourceProps(entity.getResource().getResourceProps());
            }
            resourceService.save(resource, null);
            entity.setResource(resource);
        } else {
            final ResourceEntity resource = resourceService.findResourceById(entity.getResource().getId());
            if (CollectionUtils.isEmpty(entity.getResource().getResourceProps())) {
                resource.getResourceProps().clear();
            } else {
                //update existing
                for (final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
                    for (final ResourcePropEntity persistentAttribute : resource.getResourceProps()) {
                        if (StringUtils.equals(transientAttribute.getId(), persistentAttribute.getId())) {
                            persistentAttribute.setElement(getEntity(transientAttribute.getElement()));
                            persistentAttribute.setIsMultivalued(transientAttribute.getIsMultivalued());
                            persistentAttribute.setName(transientAttribute.getName());
                            persistentAttribute.setValue(transientAttribute.getValue());
                            break;
                        }
                    }
                }

                //add  new
                for (final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
                    if (StringUtils.isBlank(transientAttribute.getId())) {
                        transientAttribute.setElement(getEntity(transientAttribute.getElement()));
                        transientAttribute.setResource(resource);
                        resource.getResourceProps().add(transientAttribute);
                    }
                }

                //delete
                for (final Iterator<ResourcePropEntity> persistentIterator = resource.getResourceProps().iterator(); persistentIterator.hasNext(); ) {
                    boolean contains = false;
                    final ResourcePropEntity persistentAttribute = persistentIterator.next();
                    for (final ResourcePropEntity transientAttribute : entity.getResource().getResourceProps()) {
                        if (StringUtils.equals(transientAttribute.getId(), persistentAttribute.getId())) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        persistentIterator.remove();
                    }
                }
            }
            //resource.setResourceProps(entity.getResource().getResourceProps());
            resource.setCoorelatedName(entity.getName());
            //resourceService.save(resource, null);
            entity.setResource(resource);
            /*
            resourceService.save(resource, null);
			entity.setResource(resource);
			*/
        }

        if (StringUtils.isBlank(entity.getId())) {
            managedSysDAO.save(entity);
        } else {
            managedSysDAO.merge(entity);
        }
    }

	@Override
	public int count(ManagedSysSearchBean searchBean) {
		return managedSysDAO.count(searchBean);
	}

    private ManagedSysSearchBean getDefaultSearchBean(){
        ManagedSysSearchBean searchBean = new ManagedSysSearchBean();
        addDefaultSortParam(searchBean);
        return searchBean;
    }

    private void addDefaultSortParam(ManagedSysSearchBean searchBean){
        SortParam sort = new SortParam();
        sort.setSortBy("name");
        sort.setOrderBy(OrderConstants.ASC);
        searchBean.addSortParam(sort);
    }
}
