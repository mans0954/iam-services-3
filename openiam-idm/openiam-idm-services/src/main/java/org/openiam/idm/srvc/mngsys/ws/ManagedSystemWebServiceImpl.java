package org.openiam.idm.srvc.mngsys.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.*;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ApproverAssocationSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ManagedSystemSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.util.SSLCert;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.LinkedList;
import java.util.List;


@Service("managedSysService")
@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService", targetNamespace = "urn:idm.openiam.org/srvc/mngsys/service", portName = "ManagedSystemWebServicePort", serviceName = "ManagedSystemWebService")
public class ManagedSystemWebServiceImpl implements ManagedSystemWebService {

    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    @Value("${KEYSTORE}")
    private String keystore;

    @Value("${KEYSTORE_PSWD}")
    private String keystorePasswd;

    @Value("${openiam.default_managed_sys}")
    private String defaultManagedSystemId;

    @Autowired
    private ManagedSystemService managedSystemService;

    @Autowired
    private ApproverAssociationDAO approverAssociationDao;

    @Autowired
    private KeyManagementService keyManagementService;

//    @Autowired
//    private ManagedSysRuleDozerConverter managedSysRuleDozerConverter;
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
    private AuditLogService auditLogService;

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
    public Response requestSSLCert(@WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys, @WebParam(name = "requesterId", targetNamespace = "") String requesterId) {
        Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLog auditLog = new IdmAuditLog();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setAction(AuditAction.SSL_CERT_REQUEST.value());
        auditLog.setTargetManagedSys(sys.getId(), sys.getName());
        try {
            String host = sys.getHostUrl();
            if(host.indexOf("://") > 0) {
               host = host.substring(host.indexOf("://")+"://".length());
            }
            SSLCert.installCert(host, sys.getPort(), keystorePasswd, keystore);
            auditLog.succeed();
        } catch(Exception ex) {
            auditLog.fail();
            auditLog.setFailureReason(ex.toString());
            response.setErrorText(ex.toString());
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(ResponseCode.INTERNAL_ERROR);
        } finally {
            auditLogService.enqueue(auditLog);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeMap> getAttributeMapsByManagedSysId(final String managedSysId) {
        List<AttributeMapEntity> attributeMaps = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
        return attributeMapDozerConverter.convertToDTOList(attributeMaps, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagedSysDto> getManagedSystems(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean,
            @WebParam(name = "size", targetNamespace = "") Integer size,
            @WebParam(name = "from", targetNamespace = "") Integer from) {
        final ManagedSysEntity managedSysEntity = managedSystemSearchBeanConverter.convert(searchBean);
        final List<ManagedSysEntity> sysEntities = managedSystemService.getManagedSystemsByExample(managedSysEntity, from, size);
        return managedSysDozerConverter.convertToDTOList(sysEntities, false);
    }

    @Override
    public Response saveManagedSystem(final ManagedSysDto sys) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(sys == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		if(StringUtils.isBlank(sys.getName())) {
    			throw new BasicDataServiceException(ResponseCode.NO_NAME);
    		}

            boolean isDefaultManagedSystem = sys.getId() != null && sys.getId().equals(defaultManagedSystemId);
    		if(!isDefaultManagedSystem && StringUtils.isBlank(sys.getConnectorId())) {
    			throw new BasicDataServiceException(ResponseCode.CONNECTOR_REQUIRED);
    		}

            if(isDefaultManagedSystem && StringUtils.isNotBlank(sys.getConnectorId())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            if (encrypt && sys.getPswd() != null) {
    			sys.setPswd(cryptor.encrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), sys.getPswd()));
    		}


    		if(StringUtils.isBlank(sys.getId())) {
    			managedSystemService.addManagedSys(sys);
    		}  else {
    			managedSystemService.updateManagedSys(sys);
    		}
    		response.setResponseValue(sys.getId());
    	} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't remove managed system", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
        
    	return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ManagedSysDto getManagedSys(String sysId) {
    	ManagedSysDto sysDto = null;
        if (sysId != null) {
        	ManagedSysEntity sys = managedSystemService.getManagedSysById(sysId);
        	if (sys != null) {
        		sysDto = managedSysDozerConverter.convertToDTO(sys, true);
        		if (sysDto != null && sysDto.getPswd() != null) {
        			try {
        				final byte[] bytes = keyManagementService.getUserKey(systemUserId, KeyName.password.name());
        				sysDto.setDecryptPassword(cryptor.decrypt(bytes, sys.getPswd()));
        			} catch (Exception e) {
        				log.error("Can't decrypt", e);
        			}
        		}
        	}
        }
        return sysDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ManagedSysDto> getAllManagedSys() {
        final List<ManagedSysEntity> sysList = managedSystemService.getAllManagedSys();
        return managedSysDozerConverter.convertToDTOList(sysList, true);
    }

    @Override
    public Response removeManagedSystem(String sysId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(StringUtils.isBlank(sysId)) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
            List<AuthProviderEntity> authProviderEntities = managedSystemService.findAuthProvidersByManagedSysId(sysId);
            if (CollectionUtils.isNotEmpty(authProviderEntities)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_AUTHENTICATION_PROVIDER, authProviderEntities.get(0).getName());
            }
    		managedSystemService.removeManagedSysById(sysId);
    	} catch (BasicDataServiceException e) {
            response.setResponseValue(e.getResponseValue());
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't remove managed system", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
    	return response;
    }

    /**
     * Finds objects for an object type (like User, Group) for a ManagedSystem
     * definition
     * 
     * @param managedSystemId
     * @param objectType
     * @return
     */
    @Override
    @Cacheable(value="managedSysObjectParam", key="{ #managedSystemId, #objectType}")
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

    @Override
    @Transactional(readOnly = true)
    public ManagedSysDto getManagedSysByResource(String resourceId) {
    	ManagedSysDto sysDto = null;
        if(resourceId != null) {
        	ManagedSysEntity sys = managedSystemService.getManagedSysByResource(resourceId, "ACTIVE");
        	if (sys != null) {
        		sysDto = managedSysDozerConverter.convertToDTO(sys, false);
        		if (sysDto != null && sysDto.getPswd() != null) {
        			try {
        				sysDto.setDecryptPassword(cryptor.decrypt(
                            keyManagementService.getUserKey(systemUserId,
                                    KeyName.password.name()), sys.getPswd()));
        			} catch (Exception e) {
        				log.error(e);
        			}
        		}
        	}
        }
        return sysDto;
    }

    // Approver Association
    // ================================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApproverAssociation> getApproverAssociations(
            ApproverAssocationSearchBean searchBean, int from, int size) {
        final ApproverAssociationEntity entity = approverSearchBeanConverter.convert(searchBean);
        final List<ApproverAssociationEntity> entityList = approverAssociationDao.getByExample(entity, from, size);
        return (entityList != null) ? approverAssociationDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy()) : null;
    }
    
    @Override
	public Response saveApproverAssociations(final List<ApproverAssociation> approverAssociationList, final AssociationType type, final String entityId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
    	 try {
    		 if(CollectionUtils.isNotEmpty(approverAssociationList)) {
    			 for(final ApproverAssociation approverAssociation : approverAssociationList) {
		             if (approverAssociation == null) {
		                 throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
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
		
		             if (StringUtils.isBlank(approverAssociation.getAssociationEntityId())
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
		             
		             if(approverAssociation.getApproverLevel() == null) {
		            	 approverAssociation.setApproverLevel(Integer.valueOf(0));
		             }
	    		 }
    		 }

    		 final List<ApproverAssociationEntity> entityList = approverAssociationDozerConverter.convertToEntityList(approverAssociationList, true);
    		 managedSystemService.saveApproverAssociations(entityList, type, entityId);
         } catch (BasicDataServiceException e) {
             response.setErrorCode(e.getCode());
             response.setStatus(ResponseStatus.FAILURE);
         } catch (Throwable e) {
             log.error(e);
             response.setStatus(ResponseStatus.FAILURE);
         }
    	return response;
    }

    @Override
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


    @Override
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
            log.error("Can't save", e);
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
    @CacheEvict(value = "managedSysObjectParam", allEntries=true)
    public Response saveManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (obj == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            if(StringUtils.isNotBlank(obj.getObjectSearchId())) {
            	managedSystemService.updateManagedSystemObjectMatch(obj);
                response.setResponseValue(obj.getObjectSearchId());
            } else {
            	String objId = managedSystemService.saveManagedSystemObjectMatch(obj);
                response.setResponseValue(objId);
            }
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't save", e);
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    @Override
    @CacheEvict(value = "managedSysObjectParam", allEntries=true)
    public void removeManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
        this.managedSystemService.deleteManagedSystemObjectMatch(obj.getObjectSearchId());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value="resourceAttributeMaps", key="{ #attributeMapId}")
    public AttributeMap getAttributeMap(String attributeMapId) {
        if (attributeMapId == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        AttributeMapEntity obj = managedSystemService
                .getAttributeMap(attributeMapId);
        return obj == null ? null : attributeMapDozerConverter.convertToDTO(
                obj, true);
    }

    @Override
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
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
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
    public void deleteAttributesMapList(List<String> ids) throws Exception {
        managedSystemService.deleteAttributesMapList(ids);
    }

    @Override
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
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

    @Override
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
    public AttributeMap updateAttributeMap(AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new IllegalArgumentException("attributeMap object is null");
        }
        managedSystemService.updateAttributeMap(attributeMapDozerConverter
                .convertToEntity(attributeMap, true));
        return attributeMap;
    }

    @Override
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
    public void removeAttributeMap(String attributeMapId) {
        if (attributeMapId == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        managedSystemService.removeAttributeMap(attributeMapId);
    }

    @Override
    @CacheEvict(value = "resourceAttributeMaps", allEntries=true)
    public void removeResourceAttributeMaps(String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId is null");
        }

        managedSystemService.removeResourceAttributeMaps(resourceId);
    }

    @Override
	@Transactional(readOnly = true)
    @Cacheable(value="resourceAttributeMaps", key="{ #resourceId}")
    public List<AttributeMap> getResourceAttributeMaps(final String resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("resourceId is null");
        }
        List<AttributeMapEntity> amEList = managedSystemService
                .getResourceAttributeMaps(resourceId);
        List<AttributeMap> mapList = new LinkedList<AttributeMap>();
        if(amEList != null) {
            for(AttributeMapEntity ame : amEList) {
                AttributeMap am = attributeMapDozerConverter.convertToDTO(ame, true);
                mapList.add(am);
            }
        }
        return mapList;
    }

    @Override
//    @Cacheable(value="resourceAttributeMaps", key="{ #searchBean.cacheUniqueBeanKey}") //TODO: take into account deleting of attribute policies
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

    @Override
    public List<AttributeMap> getAllAttributeMaps() {
        List<AttributeMapEntity> amEList = managedSystemService
                .getAllAttributeMaps();
        return amEList == null ? null : attributeMapDozerConverter
                .convertToDTOList(amEList, true);
    }

    @Override
    public List<DefaultReconciliationAttributeMap> getAllDefaultReconcileMap() {
        List<DefaultReconciliationAttributeMapEntity> list = managedSystemService
                .getAllDefaultReconAttributeMap();
        return list == null ? null
                : defaultReconciliationAttributeMapDozerConverter
                        .convertToDTOList(list, false);
    }

//    @Override
//    public List<ManagedSysRuleDto> getRulesByManagedSysId(String managedSysId) {
//        List<ManagedSysRuleEntity> resList = managedSystemService
//                .getRulesByManagedSysId(managedSysId);
//        return resList == null ? null : managedSysRuleDozerConverter
//                .convertToDTOList(resList, false);
//    }

//    @Override
//    public ManagedSysRuleDto addRules(ManagedSysRuleDto entity) {
//        if (entity == null)
//            return null;
//        ManagedSysRuleEntity res = managedSystemService
//                .addRules(managedSysRuleDozerConverter.convertToEntity(entity,
//                        false));
//        return res == null ? null : managedSysRuleDozerConverter.convertToDTO(
//                res, false);
//    }

//    @Override
//    public void deleteRules(String ruleId) {
//        managedSystemService.deleteRules(ruleId);
//    }

}
