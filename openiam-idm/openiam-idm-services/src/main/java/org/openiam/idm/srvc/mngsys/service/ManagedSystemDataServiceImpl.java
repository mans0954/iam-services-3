package org.openiam.idm.srvc.mngsys.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import java.util.List;
import java.util.ResourceBundle;

@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService", 
		targetNamespace = "urn:idm.openiam.org/srvc/mngsys/service", 
		portName = "ManagedSystemWebServicePort",
		serviceName = "ManagedSystemWebService")
public class ManagedSystemDataServiceImpl implements ManagedSystemDataService {
    @Autowired
	protected ManagedSysDAO managedSysDao;
    @Autowired
	protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
	protected ApproverAssociationDAO approverAssociationDao;
    @Autowired
	protected UserDataService userManager;

	protected AttributeMapDAO attributeMapDao;
    @Autowired
    protected KeyManagementService keyManagementService;
	@Autowired
    private ManagedSysDozerConverter managedSysDozerConverter;
    @Autowired
    private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

    private static final Log log = LogFactory.getLog(ManagedSystemDataServiceImpl.class);
	protected Cryptor cryptor;
	static protected ResourceBundle res = ResourceBundle.getBundle("securityconf");
	boolean encrypt = true;	// default encryption setting


	public ManagedSys addManagedSystem(ManagedSys sys) {

		if (sys == null) {
			throw new NullPointerException("sys is null");
		}
		
        if (encrypt && sys.getPswd() != null) {
        	try {
        		sys.setPswd(cryptor.encrypt(keyManagementService.getUserKey(sys.getUserId(), KeyName.password.name()),sys.getPswd()));
        	}catch(Exception e) {
        		log.error(e);
        	}
        };

        ManagedSysEntity entity =  managedSysDao.add(managedSysDozerConverter.convertToEntity(sys,false));
        return  managedSysDozerConverter.convertToDTO(entity,false);
	}

	public ManagedSys getManagedSys(String sysId) {
		if (sysId == null) {
			throw new NullPointerException("sysId is null");
		}

		ManagedSysEntity sys = managedSysDao.findById(sysId);
        ManagedSys sysDto = null;
        if(sys!=null){
            sysDto = managedSysDozerConverter.convertToDTO(sys,true);
            if (sysDto != null && sysDto.getPswd() != null) {
                try {
                    sysDto.setDecryptPassword(cryptor.decrypt(keyManagementService.getUserKey(sys.getUserId(), KeyName.password.name()),sys.getPswd()));
                }catch(Exception e) {
                    log.error(e);
                }
            }
        }
		return sysDto;
	}

	public ManagedSys[] getManagedSysByProvider(String providerId) {
		if (providerId == null) {
			throw new NullPointerException("providerId is null");
		}
		List<ManagedSysEntity> sysList= managedSysDao.findbyConnectorId(providerId);
        if(sysList == null) {
            return null;
        }
		int size = sysList.size();
		ManagedSys[] sysAry = new ManagedSys[size];
        managedSysDozerConverter.convertToDTOList(sysList, true).toArray(sysAry);
		return sysAry;
	}
	
	/**
	 * Returns an array of ManagedSys object for a security domain.  
	 * @param domainId
	 * @return
	 */
	public ManagedSys[] getManagedSysByDomain(String domainId) {
		if (domainId == null) {
			throw new NullPointerException("domainId is null");
		}
		List<ManagedSysEntity> sysList= managedSysDao.findbyDomain(domainId);
        if(sysList == null) {
            return null;
        }
		int size = sysList.size();
		ManagedSys[] sysAry = new ManagedSys[size];
        managedSysDozerConverter.convertToDTOList(sysList, true).toArray(sysAry);
		return sysAry;
		
	}
	
	public ManagedSys[] getAllManagedSys() {
		List<ManagedSysEntity> sysList= managedSysDao.findAllManagedSys();
        if(sysList == null) {
            return null;
        }
		int size = sysList.size();
		ManagedSys[] sysAry = new ManagedSys[size];
        managedSysDozerConverter.convertToDTOList(sysList, true).toArray(sysAry);
		return sysAry;
	}
	

	public void removeManagedSystem(String sysId) {
		if (sysId == null) {
			throw new NullPointerException("sysId is null");
		}

        ManagedSysEntity sys = managedSysDao.findById(sysId);
		managedSysDao.delete(sys);
	}

