package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.ProfilePicture;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.validator.EntityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service("userProfileService")
@Transactional
public class UserProfileServiceImpl implements UserProfileService {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
    @Qualifier("userManager")
	private UserDataService userManager;
	
    @Autowired
    private MetadataElementTemplateService pageTemplateService;
    
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    
    @Autowired
    private LoginDataService loginDataService;
    
    @Autowired
    private EmailAddressDozerConverter emailAddressDozerConverter;
    
    @Autowired
    private AddressDozerConverter addressDozerConverter;
    
    @Autowired
    private PhoneDozerConverter phoneDozerConverter;
    
    @Autowired
    private UserDozerConverter userDozerConverter;

    @Autowired
    private ProfilePictureDAO profilePictureDAO;
    
    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
	
    @Autowired
    private SysConfiguration sysConfiguration;

	@Override
	public SaveTemplateProfileResponse saveUserProfileWrapper(final UserProfileRequestModel request) {
		final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
		try {
			if (request == null || request.getUser() == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}

			this.saveUserProfile(request);

		} catch (PageTemplateException e) {
			response.setCurrentValue(e.getCurrentValue());
			response.setElementName(e.getElementName());
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (BasicDataServiceException e) {
			response.setErrorTokenList(e.getErrorTokenList());
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't perform operation", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public void saveUserProfile(UserProfileRequestModel request) throws Exception {

		if (request.getUser() == null)
			throw new BasicDataServiceException(ResponseCode.USER_NOT_FOUND);

		final UserEntity userEntity = userDozerConverter.convertToEntity(request.getUser(), true);
		entityValidator.isValid(userEntity);
        pageTemplateService.validate(request);
		
        final UserEntity dbEntity = userManager.getUser(request.getUser().getId());
        
        final List<EmailAddressEntity> emailList = emailAddressDozerConverter.convertToEntityList(request.getEmails(), true);	 
        final List<AddressEntity> addressList = addressDozerConverter.convertToEntityList(request.getAddresses(), true);
        final List<PhoneEntity> phoneList = phoneDozerConverter.convertToEntityList(request.getPhones(), true);
        
        saveEmails(userEntity, emailList);
        saveAddresses(userEntity, addressList);
        savePhones(userEntity, phoneList);
        
        if(StringUtils.isBlank(userEntity.getFirstName())) {
        	throw new BasicDataServiceException(ResponseCode.FIRST_NAME_REQUIRED);
        }
        if(StringUtils.isBlank(userEntity.getLastName())) {
        	throw new BasicDataServiceException(ResponseCode.LAST_NAME_REQUIRED);
        }
        if(CollectionUtils.isEmpty(request.getEmails())) {
        	/*
        	 * IDMAPPS-2102
        	 */
        	//throw new BasicDataServiceException(ResponseCode.EMAIL_REQUIRED);
        }
        
        //figure out the emails to delete
        if(CollectionUtils.isNotEmpty(dbEntity.getEmailAddresses())) {
        	for(final Iterator<EmailAddressEntity> it = dbEntity.getEmailAddresses().iterator(); it.hasNext();) {
        		final EmailAddressEntity dbEmail = it.next();
        		boolean contains = false;
        		if(CollectionUtils.isNotEmpty(emailList)) {
        			for(final EmailAddressEntity email : emailList) {
        				if(StringUtils.equals(email.getId(), dbEmail.getId())) {
        					contains = true;
        				}
        			}
        		}
        		
        		if(!contains) {
        			it.remove();
        		}
        	}
        }
        
        //figure out the emails to delete
        if(CollectionUtils.isNotEmpty(dbEntity.getAddresses())) {
        	for(final Iterator<AddressEntity> it = dbEntity.getAddresses().iterator(); it.hasNext();) {
        		final AddressEntity dbAddress = it.next();
        		boolean contains = false;
        		if(CollectionUtils.isNotEmpty(addressList)) {
        			for(final AddressEntity address : addressList) {
        				if(StringUtils.equals(address.getId(), dbAddress.getId())) {
        					contains = true;
        				}
        			}
        		}
        		
        		if(!contains) {
        			it.remove();
        		}
        	}
        }
        
        //figure out the phones to delete
        if(CollectionUtils.isNotEmpty(dbEntity.getPhones())) {
        	for(final Iterator<PhoneEntity> it = dbEntity.getPhones().iterator(); it.hasNext();) {
        		final PhoneEntity dbPhone = it.next();
        		boolean contains = false;
        		if(CollectionUtils.isNotEmpty(phoneList)) {
        			for(final PhoneEntity phone : phoneList) {
        				if(StringUtils.equals(phone.getId(), dbPhone.getId())) {
        					contains = true;
        				}
        			}
        		}
        		
        		if(!contains) {
        			it.remove();
        		}
        	}
        }
        
        final PageTemplateAttributeToken token = pageTemplateService.getAttributesFromTemplate(request);
        if(token != null) {
			if(CollectionUtils.isNotEmpty(token.getSaveList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getSaveList()) {
					dbEntity.addUserAttribute(entity);
				}
			}
			if(CollectionUtils.isNotEmpty(token.getUpdateList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getUpdateList()) {
					dbEntity.updateUserAttribute(entity);
				}
			}

			if(CollectionUtils.isNotEmpty(token.getDeleteList())) {
				for(final UserAttributeEntity entity : (List<UserAttributeEntity>)token.getDeleteList()) {
					dbEntity.removeUserAttribute(entity.getId());
				}
			}
        }
        
        //pageTemplateService.saveTemplate(request);
        userManager.mergeUserFields(dbEntity, userEntity);
        userManager.updateUser(dbEntity);
        //pageTemplateService.saveTemplate(request);

		//addOrDeleteSupervisors(request.getSupervisors(), userId);

	}
	
	@Override
	public void validate(NewUserProfileRequestModel request) throws Exception {
		final UserEntity userEntity = userDozerConverter.convertToEntity(request.getUser(), true);
		if(StringUtils.isBlank(userEntity.getFirstName())) {
			throw new BasicDataServiceException(ResponseCode.FIRST_NAME_REQUIRED);
		}
		if(StringUtils.isBlank(userEntity.getLastName())) {
			throw new BasicDataServiceException(ResponseCode.LAST_NAME_REQUIRED);
		}
		/*
		 * IDMAPPS-1247
		if(CollectionUtils.isEmpty(request.getEmails())) {
			throw new BasicDataServiceException(ResponseCode.EMAIL_REQUIRED);
		}
		*/
        if (!sysConfiguration.isProvisionServiceFlag()) {
			if(CollectionUtils.isEmpty(request.getLoginList())) {
				throw new BasicDataServiceException(ResponseCode.LOGIN_REQUIRED);
			} 
			
			final List<LoginEntity> principalList = loginDozerConverter.convertToEntityList(request.getLoginList(), true);
			for(final LoginEntity loginEntity : principalList) {
				if(StringUtils.isBlank(loginEntity.getLogin())) {
					throw new BasicDataServiceException(ResponseCode.LOGIN_REQUIRED);
				} else if(loginDataService.getLoginByManagedSys(loginEntity.getLogin(), loginEntity.getManagedSysId()) != null) {
					throw new BasicDataServiceException(ResponseCode.LOGIN_EXISTS);
				}
			}
        }
		pageTemplateService.validate(request);
	}

	private void savePhones(final UserEntity userEntity, List<PhoneEntity> phoneList) {
        if(CollectionUtils.isNotEmpty(phoneList)) {
        	for(final PhoneEntity phone : phoneList) {
        		phone.setParent(userEntity);
        		if(StringUtils.isBlank(phone.getId())) {
        			userManager.addPhone(phone);
        		} else {
        			userManager.updatePhone(phone);
        		}
        	}
        }
	}
	
	private void saveEmails(final UserEntity userEntity, final List<EmailAddressEntity> emailList) {
		if(CollectionUtils.isNotEmpty(emailList)) {
        	for(final EmailAddressEntity email : emailList) {
        		email.setParent(userEntity);
        		if(StringUtils.isBlank(email.getId())) {
        			userManager.addEmailAddress(email);
        		} else {
        			userManager.updateEmailAddress(email);
        		}
        	}
        }
	}
	
	private void saveAddresses(final UserEntity userEntity, final List<AddressEntity> addressList) {
		if(CollectionUtils.isNotEmpty(addressList)) {
        	for(final AddressEntity address : addressList) {
        		address.setParent(userEntity);
        		if(StringUtils.isBlank(address.getId())) {
        			userManager.addAddress(address);
        		} else {
        			userManager.updateAddress(address);
        		}
        	}
        }
	}
/*
private void addOrDeleteSupervisors(final List<User> supervisors, String userId) {
		if (CollectionUtils.isNotEmpty(supervisors))
			for (final User supervisor : supervisors)
				if (supervisor.getId() != null)
					if (supervisor.getOperation().equals(AttributeOperationEnum.ADD))
						userManager.addSuperior(supervisor.getId(), userId);
					else if(supervisor.getOperation().equals(AttributeOperationEnum.DELETE))
						userManager.removeSupervisor(supervisor.getId(), userId);
	}*/

/*	private void addOrDeleteOrganizations(final UserEntity userEntity, final List<OrganizationUserDTO> organizations, String userId){
		if (CollectionUtils.isNotEmpty(organizations)) {
			for (OrganizationUserDTO o : organizations) {
				AttributeOperationEnum operation = o.getOperation();
				if (operation == AttributeOperationEnum.ADD) {
					if (userEntity.getOrganizationUser() == null)
						userEntity.setOrganizationUser(new HashSet<OrganizationUserEntity>());
					userEntity.getOrganizationUser().add(new OrganizationUserEntity(userEntity.getId(), o.getOrganization().getId(), o.getMdTypeId()));
				} else if (operation == AttributeOperationEnum.DELETE) {
					Set<OrganizationUserEntity> affiliations = userEntity.getOrganizationUser();
					for (OrganizationUserEntity a : affiliations) {
						if (a.getOrganization() != null && org.mule.util.StringUtils.equals(o.getOrganization().getId(), a.getOrganization().getId())) {
							userEntity.getOrganizationUser().remove(a);
							break;
						}
					}

				}
			}
		}
	}*/


    @Override
    @Transactional(readOnly = true)
    public ProfilePicture getProfilePictureById(String picId) {
        ProfilePictureEntity pictureEntity = profilePictureDAO.findById(picId);
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setId(pictureEntity.getId());
        profilePicture.setName(pictureEntity.getName());
        profilePicture.setPicture(pictureEntity.getPicture());
        if(pictureEntity.getUser() != null) {
            profilePicture.setUser(userDozerConverter.convertToDTO(pictureEntity.getUser(),true));
        }
        return profilePicture;
    }

    @Override
    public ProfilePictureEntity getProfilePictureByUserId(String userId) {
        return profilePictureDAO.getByUserId(userId);
    }

    @Override
    public void saveProfilePicture(ProfilePictureEntity pic) throws Exception {
        if(pic == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        } else if (pic.getUser() == null) {
            throw new BasicDataServiceException(ResponseCode.USER_NOT_SET);
        } else if (pic.getPicture() == null) {
            throw new BasicDataServiceException(ResponseCode.VALUE_REQUIRED);
        }
        profilePictureDAO.save(pic);
    }

    @Override
    public void deleteProfilePictureById(String picId) throws Exception {
        if(picId == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        profilePictureDAO.deleteById(picId);
    }

    @Override
    public void deleteProfilePictureByUserId(String userId) throws Exception {
        if(userId == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        profilePictureDAO.deleteByUserId(userId);
    }

}
