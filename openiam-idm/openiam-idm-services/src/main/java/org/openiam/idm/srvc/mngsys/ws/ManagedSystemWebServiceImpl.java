package org.openiam.idm.srvc.mngsys.ws;

import java.util.LinkedList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.ApproverAssociationDozerConverter;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.DefaultReconciliationAttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSysRuleDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.DefaultReconciliationAttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysRuleDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ApproverAssocationSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ManagedSystemSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("managedSysService")
@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService", targetNamespace = "urn:idm.openiam.org/srvc/mngsys/service", portName = "ManagedSystemWebServicePort", serviceName = "ManagedSystemWebService")
public class ManagedSystemWebServiceImpl implements ManagedSystemWebService {

    @Autowired
    private ManagedSystemService managedSystemService;

    @Autowired
    private ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    @Autowired
    private ApproverAssociationDAO approverAssociationDao;

    @Autowired
    private UserDataService userManager;

    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    private ManagedSysRuleDozerConverter managedSysRuleDozerConverter;
    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    private ManagedSystemSearchBeanConverter managedSystemSearchBeanConverter;

    @Autowired
    private ApproverAssociationDozerConverter approverAssociationDozerConverter;
    @Autowired
    private DefaultReconciliationAttributeMapDozerConverter defaultReconciliationAttributeMapDozerConverter;
    private static final Log log = LogFactory
            .getLog(ManagedSystemWebServiceImpl.class);

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private ApproverAssocationSearchBeanConverter approverSearchBeanConverter;

    boolean encrypt = true; // default encryption setting

    @Override
    public Integer getManagedSystemsCount(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean) {
        ManagedSysEntity managedSysEntity = managedSystemSearchBeanConverter
                .convert(searchBean);
        return managedSystemService
                .getManagedSystemsCountByExample(managedSysEntity);
    }