	public void updateManagedSystem(ManagedSys sys) {
		if (sys == null) {
			throw new NullPointerException("sys is null");
		}
        if (encrypt && sys.getPswd() != null) {
        	try {
        		sys.setPswd(cryptor.encrypt(keyManagementService.getUserKey(sys.getUserId(), KeyName.password.name()), sys.getPswd()));
        	}catch(Exception e) {
        		log.error(e);
        	}
        };
        
		managedSysDao.merge(managedSysDozerConverter.convertToEntity(sys, false));
	}
	
	/**
	 * Finds objects for an object type (like User, Group) for a ManagedSystem definition
	 * @param managedSystemId
	 * @param objectType
	 * @return
	 */
	public ManagedSystemObjectMatch[] managedSysObjectParam(String managedSystemId, String objectType) {
		if (managedSystemId == null) {
			throw new NullPointerException("managedSystemId is null");
		}
		if (objectType == null) {
			throw new NullPointerException("objectType is null");
		}
		List<ManagedSystemObjectMatchEntity> objList = managedSysObjectMatchDao.findBySystemId(managedSystemId, objectType);
		if (objList == null) {
			return null;
		}
		int size = objList.size();
		ManagedSystemObjectMatch[] objAry = new ManagedSystemObjectMatch[size];
        managedSystemObjectMatchDozerConverter.convertToDTOList(objList,false).toArray(objAry);
		return objAry;
	}

