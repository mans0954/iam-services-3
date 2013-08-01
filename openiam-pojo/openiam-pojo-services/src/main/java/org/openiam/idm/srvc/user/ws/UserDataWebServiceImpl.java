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

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.AddressDozerConverter;
import org.openiam.dozer.converter.EmailAddressDozerConverter;
import org.openiam.dozer.converter.PhoneDozerConverter;
import org.openiam.dozer.converter.SupervisorDozerConverter;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.dozer.converter.UserNoteDozerConverter;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserNote;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

/**
 * @author suneet
 * 
 */
@Service("userWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.user.ws.UserDataWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/user/service",
            serviceName = "UserDataWebService",
            portName = "UserDataWebServicePort")
public class UserDataWebServiceImpl implements UserDataWebService, MuleContextAware {

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

    @Autowired
    private MailService mailService;

    private MuleContext muleContext;

    @Autowired
    private UserProfileService userProfileService;

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
    }

    @Value("${openiam.service_base}")
    private String serviceHost;
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;

    @Override
    public Response addAddress(final Address val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            final AddressEntity entity = addressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.addAddress(entity);
        } catch (BasicDataServiceException e) {
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
    public Response addAddressSet(Set<Address> adrList) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (CollectionUtils.isEmpty(adrList)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            String parentId = adrList.iterator().next().getParentId();
            final List<AddressEntity> entityList = addressDozerConverter.convertToEntityList(new ArrayList<Address>(adrList), true);
            for (AddressEntity entity : entityList) {
                UserEntity user = new UserEntity();
                user.setUserId(parentId);
                entity.setParent(user);
            }
            userManager.addAddressSet(entityList);
        } catch (BasicDataServiceException e) {
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
    public Response addAttribute(final UserAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attribute == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (attribute.getUserId() == null)
                throw new BasicDataServiceException(ResponseCode.USER_NOT_SET);
            if (attribute.getName() == null || attribute.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.USER_ATTRIBUTE_NAME_NOT_SET);

            final UserAttributeEntity entity = userAttributeDozerConverter.convertToEntity(attribute, true);
            userManager.addAttribute(entity);
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
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
    public Response addEmailAddress(EmailAddress val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.addEmailAddress(entity);
        } catch (BasicDataServiceException e) {
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
    public Response addEmailAddressSet(final Set<EmailAddress> adrList) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (CollectionUtils.isEmpty(adrList)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            String parentId = adrList.iterator().next().getParentId();
            final List<EmailAddressEntity> entityList = emailAddressDozerConverter.convertToEntityList(new ArrayList<EmailAddress>(adrList), true);
            for (EmailAddressEntity e : entityList) {
                UserEntity user = new UserEntity();
                user.setUserId(parentId);
                e.setParent(user);
            }
            userManager.addEmailAddressSet(entityList);
        } catch (BasicDataServiceException e) {
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
    public Response addNote(final UserNote note) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (note == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final UserNoteEntity entity = userNoteDozerConverter.convertToEntity(note, true);
            userManager.addNote(entity);
        } catch (BasicDataServiceException e) {
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
    public Response addPhone(Phone val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.addPhone(entity);
        } catch (BasicDataServiceException e) {
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
    public Response addPhoneSet(final Set<Phone> phoneList) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (CollectionUtils.isEmpty(phoneList)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            String parentId = phoneList.iterator().next().getParentId();
            final List<PhoneEntity> entityList = phoneDozerConverter.convertToEntityList(new ArrayList<Phone>(phoneList), true);

            for (PhoneEntity e : entityList) {
                UserEntity user = new UserEntity();
                user.setUserId(parentId);
                e.setParent(user);
            }
            userManager.addPhoneSet(entityList);
        } catch (BasicDataServiceException e) {
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
    public Response addSupervisor(final Supervisor supervisor) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (supervisor == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final SupervisorEntity entity = supervisorDozerConverter.convertToEntity(supervisor, true);
            userManager.addSupervisor(entity);
        } catch (BasicDataServiceException e) {
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
    public Response addUser(User user) throws Exception {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final UserEntity entity = userDozerConverter.convertToEntity(user, true);
            userManager.addUser(entity);
        } catch (BasicDataServiceException e) {
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
    public List<Address> getAddressList(String userId) {
        return this.getAddressListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    public List<Address> getAddressListByPage(String userId, Integer size, Integer from) {
        final List<AddressEntity> adrList = userManager.getAddressList(userId, size, from);
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
    public EmailAddress getEmailAddressById(String addressId) {
        final EmailAddressEntity adr = userManager.getEmailAddressById(addressId);
        return emailAddressDozerConverter.convertToDTO(adr, false);
    }

    @Override
    public List<EmailAddress> getEmailAddressList(String userId) {
        return this.getEmailAddressListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    public List<EmailAddress> getEmailAddressListByPage(String userId, Integer size, Integer from) {
        final List<EmailAddressEntity> adr = userManager.getEmailAddressList(userId, size, from);
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
    public List<Phone> getPhoneList(String userId) {
        return getPhoneListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    public List<Phone> getPhoneListByPage(String userId, Integer size, Integer from) {
        final List<PhoneEntity> phoneList = userManager.getPhoneList(userId, size, from);
        return phoneDozerConverter.convertToDTOList(phoneList, false);
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
        return supervisorDozerConverter.convertToDTOList(sup, true);
    }

    @Override
    public Supervisor findSupervisor(String superiorId, String subordinateId) {
        return supervisorDozerConverter.convertToDTO(
                userManager.findSupervisor(superiorId, subordinateId), true);
    }

    @Override
    public List<User> getSuperiors(String userId, Integer from, Integer size) {
        final List<UserEntity> superiors = userManager.getSuperiors(userId, from, size);
        return userDozerConverter.convertToDTOList(superiors, true);
    }

    @Override
    public int getSuperiorsCount(String userId) {
        return userManager.getSuperiorsCount(userId);
    }

    @Override
    public List<User> getSubordinates(String userId, Integer from, Integer size) {
        final List<UserEntity> subordinates = userManager.getSubordinates(userId, from, size);
        return userDozerConverter.convertToDTOList(subordinates, true);
    }

    @Override
    public int getSubordinatesCount(String userId) {
        return userManager.getSubordinatesCount(userId);
    }

    @Override
    public List<User> findPotentialSupSubs(UserSearchBean userSearchBean, Integer from, Integer size) {
        return userDozerConverter.convertToDTOList(userManager.findPotentialSupSubs(userSearchBean, from, size), true);
    }

    @Override
    public int findPotentialSupSubsCount(UserSearchBean userSearchBean) {
        return userManager.findPotentialSupSubsCount(userSearchBean);
    }

    @Override
    public Response addSuperior(String requesterId, String userId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.equals(requesterId, userId)) {
                throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
            }
            User superior = getUserWithDependent(userId, null, true);
            User subordinate = getUserWithDependent(requesterId, userId, true);
            if (superior == null || subordinate == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            SupervisorEntity found = userManager.findSupervisor(superior.getUserId(), subordinate.getUserId());
            if (found != null) {
                throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
            }
            SupervisorEntity contrary = userManager.findSupervisor(subordinate.getUserId(), superior.getUserId());
            if (contrary != null) {
                throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
            }
            userManager.addSuperior(userId, requesterId);

        } catch (BasicDataServiceException e) {
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
    public Response removeSuperior(String requesterId, String userId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            SupervisorEntity supervisor = userManager.findSupervisor(userId, requesterId);
            if (supervisor == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            userManager.removeSupervisor(supervisor.getOrgStructureId());

        } catch (BasicDataServiceException e) {
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
    public User getUserByName(String firstName, String lastName) {
        final UserEntity user = userManager.getUserByName(firstName, lastName);
        return userDozerConverter.convertToDTO(user, false);
    }

    @Override
    public User getUserWithDependent(String id, String requestorId, boolean dependants) {
        final UserEntity user = userManager.getUser(id, requestorId);
        return userDozerConverter.convertToDTO(user, dependants);
    }

    @Override
    public User getUserByPrincipal(String securityDomain, String principal, String managedSysId, boolean dependants) {
        final UserEntity user = userManager.getUserByPrincipal(securityDomain, principal, managedSysId, dependants);
        return userDozerConverter.convertToDTO(user, dependants);
    }

    @Override
    public Response removeAddress(String addressId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (addressId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            userManager.removeAddress(addressId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAllAddresses(String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            userManager.removeAllAddresses(userId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAllAttributes(String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            userManager.removeAllAttributes(userId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAllEmailAddresses(String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            userManager.removeAllEmailAddresses(userId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAllNotes(String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeAllNotes(userId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAllPhones(String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeAllPhones(userId);
        } catch (BasicDataServiceException e) {
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
    public Response removeAttribute(String attrId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attrId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeAttribute(attrId);
        } catch (BasicDataServiceException e) {
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
    public Response removeEmailAddress(String emailId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (emailId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeEmailAddress(emailId);
        } catch (BasicDataServiceException e) {
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
    public Response removeNote(String noteId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (noteId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeNote(noteId);
        } catch (BasicDataServiceException e) {
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
    public Response removePhone(String phoneId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (phoneId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removePhone(phoneId);
        } catch (BasicDataServiceException e) {
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
    public Response removeSupervisor(String supervisorId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (supervisorId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeSupervisor(supervisorId);
        } catch (BasicDataServiceException e) {
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
    public Response removeUser(String id) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (id == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeUser(id);
        } catch (BasicDataServiceException e) {
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
    public List<User> searchByDelegationProperties(final DelegationFilterSearch search) {
        final List<UserEntity> userList = userManager.searchByDelegationProperties(search);
        return userDozerConverter.convertToDTOList(userList, false);

    }

    @Override
    public List<User> findBeans(UserSearchBean userSearchBean, int from, int size) {
        final List<UserEntity> userList = userManager.findBeans(userSearchBean, from, size);
        return userDozerConverter.convertToDTOList(userList, userSearchBean.isDeepCopy());
    }

    @Override
    public int count(UserSearchBean userSearchBean) {
        return userManager.count(userSearchBean);
    }

    @Override
    public Response updateAddress(Address val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final AddressEntity entity = addressDozerConverter.convertToEntity(val, false);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.updateAddress(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateAttribute(UserAttribute attribute) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (attribute == null || attribute.getId() == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (attribute.getUserId() == null)
                throw new BasicDataServiceException(ResponseCode.USER_NOT_SET);
            if (attribute.getName() == null || attribute.getName().trim().isEmpty())
                throw new BasicDataServiceException(ResponseCode.USER_ATTRIBUTE_NAME_NOT_SET);

            final UserAttributeEntity entity = userAttributeDozerConverter.convertToEntity(attribute, false);
            userManager.updateAttribute(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateEmailAddress(EmailAddress val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.updateEmailAddress(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateNote(UserNote note) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (note == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final UserNoteEntity entity = userNoteDozerConverter.convertToEntity(note, true);
            userManager.updateNote(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updatePhone(Phone val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setUserId(val.getParentId());
            entity.setParent(user);
            userManager.updatePhone(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateSupervisor(Supervisor supervisor) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (supervisor == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final SupervisorEntity entity = supervisorDozerConverter.convertToEntity(supervisor, true);
            userManager.updateSupervisor(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateUser(User user) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final UserEntity entity = userDozerConverter.convertToEntity(user, true);
            userManager.updateUser(entity);
        } catch (BasicDataServiceException e) {
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
    public Response updateUserWithDependent(User user, boolean dependency) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final UserEntity entity = userDozerConverter.convertToEntity(user, dependency);
            userManager.updateUserWithDependent(entity, dependency);
        } catch (BasicDataServiceException e) {
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
    public List<User> getUsersForResource(final String resourceId, String requesterId, final int from, final int size) {
        final List<UserEntity> entityList = userManager.getUsersForResource(resourceId, requesterId, from, size);
        final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
        return userList;
    }

    @Override
    public int getNumOfUsersForResource(final String resourceId, String requesterId) {
        return userManager.getNumOfUsersForResource(resourceId, requesterId);
    }

    @Override
    public List<User> getUsersForGroup(final String groupId, String requesterId, final int from, final int size) {
        final List<UserEntity> entityList = userManager.getUsersForGroup(groupId, requesterId, from, size);
        final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
        return userList;
    }

    @Override
    public int getNumOfUsersForGroup(final String groupId, String requesterId) {
        return userManager.getNumOfUsersForGroup(groupId, requesterId);
    }

    @Override
    public List<User> getUsersForRole(final String roleId, String requesterId, final int from, final int size) {
        final List<UserEntity> entityList = userManager.getUsersForRole(roleId, requesterId, from, size);
        final List<User> userList = userDozerConverter.convertToDTOList(entityList, false);
        return userList;
    }

    @Override
    public int getNumOfUsersForRole(final String roleId, String requesterId) {
        return userManager.getNumOfUsersForRole(roleId, requesterId);
    }

    @Override
    public UserResponse saveUserInfo(final User user, final Supervisor supervisor) {
        final UserResponse response = new UserResponse(ResponseStatus.SUCCESS);
        try {
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (user.getUserId() == null) {
                // create new user, need to merge user objects
                List<Login> principalList = new ArrayList<Login>();
                Login principal = new Login();
                principal.setLogin(user.getLogin());
                principal.setPassword(user.getPassword());
                principalList.add(principal);
                user.setPrincipalList(principalList);

                Set<EmailAddress> emailAddressList = new HashSet<EmailAddress>();

                EmailAddress ea = new EmailAddress();
                ea.setEmailAddress(user.getEmail());
                ea.setIsDefault(true);
                emailAddressList.add(ea);
                user.setEmailAddresses(emailAddressList);
            }

            final UserEntity userEntity = userDozerConverter.convertToEntity(user, true);
            SupervisorEntity supervisorEntity = null;
            if (supervisor != null)
                supervisorEntity = supervisorDozerConverter.convertToEntity(supervisor, true);
            String userId = userManager.saveUserInfo(userEntity, supervisorEntity);
            user.setUserId(userId);

            if (user.getNotifyUserViaEmail()) {
                sendCredentialsToUser(user, user.getLogin(), user.getPassword());
            }

            response.setUser(user);
        } catch (BasicDataServiceException e) {
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
    public Response deleteUser(final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.deleteUser(userId);
        } catch (BasicDataServiceException e) {
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
    public Response enableDisableUser(final String userId, final UserStatusEnum secondaryStatus) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.enableDisableUser(userId, secondaryStatus);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    public Response activateUser(final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.activateUser(userId);
        } catch (BasicDataServiceException e) {
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
    public Integer getNumOfEmailsForUser(String userId) {
        return userManager.getNumOfEmailsForUser(userId);
    }

    @Override
    public Integer getNumOfAddressesForUser(String userId) {
        return userManager.getNumOfAddressesForUser(userId);
    }

    @Override
    public Integer getNumOfPhonesForUser(String userId) {
        return userManager.getNumOfPhonesForUser(userId);
    }

    private void sendCredentialsToUser(User user, String identity, String password) throws BasicDataServiceException {

        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getUserId());
        notificationRequest.setNotificationType("NEW_USER_EMAIL");
        notificationRequest.getParamList().add(new NotificationParam("IDENTITY", identity));
        notificationRequest.getParamList().add(new NotificationParam("PSWD", password));
        notificationRequest.getParamList().add(new NotificationParam("USER_ID", user.getUserId()));
        final boolean sendEmailResult = mailService.sendNotification(notificationRequest);
        if (!sendEmailResult) {
            throw new BasicDataServiceException(ResponseCode.SEND_EMAIL_FAILED);
        }

    }

    @Override
    public List<UserAttribute> getUserAttributes(final String userId) {
        final UserEntity user = userManager.getUser(userId, null);
        final List<UserAttributeEntity> attributes = (user != null && user.getUserAttributes() != null) ? new ArrayList<UserAttributeEntity>(user
                        .getUserAttributes().values()) : null;
        return (attributes != null) ? userAttributeDozerConverter.convertToDTOList(attributes, true) : null;
    }

    @Override
    public SaveTemplateProfileResponse saveUserProfile(final UserProfileRequestModel request) {
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
        try {
            if (request == null || request.getUser() == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            userProfileService.saveUserProfile(request);
        } catch (PageTemplateException e) {
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }

    /*
    @Override
    public SaveTemplateProfileResponse createNewUserProfile(final NewUserProfileRequestModel request) {
        final SaveTemplateProfileResponse response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS);
        try {
            if (request == null || request.getUser() == null || CollectionUtils.isEmpty(request.getLoginList())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }

            final CreateUserToken token = userProfileService.createNewUserProfile(request);
            final UserEntity userEntity = token.getUser();
            final String login = token.getLogin();
            final String plaintextPassword = token.getPassword();
            response.setUserId(userEntity.getUserId());
            response.setPlaintextPassword(plaintextPassword);
            response.setLogin(login);
        } catch (PageTemplateException e) {
            response.setCurrentValue(e.getCurrentValue());
            response.setElementName(e.getElementName());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
    */

    @Override
    public Response sendNewUserEmail(final String userId, final String password, final String login) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            final NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setUserId(userId);
            notificationRequest.setNotificationType("NEW_USER_EMAIL");
            notificationRequest.getParamList().add(new NotificationParam("IDENTITY", login));
            notificationRequest.getParamList().add(new NotificationParam("PSWD", password));
            notificationRequest.getParamList().add(new NotificationParam("USER_ID", userId));
            final boolean sendEmailResult = mailService.sendNotification(notificationRequest);
            if (!sendEmailResult) {
                throw new BasicDataServiceException(ResponseCode.SEND_EMAIL_FAILED);
            }
        } catch (BasicDataServiceException e) {
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
    public List<User> getByManagedSystem(String mSysId) {
        List<UserEntity> result = userManager.getUsersForMSys(mSysId);
        return result == null ? new ArrayList<User>() : userDozerConverter.convertToDTOList(result, true);
    }

    @Override
    public Response acceptITPolicy(final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            final UserEntity user = userManager.getUser(userId, null);
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            user.setDateITPolicyApproved(new Date());
            userManager.updateUser(user);

        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}
