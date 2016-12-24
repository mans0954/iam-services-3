package org.openiam.idm.srvc.provision;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationUserDTO;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserToGroupMembershipXref;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewUserModelToProvisionConverter {

	@Autowired
	private MetadataElementTemplateService templateService;
	
	@Autowired
	private RoleDataService roleDataService;
	
	@Autowired
	private GroupDataService groupDataService;
	
	@Autowired
	private GroupDozerConverter groupDozerConverter;
	
	@Autowired
	private RoleDozerConverter roleDozerConverter;
	
	@Autowired
	private UserAttributeDozerConverter userAttributeDozerConverter;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private UserDozerConverter userDozerConverter;
	
	@Autowired
	private OrganizationService organizationDataService;
	
	@Autowired
	private OrganizationDozerConverter organizationDozerConverter;
	

    @Autowired
    @Qualifier("sysConfiguration")
    private SysConfiguration sysConfiguration;

	@Transactional
	public ProvisionUser convertNewProfileModel(final NewUserProfileRequestModel request) {
		ProvisionUser user = null;
		if(request.getUser() != null) {
			user = new ProvisionUser(request.getUser());
			if(CollectionUtils.isNotEmpty(request.getAddresses())) {
                Set<Address> addresses = new HashSet<Address>();
                for(Address a : request.getAddresses()) {
                    a.setOperation(AttributeOperationEnum.ADD);
                    addresses.add(a);
                }
				user.setAddresses(addresses);
			}
			if(CollectionUtils.isNotEmpty(request.getEmails())) {
                Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>();
				for(EmailAddress ea : request.getEmails()) {
                    ea.setOperation(AttributeOperationEnum.ADD);
                    emailAddresses.add(ea);
                }
                user.setEmailAddresses(emailAddresses);
			}
			if(CollectionUtils.isNotEmpty(request.getLoginList())) {
                List<Login> principals = new ArrayList<Login>();
                for(Login l : request.getLoginList()) {
                    l.setOperation(AttributeOperationEnum.ADD);
                    principals.add(l);
                }
				user.setPrincipalList(principals);
			}
			if(CollectionUtils.isNotEmpty(request.getPhones())) {
                Set<Phone> phones = new HashSet<Phone>();
                for(Phone p : request.getPhones()) {
                    p.setOperation(AttributeOperationEnum.ADD);
                    phones.add(p);
                }
				user.setPhones(phones);
			}
			
			if(CollectionUtils.isNotEmpty(request.getRoleIds())) {
				for(final String roleId : request.getRoleIds()) {
					final RoleEntity entity = roleDataService.getRoleLocalized(roleId, null);
					if(entity != null) {
						final Role role = roleDozerConverter.convertToDTO(entity, false);
						user.addRole(role, null, null, null);
					}
					/*
					final UserRole userRole = new UserRole(null, roleId);
					userRoles.add(userRole);
					*/
				}
			}

			if(CollectionUtils.isNotEmpty(request.getGroupIds())) {
				for(final String groupId : request.getGroupIds()) {
					final GroupEntity entity = groupDataService.getGroup(groupId);
					if(entity != null) {
						final Group group = groupDozerConverter.convertToDTO(entity, false);
                        user.addGroup(group, null, null, null);
					}
				}
			}

			if(CollectionUtils.isNotEmpty(request.getOrganizationIds())) {
				for(final String organizationId : request.getOrganizationIds()) {
					final OrganizationEntity entity = organizationDataService.getOrganizationLocalized(organizationId, null);
					if(entity != null) {
						final Organization organization = organizationDozerConverter.convertToDTO(entity, false);
						user.addAffiliation(organization, null, null, null);
					}
				}
			}

			if(CollectionUtils.isNotEmpty(request.getSupervisorIds())) {
                final Set<User> userSupervisors = new HashSet<User>();
				for(final String supervisorId : request.getSupervisorIds()) {
					/*
					final UserEntity entity = userDataService.getUser(supervisorId);
					if(entity != null) {
						final User supervisor = userDozerConverter.convertToDTO(entity, false);
                        supervisor.setOperation(AttributeOperationEnum.ADD);
						userSupervisors.add(supervisor);
					}
					*/
					final User supervisor = userDataService.getUserDto(supervisorId);
					if(supervisor != null) {
						 supervisor.setOperation(AttributeOperationEnum.ADD);
						 userSupervisors.add(supervisor);
					}
				}
                user.setSuperiors(userSupervisors);
			}

			final HashMap<String, UserAttribute> userAttributes = new HashMap<String, UserAttribute>();
			final PageTemplateAttributeToken token = templateService.getAttributesFromTemplate(request);
			if(token != null && CollectionUtils.isNotEmpty(token.getSaveList())) {
				final List<UserAttribute> userAttributeList = userAttributeDozerConverter.convertToDTOList(((List<UserAttributeEntity>)token.getSaveList()), true);
				for(final UserAttribute attribute : userAttributeList) {
					if(attribute != null) {
						attribute.setOperation(AttributeOperationEnum.ADD);
						userAttributes.put(attribute.getName(), attribute);
					}
				}
			}
			
			if(MapUtils.isNotEmpty(request.getUser().getUserAttributes())) {
				final Set<Entry<String, UserAttribute>> entrySet = request.getUser().getUserAttributes().entrySet();
				for(final Iterator<Entry<String, UserAttribute>> it = entrySet.iterator(); it.hasNext();) {
					final Entry<String, UserAttribute> entry = it.next();
					if(entry != null) {
						final UserAttribute attribute = entry.getValue();
						attribute.setOperation(AttributeOperationEnum.ADD);
						if(attribute != null) {
							if(StringUtils.isNotBlank(attribute.getName())) {
								if(StringUtils.isNotBlank(attribute.getValue())) {
									userAttributes.put(attribute.getName(), attribute);
								} else if(Boolean.TRUE.equals(attribute.getIsMultivalued())) {
									if(CollectionUtils.isNotEmpty(attribute.getValues())) {
										for(final Iterator<String> valueIt = attribute.getValues().iterator(); valueIt.hasNext();) {
											final String value = valueIt.next();
											if(StringUtils.isBlank(value)) {
												valueIt.remove();
											}
										}
									}
									//the above code removed empty values - check again if the list is empty
									if(CollectionUtils.isNotEmpty(attribute.getValues())) {
										userAttributes.put(attribute.getName(), attribute);
									}
								}
							}
						}
					}
				}
			}
			
			user.setUserAttributes(userAttributes);
		}
		return user;
	}
}