    @Override
    public List<ManagedSysDto> getManagedSystems(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "from", targetNamespace = "") Integer from) {
        List<ManagedSysDto> managedSysDtos = new LinkedList<ManagedSysDto>();
        ManagedSysEntity managedSysEntity = managedSystemSearchBeanConverter
                .convert(searchBean);
        List<ManagedSysEntity> sysEntities = managedSystemService
                .getManagedSystemsByExample(managedSysEntity, size, from);
        if (sysEntities != null) {
            managedSysDtos = managedSysDozerConverter.convertToDTOList(
                    sysEntities, false);
        }
        return managedSysDtos;
    }

    @Override
    public ManagedSysDto addManagedSystem(ManagedSysDto sys) {

        if (sys == null) {
            throw new NullPointerException("sys is null");
        }

        if (encrypt && sys.getPswd() != null) {
            try {
                sys.setPswd(cryptor.encrypt(null, sys.getPswd()));
            } catch (Exception e) {
                log.error(e);
            }
        }

        ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys,
                true);
        managedSystemService.addManagedSys(entity);

        return managedSysDozerConverter.convertToDTO(entity, true);
    }

    @Override
    public ManagedSysDto getManagedSys(String sysId) {
        if (sysId == null) {
            throw new NullPointerException("sysId is null");
        }

        ManagedSysEntity sys = managedSystemService.getManagedSysById(sysId);
        ManagedSysDto sysDto = null;
        if (sys != null) {
            sysDto = managedSysDozerConverter.convertToDTO(sys, true);
            if (sysDto != null && sysDto.getPswd() != null) {
                try {
                    sysDto.setDecryptPassword(cryptor.decrypt(
                            keyManagementService.getUserKey(sys.getUserId(),
                                    KeyName.password.name()), sys.getPswd()));
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }
        return sysDto;
    }

    @Override
    public ManagedSysDto[] getManagedSysByProvider(String providerId) {
        if (providerId == null) {
            throw new NullPointerException("providerId is null");
        }
        List<ManagedSysEntity> sysList = managedSystemService
                .getManagedSysByConnectorId(providerId);
        if (sysList == null) {
            return null;
        }
        int size = sysList.size();
        ManagedSysDto[] sysAry = new ManagedSysDto[size];
        managedSysDozerConverter.convertToDTOList(sysList, true)
                .toArray(sysAry);
        return sysAry;
    }

    /**
     * Returns an array of ManagedSys object for a security domain.
     * 
     * @param domainId
     * @return
     */
    @Override
    public ManagedSysDto[] getManagedSysByDomain(String domainId) {
        if (domainId == null) {
            throw new NullPointerException("domainId is null");
        }
        List<ManagedSysEntity> sysList = managedSystemService
                .getManagedSysByDomain(domainId);
        if (sysList == null) {
            return null;
        }
        int size = sysList.size();
        ManagedSysDto[] sysAry = new ManagedSysDto[size];
        managedSysDozerConverter.convertToDTOList(sysList, true)
                .toArray(sysAry);
        return sysAry;

    }

    public ManagedSysDto[] getAllManagedSys() {
        List<ManagedSysEntity> sysList = managedSystemService
                .getAllManagedSys();
        if (sysList == null) {
            return null;
        }
        int size = sysList.size();
        ManagedSysDto[] sysAry = new ManagedSysDto[size];
        managedSysDozerConverter.convertToDTOList(sysList, true)
                .toArray(sysAry);
        return sysAry;
    }

    public void removeManagedSystem(String sysId) {
        if (sysId == null) {
            throw new NullPointerException("sysId is null");
        }

        managedSystemService.removeManagedSysById(sysId);
    }

    public void updateManagedSystem(ManagedSysDto sys) {
        if (sys == null) {
            throw new NullPointerException("sys is null");
        }
        if (encrypt && sys.getPswd() != null) {
            try {
                sys.setPswd(cryptor.encrypt(keyManagementService.getUserKey(
                        sys.getUserId(), KeyName.password.name()), sys
                        .getPswd()));
            } catch (Exception e) {
                log.error(e);
            }
        }
        ManagedSysEntity managedSysEntity = managedSysDozerConverter
                .convertToEntity(sys, true);
        managedSystemService.updateManagedSys(managedSysEntity);
    }

    /**
     * Finds objects for an object type (like User, Group) for a ManagedSystem
     * definition
     * 
     * @param managedSystemId
     * @param objectType
     * @return
     */
    public ManagedSystemObjectMatch[] managedSysObjectParam(
            String managedSystemId, String objectType) {
        if (managedSystemId == null) {
            throw new NullPointerException("managedSystemId is null");
        }
        if (objectType == null) {
            throw new NullPointerException("objectType is null");
        }
        List<ManagedSystemObjectMatchEntity> objList = managedSystemService
                .managedSysObjectParam(managedSystemId, objectType);
        if (objList == null) {
            return null;
        }
        int size = objList.size();
        ManagedSystemObjectMatch[] objAry = new ManagedSystemObjectMatch[size];
        managedSystemObjectMatchDozerConverter.convertToDTOList(objList, false)
                .toArray(objAry);
        return objAry;
    }

    public ManagedSysDto getManagedSysByResource(String resourceId) {
        if (resourceId == null) {
            throw new NullPointerException("resourceId is null");
        }
        ManagedSysEntity sys = managedSystemService.getManagedSysByResource(
                resourceId, "ACTIVE");
        ManagedSysDto sysDto = null;
        if (sys != null) {
            sysDto = managedSysDozerConverter.convertToDTO(sys, true);
            if (sysDto != null && sysDto.getPswd() != null) {
                try {
                    sysDto.setDecryptPassword(cryptor.decrypt(
                            keyManagementService.getUserKey(sys.getUserId(),
                                    KeyName.password.name()), sys.getPswd()));
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }

        return sysDto;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService#getApproversByAction
     * (java.lang.String, java.lang.String, int)
     */
    /*
     * public List<ApproverAssociation> getApproversByAction(String
     * managedSysId, String action, int level) { if ( managedSysId == null) {
     * throw new NullPointerException("managedSysId is null"); } return
     * resourceApproverDao.findApproversByAction(managedSysId, action, level); }
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService#getApproversByResource
     * (java.lang.String)
     */
    /*
     * public List<ApproverAssociation> getApproversByResource(String
     * managedSysId) { if ( managedSysId == null) { throw new
     * NullPointerException("managedSysId is null"); } return
     * resourceApproverDao.findApproversByResource(managedSysId); }
     */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService#getManagedSysByName
     * (java.lang.String)
     */
    public ManagedSysDto getManagedSysByName(String name) {
        if (name == null) {
            throw new NullPointerException(
                    "Parameter Managed system name is null");
        }
        return managedSysDozerConverter.convertToDTO(
                managedSystemService.getManagedSysByName(name), true);
    }

    // Approver Association
    // ================================================================

    @Override
    public List<ApproverAssociation> getApproverAssociations(
            ApproverAssocationSearchBean searchBean, int from, int size) {
        final ApproverAssociationEntity entity = approverSearchBeanConverter
                .convert(searchBean);
        final List<ApproverAssociationEntity> entityList = approverAssociationDao
                .getByExample(entity, from, size);
        return (entityList != null) ? approverAssociationDozerConverter
                .convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
    }

    public Response saveApproverAssociation(
            final ApproverAssociation approverAssociation) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (approverAssociation == null) {
                throw new BasicDataServiceException(
                        ResponseCode.OBJECT_NOT_FOUND);
            }

            if (StringUtils.isBlank(approverAssociation.getApproverEntityId())
                    || approverAssociation.getApproverEntityType() == null) {
                approverAssociation.setApproverEntityId(null);
                approverAssociation.setApproverEntityType(null);
            }

            if (StringUtils.isBlank(approverAssociation.getOnApproveEntityId())
                    || approverAssociation.getOnApproveEntityType() == null) {
                approverAssociation.setOnApproveEntityId(null);
                approverAssociation.setOnApproveEntityType(null);
            }

            if (StringUtils.isBlank(approverAssociation.getOnRejectEntityId())
                    || approverAssociation.getOnRejectEntityType() == null) {
                approverAssociation.setOnRejectEntityId(null);
                approverAssociation.setOnRejectEntityType(null);
            }

            if (StringUtils.isBlank(approverAssociation
                    .getAssociationEntityId())
                    || approverAssociation.getAssociationType() == null) {
                approverAssociation.setAssociationEntityId(null);
                approverAssociation.setAssociationType(null);
            }

            if (approverAssociation.getApproverEntityType() == null
                    || StringUtils.isBlank(approverAssociation
                            .getApproverEntityId())) {
                throw new BasicDataServiceException(
                        ResponseCode.REQUEST_APPROVERS_NOT_SET);
            }

            final ApproverAssociationEntity entity = approverAssociationDozerConverter
                    .convertToEntity(approverAssociation, true);
            if (StringUtils.isNotBlank(entity.getId())) {
                approverAssociationDao.merge(entity);
            } else {
                approverAssociationDao.save(entity);
            }
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error(e);
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    public ApproverAssociation getApproverAssociation(
            String approverAssociationId) {
        final ApproverAssociationEntity entity = approverAssociationDao
                .findById(approverAssociationId);
        return (entity != null) ? approverAssociationDozerConverter
                .convertToDTO(entity, true) : null;
    }

    public Response removeApproverAssociation(String approverAssociationId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (approverAssociationId == null) {
                throw new BasicDataServiceException(
                        ResponseCode.OBJECT_NOT_FOUND);
            }

            final ApproverAssociationEntity entity = approverAssociationDao
                    .findById(approverAssociationId);
            if (entity != null) {
                approverAssociationDao.delete(entity);
            }
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error(e);
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService#
     * addManagedSystemObjectMatch
     * (org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch)
     */
    @Override
    public void addManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
        managedSysObjectMatchDao.save(managedSystemObjectMatchDozerConverter
                .convertToEntity(obj, false));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService#
     * updateManagedSystemObjectMatch
     * (org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch)
     */
    public void updateManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
        this.managedSysObjectMatchDao
                .merge(managedSystemObjectMatchDozerConverter.convertToEntity(
                        obj, false));

    }

    public void removeManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
        this.managedSysObjectMatchDao
                .delete(managedSystemObjectMatchDozerConverter.convertToEntity(
                        obj, false));
    }

    public AttributeMap getAttributeMap(String attributeMapId) {
        if (attributeMapId == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        AttributeMapEntity obj = managedSystemService
                .getAttributeMap(attributeMapId);
        return obj == null ? null : attributeMapDozerConverter.convertToDTO(
                obj, true);
    }

    public AttributeMap addAttributeMap(AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new IllegalArgumentException("AttributeMap object is null");
        }
        AttributeMapEntity entity = attributeMapDozerConverter.convertToEntity(
                attributeMap, true);
        return attributeMapDozerConverter.convertToDTO(
                managedSystemService.addAttributeMap(entity), true);
    }

    @Override
    public void deleteAttributesMapList(List<String> ids) throws Exception {
        managedSystemService.deleteAttributesMapList(ids);
    }

    @Override
    public List<AttributeMap> saveAttributesMap(List<AttributeMap> attrMap,
            String mSysId, String resId, String synchConfigId) throws Exception {
        if (CollectionUtils.isEmpty(attrMap)
                && (StringUtils.isEmpty(resId) || StringUtils.isEmpty(mSysId))
                && StringUtils.isEmpty(synchConfigId))
            return null;
        List<AttributeMapEntity> res = managedSystemService.saveAttributesMap(
                attributeMapDozerConverter.convertToEntityList(attrMap, true),
                mSysId, resId, synchConfigId);
        if (res == null)
            return null;
        else
            return attributeMapDozerConverter.convertToDTOList(res, true);
    }

    public AttributeMap updateAttributeMap(AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new IllegalArgumentException("attributeMap object is null");
        }
        managedSystemService.updateAttributeMap(attributeMapDozerConverter
                .convertToEntity(attributeMap, true));
        return attributeMap;
    }

    public void removeAttributeMap(String attributeMapId) {
        if (attributeMapId == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        managedSystemService.removeAttributeMap(attributeMapId);
    }

    public void removeResourceAttributeMaps(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId is null");
        }

        managedSystemService.removeResourceAttributeMaps(resourceId);
    }

    public List<AttributeMap> getResourceAttributeMaps(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId is null");
        }
        List<AttributeMapEntity> amEList = managedSystemService
                .getResourceAttributeMaps(resourceId);
        return amEList == null ? null : attributeMapDozerConverter
                .convertToDTOList(amEList, true);
    }

    @Override
    public List<AttributeMap> findResourceAttributeMaps(
            AttributeMapSearchBean searchBean) {
        if (searchBean == null) {
            throw new IllegalArgumentException("searchBean is null");
        }
        List<AttributeMapEntity> ameList = managedSystemService
                .getResourceAttributeMaps(searchBean);
        return (ameList == null) ? null : attributeMapDozerConverter
                .convertToDTOList(ameList, true);
    }

    public List<AttributeMap> getAllAttributeMaps() {
        List<AttributeMapEntity> amEList = managedSystemService
                .getAllAttributeMaps();
        return amEList == null ? null : attributeMapDozerConverter
                .convertToDTOList(amEList, true);
    }

    public List<DefaultReconciliationAttributeMap> getAllDefaultReconcileMap() {
        List<DefaultReconciliationAttributeMapEntity> list = managedSystemService
                .getAllDefaultReconAttributeMap();
        return list == null ? null
                : defaultReconciliationAttributeMapDozerConverter
                        .convertToDTOList(list, false);
    }

    @Override
    public List<ManagedSysRuleDto> getRulesByManagedSysId(String managedSysId) {
        List<ManagedSysRuleEntity> resList = managedSystemService
                .getRulesByManagedSysId(managedSysId);
        return resList == null ? null : managedSysRuleDozerConverter
                .convertToDTOList(resList, false);
    }

    @Override
    public ManagedSysRuleDto addRules(ManagedSysRuleDto entity) {
        if (entity == null)
            return null;
        ManagedSysRuleEntity res = managedSystemService
                .addRules(managedSysRuleDozerConverter.convertToEntity(entity,
                        false));
        return res == null ? null : managedSysRuleDozerConverter.convertToDTO(
                res, false);
    }

    @Override
    public void deleteRules(String ruleId) {
        managedSystemService.deleteRules(ruleId);
    }

}