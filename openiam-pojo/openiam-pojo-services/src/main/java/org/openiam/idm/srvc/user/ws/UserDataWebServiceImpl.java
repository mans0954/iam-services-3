/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.user.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.DozerUtils;
import org.openiam.dozer.converter.AddressDozerConverter;
import org.openiam.dozer.converter.EmailAddressDozerConverter;
import org.openiam.dozer.converter.PhoneDozerConverter;
import org.openiam.dozer.converter.SupervisorDozerConverter;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.dozer.converter.UserNoteDozerConverter;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.ws.AddressListResponse;
import org.openiam.idm.srvc.continfo.ws.AddressMapResponse;
import org.openiam.idm.srvc.continfo.ws.AddressResponse;
import org.openiam.idm.srvc.continfo.ws.EmailAddressListResponse;
import org.openiam.idm.srvc.continfo.ws.EmailAddressMapResponse;
import org.openiam.idm.srvc.continfo.ws.EmailAddressResponse;
import org.openiam.idm.srvc.continfo.ws.PhoneListResponse;
import org.openiam.idm.srvc.continfo.ws.PhoneMapResponse;
import org.openiam.idm.srvc.continfo.ws.PhoneResponse;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author suneet
 *
 */
@Service("userWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.user.ws.UserDataWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/user/service", 
		serviceName = "UserDataWebService",
		portName = "UserDataWebServicePort")
public class UserDataWebServiceImpl implements UserDataWebService {
	
	private static Logger log = Logger.getLogger(UserDataWebServiceImpl.class);
	
	@Autowired
    @Qualifier("userManager")
	private UserDataService userManager;
	
    @Autowired
    private UserDozerConverter userDozerConverter;
    
    @Autowired
    private SupervisorDozerConverter supervisorDozerConverter;
    
    @Autowired
    private EmailAddressDozerConverter emailAddressDozerConverter;
    
    @Autowired
    private UserNoteDozerConverter userNoteDozerConverter;
    
    @Autowired
    private AddressDozerConverter addressDozerConverter;
    
    @Autowired
    private UserAttributeDozerConverter userAttributeDozerConverter;
    
    @Autowired
    private PhoneDozerConverter phoneDozerConverter;
	