	public ManagedSys getManagedSysByResource(String resourceId) {
		if (resourceId == null) {
			throw new NullPointerException("resourceId is null");
		}		
		return managedSysDozerConverter.convertToDTO(managedSysDao.findByResource(resourceId, "ACTIVE"), true);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService#getApproversByAction(java.lang.String, java.lang.String, int)
	 */
/*	public List<ApproverAssociation> getApproversByAction(String managedSysId,
			String action, int level) {
		if ( managedSysId == null) {
			throw new NullPointerException("managedSysId is null");
		}
		return  resourceApproverDao.findApproversByAction(managedSysId, action, level);
	}
*/
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService#getApproversByResource(java.lang.String)
	 */
/*	public List<ApproverAssociation> getApproversByResource(String managedSysId) {
		if ( managedSysId == null) {
			throw new NullPointerException("managedSysId is null");
		}
		return  resourceApproverDao.findApproversByResource(managedSysId);
	}
*/

	
	public Cryptor getCryptor() {
		return cryptor;
	}

	public void setCryptor(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService#getManagedSysByName(java.lang.String)
	 */
	public ManagedSys getManagedSysByName(String name) {
		if (name == null) {
			throw new NullPointerException("Parameter Managed system name is null");
		}
		return managedSysDozerConverter.convertToDTO(managedSysDao.findByName(name),true);
	}

	//Approver Association  ================================================================

	
	public ApproverAssociationDAO getApproverAssociationDao() {
		return approverAssociationDao;
	}

	public void setApproverAssociationDao(ApproverAssociationDAO approverAssociationDao) {
		this.approverAssociationDao = approverAssociationDao;
	}


	public ApproverAssociation addApproverAssociation(ApproverAssociation approverAssociation) {
        if(approverAssociation == null) {
            throw new IllegalArgumentException("approverAssociation object is null");
        }

		return approverAssociationDao.add(approverAssociation);
	}

	public ApproverAssociation updateApproverAssociation(ApproverAssociation approverAssociation) {
        if(approverAssociation == null) {
            throw new IllegalArgumentException("approverAssociation object is null");
        }

		return approverAssociationDao.update(approverAssociation);
	}

	public ApproverAssociation getApproverAssociation(String approverAssociationId) {
        if(approverAssociationId == null) {
            throw new IllegalArgumentException("approverAssociationId is null");
        }

		return approverAssociationDao.findById(approverAssociationId);
	}

	public void removeApproverAssociation(String approverAssociationId) {
        if(approverAssociationId == null) {
            throw new IllegalArgumentException("approverAssociationId is null");
        }
		ApproverAssociation obj = this.approverAssociationDao.findById(approverAssociationId);
		this.approverAssociationDao.remove(obj);
	}

	public int removeAllApproverAssociations() {
		return this.approverAssociationDao.removeAllApprovers();
	}

	
	public List<ApproverAssociation> getApproverByRequestType(String requestType, int level) {
        if(requestType == null) {
            throw new IllegalArgumentException("requestType is null");
        }
		return this.approverAssociationDao.findApproversByRequestType(requestType, level);
	}
	
	public List<ApproverAssociation> getAllApproversByRequestType(String requestType) {
        if(requestType == null) {
            throw new IllegalArgumentException("requestType is null");
        }
		return approverAssociationDao.findAllApproversByRequestType(requestType);		
	}
	
	public List<ApproverAssociation> getApproversByObjectId (String associationObjId) {
        if(associationObjId == null) {
            throw new IllegalArgumentException("associationObjId is null");
        }
		return this.approverAssociationDao.findApproversByObjectId(associationObjId);
	}

	public int removeApproversByObjectId(String associationObjId) {
        if(associationObjId == null) {
            throw new IllegalArgumentException("associationObjId is null");
        }
		return this.approverAssociationDao.removeApproversByObjectId(associationObjId);
	}

	// find by RESOURCE, GROUP, ROLE, SUPERVISOR,INDIVIDUAL
	List<ApproverAssociation> getApproversByObjectType(String associationType) {
        if(associationType == null) {
            throw new IllegalArgumentException("associationType is null");
        }
		return this.approverAssociationDao.findApproversByObjectType(associationType);
	}
	
	public int removeApproversByObjectType(String associationType) {
        if(associationType == null) {
            throw new IllegalArgumentException("associationType is null");
        }
		return this.approverAssociationDao.removeApproversByObjectType(associationType);
	}
	
	
	List<ApproverAssociation> getApproversByAction(String associationObjId,
			String action, int level) {
        if(associationObjId == null) {
            throw new IllegalArgumentException("associationObjId is null");
        }
		return this.approverAssociationDao.findApproversByAction(associationObjId, action, level);
	}

	
	List<ApproverAssociation> getApproversByUser(String userId) {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
		return this.approverAssociationDao.findApproversByUser(userId);
	}
	
	public int removeApproversByUser(String userId) {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
		return this.approverAssociationDao.removeApproversByUser(userId);
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService#addManagedSystemObjectMatch(org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch)
	 */
	public void addManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
		managedSysObjectMatchDao.save(managedSystemObjectMatchDozerConverter.convertToEntity(obj,false));
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSystemDataService#updateManagedSystemObjectMatch(org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch)
	 */
	public void updateManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
		this.managedSysObjectMatchDao.merge(managedSystemObjectMatchDozerConverter.convertToEntity(obj,false));
		
	}
	
	public void removeManagedSystemObjectMatch(ManagedSystemObjectMatch obj) {
		this.managedSysObjectMatchDao.delete(managedSystemObjectMatchDozerConverter.convertToEntity(obj,false));
	}

	public AttributeMap getAttributeMap(String attributeMapId) {
        if(attributeMapId == null) {
            throw new IllegalArgumentException("attributeMapId is null");
        }

		AttributeMap obj = attributeMapDao.findById(attributeMapId);

		return obj;
	}

	public AttributeMap addAttributeMap(AttributeMap attributeMap) {
        if(attributeMap == null) {
            throw new IllegalArgumentException("AttributeMap object is null");
        }

		return attributeMapDao.add(attributeMap);
	}

	public AttributeMap updateAttributeMap(AttributeMap attributeMap) {
        if(attributeMap == null) {
            throw new IllegalArgumentException("attributeMap object is null");
        }

		return attributeMapDao.update(attributeMap);
	}

	public void removeAttributeMap(String attributeMapId) {
		if (attributeMapId == null) {
			throw new IllegalArgumentException("attributeMapId is null");
		}
		AttributeMap obj = this.attributeMapDao.findById(attributeMapId);
		this.attributeMapDao.remove(obj);
		
	}

	public int removeResourceAttributeMaps(String resourceId) {
        if(resourceId == null) {
            throw new IllegalArgumentException("resourceId is null");
        }

		return this.attributeMapDao.removeResourceAttributeMaps(resourceId);
	}

	public List<AttributeMap> getResourceAttributeMaps(String resourceId) {
		if (resourceId == null) {
			throw new IllegalArgumentException("resourceId is null");
		}
		return attributeMapDao.findByResourceId(resourceId);
	}

	public List<AttributeMap> getAllAttributeMaps() {
		List<AttributeMap> attributeMapList = attributeMapDao.findAllAttributeMaps();

		return attributeMapList;
	}




    public AttributeMapDAO getAttributeMapDao() {
		return attributeMapDao;
	}

	public void setAttributeMapDao(AttributeMapDAO attributeMapDao) {
		this.attributeMapDao = attributeMapDao;
	}

	
}
