package org.openiam.idm.srvc.mngsys.ws;

import java.util.LinkedList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.dozer.converter.ApproverAssociationDozerConverter;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.DefaultReconciliationAttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.dozer.converter.MngSysPolicyDozerConverter;
import org.openiam.dozer.converter.ResourcePropDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AttributeMapSearchBean;
import org.openiam.idm.searchbeans.ManagedSysSearchBean;
import org.openiam.idm.searchbeans.MngSysPolicySearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.bean.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.bean.MngSysPolicyBean;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.DefaultReconciliationAttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.DefaultReconciliationAttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;
import org.openiam.idm.srvc.mngsys.domain.*;
import org.openiam.idm.srvc.mngsys.dto.*;
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

    @Autowired
    private MngSysPolicyDozerConverter mngSysPolicyDozerConverter;

    @Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;

    @Autowired
    private AttributeMapDozerConverter attributeMapDozerConverter;

    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    @Autowired
    private ApproverAssociationDozerConverter approverAssociationDozerConverter;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private DefaultReconciliationAttributeMapDozerConverter defaultReconciliationAttributeMapDozerConverter;
    
    @Autowired
    private ResourcePropDozerConverter resourcePropConverter;

    @Autowired
    private LoginDataService loginManager;

    private static final Log log = LogFactory
            .getLog(ManagedSystemWebServiceImpl.class);

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    boolean encrypt = true; // default encryption setting

    @Override
    public int getManagedSystemsCount(
            @WebParam(name = "searchBean", targetNamespace = "") ManagedSysSearchBean searchBean) {
        return managedSystemService.count(searchBean);
    }

    @Override
    public Response requestSSLCert(@WebParam(name = "sys", targetNamespace = "") ManagedSysDto sys, @WebParam(name = "requesterId", targetNamespace = "") String requesterId) {
        Response response = new Response(ResponseStatus.SUCCESS);
        IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
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
    public List<ManagedSysDto> getManagedSystems(final ManagedSysSearchBean searchBean, final int from, final int size) {
        final List<ManagedSysDto> sysDtos = managedSystemService.getManagedSystemsByExample(searchBean, from, size);
        return sysDtos;
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

            if(StringUtils.isBlank(sys.getConnectorId())) {
                throw new BasicDataServiceException(ResponseCode.CONNECTOR_REQUIRED);
            }

            if (encrypt && sys.getPswd() != null) {
                sys.setPswd(cryptor.encrypt(keyManagementService.getUserKey(systemUserId, KeyName.password.name()), sys.getPswd()));
            }

            final ManagedSysEntity entity = managedSysDozerConverter.convertToEntity(sys, true);
            managedSystemService.save(entity);
            response.setResponseValue(entity.getId());
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
    @Transactional(readOnly = true)
    /* AM-851 */
    //@Cacheable(value = "managedSysAttributeMaps", key = "{#managedSysId}")
    public List<AttributeMapEntity> getAttributeMapsByManagedSysId(final String managedSysId) {
        List<AttributeMapEntity> attributeMaps = managedSystemService.getAttributeMapsByManagedSysId(managedSysId);
        return attributeMaps;
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
            List<LoginEntity> loginEntities = loginManager.getAllLoginByManagedSys(sysId);
            if (CollectionUtils.isNotEmpty(loginEntities)) {
                throw new BasicDataServiceException(ResponseCode.LINKED_TO_USERS, String.valueOf(loginEntities.size()));
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
        final List<ApproverAssociationEntity> entityList = approverAssociationDao.getByExample(searchBean, from, size);
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
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("managedSysObjectParam")
            }
        )
    public Response saveManagedSystemObjectMatch(final ManagedSystemObjectMatch obj) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (obj == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            final ManagedSystemObjectMatchEntity entity = managedSystemObjectMatchDozerConverter.convertToEntity(obj, true);
            managedSystemService.saveManagedSysObjectMatch(entity);
            response.setResponseValue(entity.getId());
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
    @Transactional(readOnly = true)
    @Cacheable(value = "resourceAttributeMaps", key = "{#id}")
    public AttributeMap getAttributeMap(String id) {
        if (id == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        AttributeMapEntity obj = managedSystemService
                .getAttributeMap(id);
        return obj == null ? null : attributeMapDozerConverter.convertToDTO(
                obj, true);
    }

    @Override
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("resourceAttributeMaps")
            }
        )
    public AttributeMap addAttributeMap(final AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new IllegalArgumentException("AttributeMap object is null");
        }
        AttributeMapEntity entity = attributeMapDozerConverter.convertToEntity(
                attributeMap, true);
        return attributeMapDozerConverter.convertToDTO(
                managedSystemService.addAttributeMap(entity), true);
    }

    @Override
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("resourceAttributeMaps")
            }
        )
    public void deleteAttributesMapList(final List<String> ids) throws Exception {
        managedSystemService.deleteAttributesMapList(ids);
    }

    @Override
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("resourceAttributeMaps")
            }
        )
    public AttributeMap updateAttributeMap(final AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new IllegalArgumentException("attributeMap object is null");
        }
        managedSystemService.updateAttributeMap(attributeMapDozerConverter
                .convertToEntity(attributeMap, true));
        return attributeMap;
    }

    @Override
    @CacheKeyEviction(
        	evictions={
                @CacheKeyEvict("resourceAttributeMaps")
            }
        )
    public void removeAttributeMap(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }
        managedSystemService.removeAttributeMap(id);
    }

    @Override
	@Transactional(readOnly = true)
    @Cacheable(value="resourceAttributeMapsByResource", key="{ #resourceId}")
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