    @Override
	public Response addAddress(final Address val) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			final AddressEntity entity = addressDozerConverter.convertToEntity(val, true);
			userManager.addAddress(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addAddressSet(Set<Address> adrList) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(CollectionUtils.isEmpty(adrList)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<AddressEntity> entityList = addressDozerConverter.convertToEntityList(new ArrayList<Address>(adrList), true);
			userManager.addAddressSet(entityList);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addAttribute(final UserAttribute attribute) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserAttributeEntity entity = userAttributeDozerConverter.convertToEntity(attribute, true);
			userManager.addAttribute(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addEmailAddress(EmailAddress val) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
			userManager.addEmailAddress(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addEmailAddressSet(final Set<EmailAddress> adrList) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(CollectionUtils.isEmpty(adrList)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<EmailAddressEntity> entityList = emailAddressDozerConverter.convertToEntityList(new ArrayList<EmailAddress>(adrList), true);
			userManager.addEmailAddressSet(entityList);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addNote(final UserNote note) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(note == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserNoteEntity entity = userNoteDozerConverter.convertToEntity(note, true);
			userManager.addNote(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addPhone(Phone val) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
			userManager.addPhone(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addPhoneSet(final Set<Phone> phoneList) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(CollectionUtils.isEmpty(phoneList)) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final List<PhoneEntity> entityList = phoneDozerConverter.convertToEntityList(new ArrayList<Phone>(phoneList), true);
			userManager.addPhoneSet(entityList);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addSupervisor(final Supervisor supervisor) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(supervisor == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final SupervisorEntity entity = supervisorDozerConverter.convertToEntity(supervisor, true);
			userManager.addSupervisor(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addUser(User user) throws Exception {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(user == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserEntity entity = userDozerConverter.convertToEntity(user, true);
			userManager.addUser(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response addUserWithDependent(User user, boolean dependency) throws Exception {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(user == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserEntity entity = userDozerConverter.convertToEntity(user, true);
			userManager.addUserWithDependent(entity, dependency);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public List<User> findUserByOrganization(final String orgId) {
		final List<UserEntity> entityList = userManager.findUserByOrganization(orgId);
		return userDozerConverter.convertToDTOList(entityList, false);
	}

    @Override
	public List<User> findUsersByLastUpdateRange(final Date startDate, final Date endDate) {
		final List<UserEntity> entityList = userManager.findUsersByLastUpdateRange(startDate, endDate);
		return userDozerConverter.convertToDTOList(entityList, false);
	}

    @Override
	public List<User> findUsersByStatus(String status) {
		final List<UserEntity> entityList = userManager.findUsersByStatus(UserStatusEnum.valueOf(status));
		return userDozerConverter.convertToDTOList(entityList, false);
	}

    @Override
	public Address getAddressById(String addressId) {
		final AddressEntity adr = userManager.getAddressById(addressId);
		return addressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public Address getAddressByName(String userId, String addressName) {
		final AddressEntity adr = userManager.getAddressByName(userId, addressName);
		return addressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public List<Address> getAddressList(String userId) {
		final List<AddressEntity> adrList = userManager.getAddressList(userId);
		return addressDozerConverter.convertToDTOList(adrList, false);
	}

    @Override
	public List<UserNote> getAllNotes(String userId) {
		final List<UserNoteEntity> entityList = userManager.getAllNotes(userId);
		return userNoteDozerConverter.convertToDTOList(entityList, false);
	}

    @Override
	public UserAttribute getAttribute(String attrId) {
		final UserAttributeEntity userAttr = userManager.getAttribute(attrId);
		return userAttributeDozerConverter.convertToDTO(userAttr, false);
	}

    @Override
	public Address getDefaultAddress(String userId) {
		final AddressEntity adr = userManager.getDefaultAddress(userId);
		return addressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public EmailAddress getDefaultEmailAddress(String userId) {
		final EmailAddressEntity adr = userManager.getDefaultEmailAddress(userId);
		return emailAddressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public Phone getDefaultPhone(String userId) {
		final PhoneEntity ph = userManager.getDefaultPhone(userId);
		return phoneDozerConverter.convertToDTO(ph, false);
	}

    @Override
	public EmailAddress getEmailAddressById(String addressId) {
		final EmailAddressEntity adr = userManager.getEmailAddressById(addressId);
		return emailAddressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public EmailAddress getEmailAddressByName(String userId,
			String addressName) {
		final EmailAddressEntity adr = userManager.getEmailAddressByName(userId, addressName);
		return emailAddressDozerConverter.convertToDTO(adr, false);
	}

    @Override
	public List<EmailAddress> getEmailAddressList(String userId) {
		final List<EmailAddressEntity> adr = userManager.getEmailAddressList(userId);
		return emailAddressDozerConverter.convertToDTOList(adr, false);
	}

    @Override
	public List<Supervisor> getEmployees(String supervisorId) {
		final List<SupervisorEntity> sup = userManager.getEmployees(supervisorId);
		return supervisorDozerConverter.convertToDTOList(sup, false);
	}

    @Override
	public UserNote getNote(String noteId) {
		final UserNoteEntity note = userManager.getNote(noteId);
		return userNoteDozerConverter.convertToDTO(note, false);
	}

    @Override
	public Phone getPhoneById(String addressId) {
		final PhoneEntity ph = userManager.getPhoneById(addressId);
		return phoneDozerConverter.convertToDTO(ph, false);
	}

    @Override
	public Phone getPhoneByName(String userId, String addressName) {
		final PhoneEntity ph = userManager.getPhoneByName(userId, addressName);
		return phoneDozerConverter.convertToDTO(ph, false);
	}

    @Override
	public List<Phone> getPhoneList(String userId) {
		final List<PhoneEntity> ph = userManager.getPhoneList(userId);
		return phoneDozerConverter.convertToDTOList(ph, false);
	}

    @Override
	public Supervisor getPrimarySupervisor(String employeeId) {
		final SupervisorEntity sup = userManager.getPrimarySupervisor(employeeId);
		return supervisorDozerConverter.convertToDTO(sup, false);
	}

    @Override
	public Supervisor getSupervisor(String supervisorObjId) {
		final SupervisorEntity sup = userManager.getSupervisor(supervisorObjId);
		return supervisorDozerConverter.convertToDTO(sup, false);
	}

    @Override
	public List<Supervisor> getSupervisors(String employeeId) {
		final List<SupervisorEntity> sup = userManager.getSupervisors(employeeId);
		return supervisorDozerConverter.convertToDTOList(sup, false);
	}

    @Override
	public User getUserByName(String firstName, String lastName) {
		final UserEntity user = userManager.getUserByName(firstName, lastName);
		return userDozerConverter.convertToDTO(user, false);
	}

    @Override
	public User getUserWithDependent(String id, boolean dependants) {
		final UserEntity user = userManager.getUser(id);
		return userDozerConverter.convertToDTO(user, dependants);
	}
	
    @Override
	public User getUserByPrincipal(
			String securityDomain, 
			String principal, 
			String managedSysId, 
			boolean dependants) {
		final UserEntity user = userManager.getUserByPrincipal(securityDomain, principal, managedSysId, dependants);
		return userDozerConverter.convertToDTO(user, dependants);
	}

    @Override
	public Response removeAddress(String addressId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(addressId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			userManager.removeAddress(addressId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAllAddresses(String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			userManager.removeAllAddresses(userId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAllAttributes(String userId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			userManager.removeAllAttributes(userId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAllEmailAddresses(String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			userManager.removeAllEmailAddresses(userId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAllNotes(String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeAllNotes(userId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAllPhones(String userId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(userId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeAllPhones(userId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeAttribute(String attrId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attrId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeAttribute(attrId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeEmailAddress(String emailId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(emailId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeEmailAddress(emailId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeNote(String noteId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(noteId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeNote(noteId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removePhone(String phoneId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(phoneId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removePhone(phoneId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeSupervisor(String supervisorId) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(supervisorId == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeSupervisor(supervisorId);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response removeUser(String id) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(id == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			userManager.removeUser(id);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
    @Deprecated
	public List<User> search(UserSearch search) {
		final List<UserEntity> userList = userManager.search(search);
		return userDozerConverter.convertToDTOList(userList, false);
	}

    @Override
    public List<User> searchByDelegationProperties(final DelegationFilterSearch search) {
    	final List<UserEntity> userList = userManager.searchByDelegationProperties(search);
    	return userDozerConverter.convertToDTOList(userList, false);

    }
    
    @Override
    public List<User> findBeans(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean,
                         @WebParam(name = "from", targetNamespace = "") int from,
                         @WebParam(name = "size", targetNamespace = "") int size){
        final List<UserEntity> userList = userManager.findBeans(userSearchBean, from, size);
        return userDozerConverter.convertToDTOList(userList, false);
    }

    @Override
    public int count(UserSearchBean userSearchBean){
        return userManager.count(userSearchBean);
    }

    @Override
	public Response updateAddress(Address val) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final AddressEntity entity = addressDozerConverter.convertToEntity(val, false);
			userManager.updateAddress(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateAttribute(UserAttribute attribute) {		
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(attribute == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserAttributeEntity entity = userAttributeDozerConverter.convertToEntity(attribute, false);
			userManager.updateAttribute(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateEmailAddress(EmailAddress val) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
			userManager.updateEmailAddress(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateNote(UserNote note) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(note == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserNoteEntity entity = userNoteDozerConverter.convertToEntity(note, true);
			userManager.updateNote(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updatePhone(Phone val) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(val == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
			userManager.updatePhone(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateSupervisor(Supervisor supervisor) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(supervisor == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final SupervisorEntity entity = supervisorDozerConverter.convertToEntity(supervisor, true);
			userManager.updateSupervisor(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateUser(User user) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(user == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserEntity entity = userDozerConverter.convertToEntity(user, true);
			userManager.updateUser(entity);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

    @Override
	public Response updateUserWithDependent(User user, boolean dependency) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(user == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			}
			
			final UserEntity entity = userDozerConverter.convertToEntity(user, dependency);
			userManager.updateUserWithDependent(entity, dependency);
		} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't perform operation", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
		return response;
	}

	@Override
	public List<User> getUsersForResource(final String resourceId, final int from, final int size) {
		final List<UserEntity> entityList = userManager.getUsersForResource(resourceId, from, size);
		final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
		return userList;
	}

	@Override
	public int getNumOfUsersForResource(final String resourceId) {
		return userManager.getNumOfUsersForResource(resourceId);
	}

	@Override
	public List<User> getUsersForGroup(final String groupId, final int from, final int size) {
		final List<UserEntity> entityList = userManager.getUsersForGroup(groupId, from, size);
		final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
		return userList;
	}

	@Override
	public int getNumOfUsersForGroup(final String groupId) {
		return userManager.getNumOfUsersForGroup(groupId);
	}

	@Override
	public List<User> getUsersForRole(final String roleId, final int from, final int size) {
		final List<UserEntity> entityList = userManager.getUsersForRole(roleId, from, size);
		final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
		return userList;
	}

	@Override
	public int getNumOfUsersForRole(final String roleId) {
		return userManager.getNumOfUsersForRole(roleId);
	}

}
