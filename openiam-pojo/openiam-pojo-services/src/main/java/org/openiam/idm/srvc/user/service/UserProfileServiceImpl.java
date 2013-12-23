package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.validator.EntityValidator;
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

	@Autowired
    @Qualifier("userManager")
	private UserDataService userManager;
	
	@Autowired
	private MailService mailService;
	
    @Autowired
    private MetadataElementTemplateService pageTemplateService;
    
    @Autowired
    private LoginDozerConverter loginDozerConverter;
    
    @Autowired
    private LoginDAO loginDAO;
    
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
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
	
    // @Autowired
    // @Qualifier("sysConfiguration")
    // protected SysConfiguration sysConfiguration;

    @Value("${org.openiam.provision.service.flag}")
    protected boolean provisionServiceFlag = true;

	@Override
	public void saveUserProfile(UserProfileRequestModel request) throws Exception {
		final UserEntity userEntity = userDozerConverter.convertToEntity(request.getUser(), true);
		entityValidator.isValid(userEntity);
        pageTemplateService.validate(request);
		
        final UserEntity dbEntity = userManager.getUser(request.getUser().getUserId());
        
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
        	throw new BasicDataServiceException(ResponseCode.EMAIL_REQUIRED);
        }
        
        //figure out the emails to delete
        if(CollectionUtils.isNotEmpty(dbEntity.getEmailAddresses())) {
        	for(final Iterator<EmailAddressEntity> it = dbEntity.getEmailAddresses().iterator(); it.hasNext();) {
        		final EmailAddressEntity dbEmail = it.next();
        		boolean contains = false;
        		if(CollectionUtils.isNotEmpty(emailList)) {
        			for(final EmailAddressEntity email : emailList) {
        				if(StringUtils.equals(email.getEmailId(), dbEmail.getEmailId())) {
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
        				if(StringUtils.equals(address.getAddressId(), dbAddress.getAddressId())) {
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
        				if(StringUtils.equals(phone.getPhoneId(), dbPhone.getPhoneId())) {
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
        		for(final UserAttributeEntity entity : token.getSaveList()) {
        			dbEntity.addUserAttribute(entity);
        		}
        	}
        	if(CollectionUtils.isNotEmpty(token.getUpdateList())) {
        		for(final UserAttributeEntity entity : token.getUpdateList()) {
        			dbEntity.updateUserAttribute(entity);
        		}
        	}
        	
        	if(CollectionUtils.isNotEmpty(token.getDeleteList())) {
        		for(final UserAttributeEntity entity : token.getDeleteList()) {
        			dbEntity.removeUserAttribute(entity.getId());
        		}
        	}
        }
        
        //pageTemplateService.saveTemplate(request);
        userManager.mergeUserFields(dbEntity, userEntity);
        userManager.updateUser(dbEntity);
        //pageTemplateService.saveTemplate(request);
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
        if (!provisionServiceFlag) {
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
        		if(StringUtils.isBlank(phone.getPhoneId())) {
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
        		if(StringUtils.isBlank(email.getEmailId())) {
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
        		if(StringUtils.isBlank(address.getAddressId())) {
        			userManager.addAddress(address);
        		} else {
        			userManager.updateAddress(address);
        		}
        	}
        }
	}
}