/*    @Transactional(readOnly = true)
    @Override
    public List<ManagedSysDto> getAllManagedSysNames() {
        final List<ManagedSysEntity> sysList = managedSystemService.getAllManagedSysNames();
        return managedSysDozerConverter.convertToDTOList(sysList, true);
    }*/

    @Override
    public void removeMngSysPolicy(String mngSysPolicyId) throws Exception {
        managedSystemService.removeMngSysPolicy(mngSysPolicyId);
    }

    @Override
    public List<AttributeMap> getAttributeMapsByMngSysPolicyId(String mngSysPolicyId) {
        return managedSystemService.getAttributeMapsByMngSysPolicyId(mngSysPolicyId);
    }

    @Override
    public MngSysPolicyDto getMngSysPolicyById(String mngSysPolicyId) {
        MngSysPolicyEntity entity = managedSystemService.getManagedSysPolicyById(mngSysPolicyId);
        return mngSysPolicyDozerConverter.convertToDTO(entity, true);
    }

    @Override
    public MngSysPolicyBean getMngSysPolicyBeanById(String mngSysPolicyId) {
        return new MngSysPolicyBean(getMngSysPolicyById(mngSysPolicyId));
    }

    @Override
    public List<MngSysPolicyDto> getMngSysPoliciesByMngSysId(String mngSysId) {
        return managedSystemService.getManagedSysPolicyByMngSysId(mngSysId);
    }

    @Override
    public List<MngSysPolicyDto> findMngSysPolicies(MngSysPolicySearchBean searchBean, Integer from, Integer size) {
        return managedSystemService.findMngSysPolicies(searchBean, from, size);
    }

    @Override
    public List<MngSysPolicyBean> findMngSysPolicyBeans(MngSysPolicySearchBean searchBean, Integer from, Integer size) {
        return managedSystemService.findMngSysPolicyBeans(searchBean, from, size);
    }

    @Override
    public int getMngSysPoliciesCount(MngSysPolicySearchBean searchBean) {
        return managedSystemService.getMngSysPoliciesCount(searchBean);
    }

    @Override
    public Response saveMngSysPolicyBean(MngSysPolicyBean mngSysPolicy) {
        Response res = new Response();
        try {
            res.setResponseValue(managedSystemService.saveMngSysPolicyBean(mngSysPolicy));
            res.succeed();
        } catch (BasicDataServiceException e) {
            res.setErrorCode(e.getCode());
            res.fail();
        }
        return res;
    }
}
