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

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.searchbeans.PotentialSupSubSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.meta.exception.PageTemplateException;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationUserEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.service.UserProfileService;
import org.openiam.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 */
@Service("userWS")
@WebService(endpointInterface = "org.openiam.idm.srvc.user.ws.UserDataWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/user/service",
        serviceName = "UserDataWebService",
        portName = "UserDataWebServicePort")
public class UserDataWebServiceImpl implements UserDataWebService {

    private static Logger log = Logger.getLogger(UserDataWebServiceImpl.class);

    @Autowired
    private UserDataService userDataService;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    protected AuditLogService auditLogService;

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
    private LanguageDozerConverter languageConverter;

    @Autowired
    private ProfilePictureDozerConverter profilePictureDozerConverter;

    @Autowired
    private AddressDozerConverter addressDozerConverter;

    @Autowired
    private UserAttributeDozerConverter userAttributeDozerConverter;

    @Autowired
    private PhoneDozerConverter phoneDozerConverter;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public Response addAddress(final Address val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.ADDRESS_TYPE_REQUIRED);
            }
            AddressSearchBean searchBean = new AddressSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            List<AddressEntity> entityList = userManager.getAddressList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList))
                throw new BasicDataServiceException(ResponseCode.ADDRESS_TYPE_DUPLICATED);

            final AddressEntity entity = addressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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
            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.EMAIL_ADDRESS_TYPE_REQUIRED);
            }
            EmailSearchBean searchBean = new EmailSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            List<EmailAddressEntity> entityList = userManager.getEmailAddressList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList))
                throw new BasicDataServiceException(ResponseCode.EMAIL_ADDRESS_TYPE_DUPLICATED);

            final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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

    /*
     * @Override public Response addNote(final UserNote note) { final Response
     * response = new Response(ResponseStatus.SUCCESS); try { if (note == null)
     * { throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS); }
     * 
     * final UserNoteEntity entity =
     * userNoteDozerConverter.convertToEntity(note, true);
     * userManager.addNote(entity); } catch (BasicDataServiceException e) {
     * response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch (Throwable e) {
     * log.error("Can't perform operation", e);
     * response.setErrorText(e.getMessage());
     * response.setStatus(ResponseStatus.FAILURE); } return response; }
     */

    @Override
    public Response addPhone(Phone val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.PHONE_TYPE_REQUIRED);
            }
            PhoneSearchBean searchBean = new PhoneSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            List<PhoneEntity> entityList = userManager.getPhoneList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList))
                throw new BasicDataServiceException(ResponseCode.PHONE_TYPE_DUPLICATED);

            final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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
    //@Transactional(readOnly = true)
    public List<User> findUserByOrganization(final String orgId) {

        List<User> resultList = Collections.EMPTY_LIST;
        try {
            resultList = userManager.findUserDtoByOrganization(orgId);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    //@Transactional(readOnly = true)
    public Address getAddressById(String addressId) {
        return userManager.getAddressDtoById(addressId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Address> getAddressList(String userId) {
        return this.getAddressListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Address> getAddressListByPage(String userId, Integer size, Integer from) {

        return userManager.getAddressDtoList(userId, size, from);
    }

    /*
     * @Override
     * 
     * @Transactional(readOnly=true) public List<UserNote> getAllNotes(String
     * userId) { final List<UserNoteEntity> entityList =
     * userManager.getAllNotes(userId); return
     * userNoteDozerConverter.convertToDTOList(entityList, false); }
     */

    @Override
    //@Transactional(readOnly = true)
    public UserAttribute getAttribute(String attrId) {
        return userManager.getAttributeDto(attrId);
    }

    @Override
    //@Transactional(readOnly = true)
    public EmailAddress getEmailAddressById(String addressId) {
        return userManager.getEmailAddressDtoById(addressId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<EmailAddress> getEmailAddressList(String userId) {
        return this.getEmailAddressListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<EmailAddress> getEmailAddressListByPage(String userId, Integer size, Integer from) {
        return userManager.getEmailAddressDtoList(userId, size, from);
    }

    // @Override
    // @Transactional(readOnly=true)
    // public List<Supervisor> getEmployees(String supervisorId) {
    // final List<SupervisorEntity> sup =
    // userManager.getEmployees(supervisorId);
    // return supervisorDozerConverter.convertToDTOList(sup, false);
    // }

    /*
     * @Override
     * 
     * @Transactional(readOnly=true) public UserNote getNote(String noteId) {
     * final UserNoteEntity note = userManager.getNote(noteId); return
     * userNoteDozerConverter.convertToDTO(note, false); }
     */

    @Override
    //@Transactional(readOnly = true)
    public Phone getPhoneById(String addressId) {
        return userManager.getPhoneDtoById(addressId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Phone> getPhoneList(String userId) {
        return getPhoneListByPage(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Phone> getPhoneListByPage(String userId, Integer size, Integer from) {
        return userManager.getPhoneDtoList(userId, size, from);
    }

    @Override
    //@Transactional(readOnly = true)
    public User getPrimarySupervisor(String employeeId) {
        return userManager.getPrimarySupervisorDto(employeeId);
    }

    // @Override
    // @Transactional(readOnly=true)
    // public Supervisor getSupervisor(String supervisorObjId) {
    // final SupervisorEntity sup = userManager.getSupervisor(supervisorObjId);
    // return supervisorDozerConverter.convertToDTO(sup, false);
    // }

    // @Override
    // @Transactional(readOnly=true)
    // public List<Supervisor> getSupervisors(String employeeId) {
    // final List<SupervisorEntity> sup =
    // userManager.getSupervisors(employeeId);
    // return supervisorDozerConverter.convertToDTOList(sup, true);
    // }

    @Override
    //@Transactional(readOnly = true)
    public Supervisor findSupervisor(String superiorId, String subordinateId) {
        return userManager.findSupervisorDto(superiorId, subordinateId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getSuperiors(String userId, Integer from, Integer size) {
        return userManager.getSuperiorsDto(userId, from, size);
    }

    @Override
    public int getSuperiorsCount(String userId) {
        return userManager.getSuperiorsCount(userId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getSubordinates(String userId, Integer from, Integer size) {
        return userManager.getSubordinatesDto(userId, from, size);
    }

    @Override
    public int getSubordinatesCount(String userId) {
        return userManager.getSubordinatesCount(userId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> findPotentialSupSubs(PotentialSupSubSearchBean userSearchBean, Integer from, Integer size) {
        List<User> resultList = Collections.EMPTY_LIST;
        try {
            resultList = userManager.findPotentialSupSubsDto(userSearchBean, from, size);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public int findPotentialSupSubsCount(PotentialSupSubSearchBean userSearchBean) {
        int count = 0;
        try {
            count = userManager.findPotentialSupSubsCount(userSearchBean);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
        return count;
    }

    @Override
    public Response addSuperior(String superiorId, String suborinateId, String requesterId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.equals(superiorId, suborinateId)) {
                throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
            }
            User superior = getUserWithDependent(superiorId, null, true);
            User subordinate = getUserWithDependent(suborinateId, requesterId, true);
            if (superior == null || subordinate == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            SupervisorEntity found = userManager.findSupervisor(superior.getId(), subordinate.getId());
            if (found != null) {
                throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
            }
            SupervisorEntity contrary = userManager.findSupervisor(subordinate.getId(), superior.getId());
            if (contrary != null) {
                throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
            }
            userManager.addSuperior(superiorId, suborinateId);

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
    public Response removeSuperior(String userId, String employeeId) {

        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            SupervisorEntity supervisor = userManager.findSupervisor(userId, employeeId);
            if (supervisor == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }
            userManager.removeSupervisor(userId, employeeId);

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
    //@Transactional(readOnly = true)
    public User getUserWithDependent(String id, String requestorId, boolean dependants) {
        return userManager.getUserDto(id, requestorId, dependants);
    }

    @Override
    //@Transactional(readOnly = true)
    public User getUserByPrincipal(String principal, String managedSysId, boolean dependants) {
        return userManager.getUserDtoByPrincipal(principal, managedSysId, dependants);
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

    /*
     * @Override public Response removeAllNotes(String userId) { final Response
     * response = new Response(ResponseStatus.SUCCESS); try { if (userId ==
     * null) { throw new
     * BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS); }
     * userManager.removeAllNotes(userId); } catch (BasicDataServiceException e)
     * { response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch (Throwable e) {
     * log.error("Can't perform operation", e);
     * response.setErrorText(e.getMessage());
     * response.setStatus(ResponseStatus.FAILURE); } return response; }
     */

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

    /*
     * @Override public Response removeNote(String noteId) { final Response
     * response = new Response(ResponseStatus.SUCCESS); try { if (noteId ==
     * null) { throw new
     * BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS); }
     * userManager.removeNote(noteId); } catch (BasicDataServiceException e) {
     * response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch (Throwable e) {
     * log.error("Can't perform operation", e);
     * response.setErrorText(e.getMessage());
     * response.setStatus(ResponseStatus.FAILURE); } return response; }
     */

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
    public Response removeSupervisor(String supervisorId, String employeeId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (supervisorId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.removeSupervisor(supervisorId, employeeId);
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
    //@Transactional(readOnly = true)
    public List<User> findBeans(UserSearchBean userSearchBean, int from, int size) {
        List<User> resultList = Collections.EMPTY_LIST;
        try {
            resultList = userManager.findBeansDto(userSearchBean, from, size);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public int count(UserSearchBean userSearchBean) {
        int count = 0;
        try {
            count = userManager.count(userSearchBean);
        } catch (BasicDataServiceException e) {
            log.error(e.getMessage(), e);
        }
        return count;
    }

    @Override
    public Response updateAddress(Address val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.ADDRESS_TYPE_REQUIRED);
            }

            AddressSearchBean searchBean = new AddressSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            List<AddressEntity> entityList = userManager.getAddressList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList) && !entityList.get(0).getAddressId().equals(val.getAddressId()))
                throw new BasicDataServiceException(ResponseCode.ADDRESS_TYPE_DUPLICATED);

            final AddressEntity entity = addressDozerConverter.convertToEntity(val, false);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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

            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.EMAIL_ADDRESS_TYPE_REQUIRED);
            }

            EmailSearchBean searchBean = new EmailSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
            List<EmailAddressEntity> entityList = userManager.getEmailAddressList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList) && !entityList.get(0).getEmailId().equals(val.getEmailId()))
                throw new BasicDataServiceException(ResponseCode.EMAIL_ADDRESS_TYPE_DUPLICATED);

            final EmailAddressEntity entity = emailAddressDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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

    /*
     * @Override public Response updateNote(UserNote note) { final Response
     * response = new Response(ResponseStatus.SUCCESS); try { if (note == null)
     * { throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS); }
     * 
     * final UserNoteEntity entity =
     * userNoteDozerConverter.convertToEntity(note, true);
     * userManager.updateNote(entity); } catch (BasicDataServiceException e) {
     * response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch (Throwable e) {
     * log.error("Can't perform operation", e);
     * response.setErrorText(e.getMessage());
     * response.setStatus(ResponseStatus.FAILURE); } return response; }
     */

    @Override
    public Response updatePhone(Phone val) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (val == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(val.getMetadataTypeId())) {
                throw new BasicDataServiceException(ResponseCode.PHONE_TYPE_REQUIRED);
            }

            PhoneSearchBean searchBean = new PhoneSearchBean();
            searchBean.setParentId(val.getParentId());
            searchBean.setMetadataTypeId(val.getMetadataTypeId());
            // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
            List<PhoneEntity> entityList = userManager.getPhoneList(searchBean, Integer.MAX_VALUE, 0);
            if (CollectionUtils.isNotEmpty(entityList) && !entityList.get(0).getPhoneId().equals(val.getPhoneId()))
                throw new BasicDataServiceException(ResponseCode.PHONE_TYPE_DUPLICATED);

            final PhoneEntity entity = phoneDozerConverter.convertToEntity(val, true);
            UserEntity user = new UserEntity();
            user.setId(val.getParentId());
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

    // @Override
    // public Response updateSupervisor(Supervisor supervisor) {
    // final Response response = new Response(ResponseStatus.SUCCESS);
    // try {
    // if (supervisor == null) {
    // throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
    // }
    //
    // final SupervisorEntity entity =
    // supervisorDozerConverter.convertToEntity(supervisor, true);
    // userManager.updateSupervisor(entity);
    // } catch (BasicDataServiceException e) {
    // response.setErrorCode(e.getCode());
    // response.setStatus(ResponseStatus.FAILURE);
    // } catch (Throwable e) {
    // log.error("Can't perform operation", e);
    // response.setErrorText(e.getMessage());
    // response.setStatus(ResponseStatus.FAILURE);
    // }
    // return response;
    // }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getUsersForResource(final String resourceId, String requesterId, final int from, final int size) {
        return userManager.getUsersDtoForResource(resourceId, requesterId, from, size);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getUsersForResourceWithSorting(final UserSearchBean userSearchBean, final int from, final int size) {
        return userManager.getUsersDtoForResource(userSearchBean, from, size);
    }


    @Override
    public int getNumOfUsersForResource(final String resourceId, String requesterId) {
        return userManager.getNumOfUsersForResource(resourceId, requesterId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getUsersForGroup(final String groupId, String requesterId, final int from, final int size) {
        return userManager.getUsersDtoForGroup(groupId, requesterId, from, size);
    }

    @Override
    public int getNumOfUsersForGroup(final String groupId, String requesterId) {
        return userManager.getNumOfUsersForGroup(groupId, requesterId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getUsersForRole(final String roleId, String requesterId, final int from, final int size) {
        return userManager.getUsersDtoForRole(roleId, requesterId, from, size);
    }

    @Override
    public int getNumOfUsersForRole(final String roleId, String requesterId) {
        return userManager.getNumOfUsersForRole(roleId, requesterId);
    }

    @Override
    public UserResponse saveUserInfo(final User user, final String supervisorId) {
        final UserResponse response = new UserResponse(ResponseStatus.SUCCESS);
        try {
            if (user == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (user.getId() == null) {

                // final MetadataTypeSearchBean typeSearchBean = new
                // MetadataTypeSearchBean();
                // typeSearchBean.setGrouping("EMAIL");
                // typeSearchBean.setActive(true);

                // final List<MetadataTypeEntity> entityList =
                // metadataService.findBeans(typeSearchBean, 0,
                // Integer.MAX_VALUE);
                // List<MetadataType> typeList = (entityList != null) ?
                // metaDataTypeDozerConverter.convertToDTOList(entityList,
                // false) : null;

                // create new user, need to merge user objects
                List<Login> principalList = new ArrayList<Login>();
                Login principal = new Login();
                principal.setLogin(user.getLogin());
                principal.setPassword(user.getPassword());
                principalList.add(principal);
                user.setPrincipalList(principalList);

                // if(CollectionUtils.isNotEmpty(typeList)){
                // Set<EmailAddress> emailAddressList = new
                // HashSet<EmailAddress>();
                //
                // EmailAddress ea = new EmailAddress();
                // ea.setEmailAddress(user.getEmail());
                // ea.setIsDefault(true);
                // ea.setMetadataTypeId(typeList.get(0).getMetadataTypeId());
                // emailAddressList.add(ea);
                // user.setEmailAddresses(emailAddressList);
                // }
            }

            final UserEntity userEntity = userDozerConverter.convertToEntity(user, true);
            SupervisorEntity supervisorEntity = null;
            // if (supervisorId != null)
            // supervisorEntity =
            // supervisorDozerConverter.convertToEntity(supervisor, true);
            String userId = userManager.saveUserInfo(userEntity, supervisorId);
            user.setId(userId);

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
    public Response setSecondaryStatus(final String userId, final UserStatusEnum secondaryStatus) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.setSecondaryStatus(userId, secondaryStatus);
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
    public Response resetUser(final String userId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (userId == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            userManager.resetUser(userId);
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
    public int getNumOfEmailsForUser(String userId) {
        return userManager.getNumOfEmailsForUser(userId);
    }

    @Override
    public int getNumOfAddressesForUser(String userId) {
        return userManager.getNumOfAddressesForUser(userId);
    }

    @Override
    public int getNumOfPhonesForUser(String userId) {
        return userManager.getNumOfPhonesForUser(userId);
    }

    private void sendCredentialsToUser(User user, String identity, String password) throws BasicDataServiceException {

        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(user.getId());
        notificationRequest.setNotificationType("NEW_USER_EMAIL");
        notificationRequest.getParamList().add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), identity));
        notificationRequest.getParamList().add(new NotificationParam(MailTemplateParameters.PASSWORD.value(), password));
        notificationRequest.getParamList().add(new NotificationParam(MailTemplateParameters.USER_ID.value(), user.getId()));
        final boolean sendEmailResult = mailService.sendNotification(notificationRequest);
        if (!sendEmailResult) {
            throw new BasicDataServiceException(ResponseCode.SEND_EMAIL_FAILED);
        }

    }

    @Override
    public List<UserAttribute> getUserAttributes(final String userId) {
        return userManager.getUserAttributesDtoList(userId);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<UserAttribute> getUserAttributesInternationalized(final String userId, final Language language) {
        final List<UserAttribute> retval = userManager.getUserAttributeDtoList(userId, languageConverter.convertToEntity(language, false));
        return retval;
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
    public ProfilePicture getProfilePictureById(String picId, String requesterId) {
        return userProfileService.getProfilePictureById(picId);
    }

    @Override
    public ProfilePicture getProfilePictureByUserId(String userId, String requesterId) {
        return profilePictureDozerConverter.convertToDTO(userProfileService.getProfilePictureByUserId(userId), false);
    }

    @Override
    public Response saveProfilePicture(ProfilePicture pic, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        if (StringUtils.isBlank(pic.getId())) {
            idmAuditLog.setAction(AuditAction.ADD_PROFILE_PICTURE_FOR_USER.value());
        } else {
            idmAuditLog.setAction(AuditAction.UPDATE_PROFILE_PICTURE_FOR_USER.value());
        }
        String userId = pic.getUser().getId();
        UserEntity user = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        idmAuditLog.setAuditDescription(String.format("Add profile pic for user: %s", pic.getUser().getId()));

        try {
            userProfileService.saveProfilePicture(profilePictureDozerConverter.convertToEntity(pic, true));
            idmAuditLog.succeed();

        } catch (BasicDataServiceException e) {
            response.setErrorTokenList(e.getErrorTokenList());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);

        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setException(e);

        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    public Response deleteProfilePictureById(String picId, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.DELETE_PROFILE_PICTURE_FOR_USER.value());
        idmAuditLog.setAuditDescription(String.format("Delete profile picture with id: %s", picId));

        try {
            userProfileService.deleteProfilePictureById(picId);
            idmAuditLog.succeed();

        } catch (BasicDataServiceException e) {
            response.setErrorTokenList(e.getErrorTokenList());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);

        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setException(e);

        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    @Override
    public Response deleteProfilePictureByUserId(String userId, String requesterId) {
        final Response response = new Response(ResponseStatus.SUCCESS);

        IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requesterId);
        idmAuditLog.setAction(AuditAction.DELETE_PROFILE_PICTURE_FOR_USER.value());
        UserEntity user = userDataService.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), user.getPrincipalList());
        idmAuditLog.setTargetUser(userId, primaryIdentity.getLogin());
        idmAuditLog.setAuditDescription(String.format("Delete profile pic for user: %s", userId));

        try {
            userProfileService.deleteProfilePictureByUserId(userId);
            idmAuditLog.succeed();

        } catch (BasicDataServiceException e) {
            response.setErrorTokenList(e.getErrorTokenList());
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);

        } catch (Throwable e) {
            log.error("Can't perform operation", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
            idmAuditLog.fail();
            idmAuditLog.setException(e);

        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
    }

    /*
     * @Override public SaveTemplateProfileResponse createNewUserProfile(final
     * NewUserProfileRequestModel request) { final SaveTemplateProfileResponse
     * response = new SaveTemplateProfileResponse(ResponseStatus.SUCCESS); try {
     * if (request == null || request.getUser() == null ||
     * CollectionUtils.isEmpty(request.getLoginList())) { throw new
     * BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS); }
     * 
     * final CreateUserToken token =
     * userProfileService.createNewUserProfile(request); final UserEntity
     * userEntity = token.getUser(); final String login = token.getLogin();
     * final String plaintextPassword = token.getPassword();
     * response.setUserId(userEntity.getUserId());
     * response.setPlaintextPassword(plaintextPassword);
     * response.setLogin(login); } catch (PageTemplateException e) {
     * response.setCurrentValue(e.getCurrentValue());
     * response.setElementName(e.getElementName());
     * response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch
     * (BasicDataServiceException e) { response.setErrorCode(e.getCode());
     * response.setStatus(ResponseStatus.FAILURE); } catch (Throwable e) {
     * log.error("Can't perform operation", e);
     * response.setErrorText(e.getMessage());
     * response.setStatus(ResponseStatus.FAILURE); } return response; }
     */

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

    public Response validateUserSearchRequest(UserSearchBean userSearchBean) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (!userManager.validateSearchBean(userSearchBean)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_USER_SEARCH_REQUEST);
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
    //@Transactional(readOnly = true)
    public List<User> getUserByLastDate(Date lastDate) {
        List<User> resultList = Collections.EMPTY_LIST;
        try {
            resultList = userManager.getUserDtoByLastDate(lastDate);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return resultList;
    }

    @Override
    public List<User> getUserByCreatedDate(Date fromDate, Date toDate) {
        return userManager.getUserDtoByCreatedDate(fromDate, toDate);
    }

    @Override
    public List<User> getUserByDeletedDate(Date fromDate, Date toDate) {
        return userManager.getUserDtoByDeletedDate(fromDate, toDate);
    }

    @Override
    public List<User> getUserByUpdatedDate(Date fromDate, Date toDate) {
        return userManager.getUserDtoByUpdatedDate(fromDate, toDate);
    }

    @Override
    //@Transactional(readOnly = true)
    public List<User> getAllSuperiors(@WebParam(name = "from", targetNamespace = "") Integer from,
                                      @WebParam(name = "size", targetNamespace = "") Integer size) {

        return userManager.getAllSuperiorsDto(from, size);
    }

    @Override
    //@Transactional(readOnly = true)
    public int getAllSuperiorsCount() {
        return userManager.getAllSuperiorsCount();
    }

    @Override
    //@Transactional(readOnly = true)
    public List<EmailAddress> findEmailBeans(final EmailSearchBean searchBean, final int size, final int from) {
        return userManager.getEmailAddressDtoList(searchBean, size, from);
    }

//    @Override
//    public Map<String, UserAttribute> getUserAttributesAsMap(@WebParam(name = "userId", targetNamespace = "") String userId){
//        return userManager.getUserAttributesDto(userId);
//    }
}
