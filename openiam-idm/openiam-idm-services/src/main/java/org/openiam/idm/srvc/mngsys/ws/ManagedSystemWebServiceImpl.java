package org.openiam.idm.srvc.mngsys.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.AttributeMapDozerConverter;
import org.openiam.dozer.converter.ManagedSysDozerConverter;
import org.openiam.dozer.converter.ManagedSystemObjectMatchDozerConverter;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.*;
import org.openiam.idm.srvc.mngsys.searchbeans.converter.ManagedSystemSearchBeanConverter;
import org.openiam.idm.srvc.mngsys.service.*;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.LinkedList;
import java.util.List;

@Service("managedSystemWebWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService", targetNamespace = "urn:idm.openiam.org/srvc/mngsys/service", portName = "ManagedSystemWebServicePort", serviceName = "ManagedSystemWebService")
public class ManagedSystemWebServiceImpl implements ManagedSystemWebService {
	@Autowired
	protected ManagedSystemService managedSystemService;
	@Autowired
	protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;
	protected ApproverAssociationDAO approverAssociationDao;
	@Autowired
	protected UserDataService userManager;
	@Autowired
	protected KeyManagementService keyManagementService;
	@Autowired
	private ManagedSysDozerConverter managedSysDozerConverter;

	@Autowired
	private AttributeMapDozerConverter attributeMapDozerConverter;
	@Autowired
	private ManagedSystemObjectMatchDozerConverter managedSystemObjectMatchDozerConverter;

	@Autowired
	private ManagedSystemSearchBeanConverter managedSystemSearchBeanConverter;

	private static final Log log = LogFactory
			.getLog(ManagedSystemWebServiceImpl.class);
	protected Cryptor cryptor;

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
				false);
		managedSystemService.addManagedSys(entity);

		return managedSysDozerConverter.convertToDTO(entity, false);
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
				.convertToEntity(sys, false);
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
		List<ManagedSystemObjectMatchEntity> objList = managedSysObjectMatchDao
				.findBySystemId(managedSystemId, objectType);
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
		return managedSysDozerConverter.convertToDTO(managedSystemService
				.getManagedSysByResource(resourceId, "ACTIVE"), true);
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

	public Cryptor getCryptor() {
		return cryptor;
	}

	public void setCryptor(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

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

	public ApproverAssociationDAO getApproverAssociationDao() {
		return approverAssociationDao;
	}

	public void setApproverAssociationDao(
			ApproverAssociationDAO approverAssociationDao) {
		this.approverAssociationDao = approverAssociationDao;
	}

	public ApproverAssociation addApproverAssociation(
			ApproverAssociation approverAssociation) {
		if (approverAssociation == null) {
			throw new IllegalArgumentException(
					"approverAssociation object is null");
		}

		return approverAssociationDao.add(approverAssociation);
	}

	public ApproverAssociation updateApproverAssociation(
			ApproverAssociation approverAssociation) {
		if (approverAssociation == null) {
			throw new IllegalArgumentException(
					"approverAssociation object is null");
		}

		return approverAssociationDao.update(approverAssociation);
	}

	public ApproverAssociation getApproverAssociation(
			String approverAssociationId) {
		if (approverAssociationId == null) {
			throw new IllegalArgumentException("approverAssociationId is null");
		}

		return approverAssociationDao.findById(approverAssociationId);
	}

	public void removeApproverAssociation(String approverAssociationId) {
		if (approverAssociationId == null) {
			throw new IllegalArgumentException("approverAssociationId is null");
		}
		ApproverAssociation obj = this.approverAssociationDao
				.findById(approverAssociationId);
		this.approverAssociationDao.remove(obj);
	}

	public int removeAllApproverAssociations() {
		return this.approverAssociationDao.removeAllApprovers();
	}

	public List<ApproverAssociation> getApproverByRequestType(
			String requestType, int level) {
		if (requestType == null) {
			throw new IllegalArgumentException("requestType is null");
		}
		return this.approverAssociationDao.findApproversByRequestType(
				requestType, level);
	}

	public List<ApproverAssociation> getAllApproversByRequestType(
			String requestType) {
		if (requestType == null) {
			throw new IllegalArgumentException("requestType is null");
		}
		return approverAssociationDao
				.findAllApproversByRequestType(requestType);
	}

	public List<ApproverAssociation> getApproversByObjectId(
			String associationObjId) {
		if (associationObjId == null) {
			throw new IllegalArgumentException("associationObjId is null");
		}
		return this.approverAssociationDao
				.findApproversByObjectId(associationObjId);
	}

	public int removeApproversByObjectId(String associationObjId) {
		if (associationObjId == null) {
			throw new IllegalArgumentException("associationObjId is null");
		}
		return this.approverAssociationDao
				.removeApproversByObjectId(associationObjId);
	}

	// find by RESOURCE, GROUP, ROLE, SUPERVISOR,INDIVIDUAL
	List<ApproverAssociation> getApproversByObjectType(String associationType) {
		if (associationType == null) {
			throw new IllegalArgumentException("associationType is null");
		}
		return this.approverAssociationDao
				.findApproversByObjectType(associationType);
	}

	public int removeApproversByObjectType(String associationType) {
		if (associationType == null) {
			throw new IllegalArgumentException("associationType is null");
		}
		return this.approverAssociationDao
				.removeApproversByObjectType(associationType);
	}

	List<ApproverAssociation> getApproversByAction(String associationObjId,
			String action, int level) {
		if (associationObjId == null) {
			throw new IllegalArgumentException("associationObjId is null");
		}
		return this.approverAssociationDao.findApproversByAction(
				associationObjId, action, level);
	}

	List<ApproverAssociation> getApproversByUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		return this.approverAssociationDao.findApproversByUser(userId);
	}

	public int removeApproversByUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		return this.approverAssociationDao.removeApproversByUser(userId);
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

	public int removeResourceAttributeMaps(String resourceId) {
		if (resourceId == null) {
			throw new IllegalArgumentException("resourceId is null");
		}

		return managedSystemService.removeResourceAttributeMaps(resourceId);
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

	public List<AttributeMap> getAllAttributeMaps() {
		List<AttributeMapEntity> amEList = managedSystemService
				.getAllAttributeMaps();
		return amEList == null ? null : attributeMapDozerConverter
				.convertToDTOList(amEList, true);
	}
}
