package org.openiam.idm.srvc.provision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
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
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
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
	
	public ProvisionUser convertNewProfileModel(final NewUserProfileRequestModel request) {
		ProvisionUser user = null;
		if(request.getUser() != null) {
			user = new ProvisionUser(request.getUser());
			if(CollectionUtils.isNotEmpty(request.getAddresses())) {
				user.setAddresses(new HashSet<Address>(request.getAddresses()));
			}
			if(CollectionUtils.isNotEmpty(request.getEmails())) {
                Set<EmailAddress> emailAddresses = new HashSet<EmailAddress>();
				for(EmailAddress ea : request.getEmails()) {
                    emailAddresses.add(ea);
                    ea.setOperation(AttributeOperationEnum.ADD);
                }
                user.setEmailAddresses(emailAddresses);
			}
			if(CollectionUtils.isNotEmpty(request.getLoginList())) {
				user.setPrincipalList(request.getLoginList());
			}
			if(CollectionUtils.isNotEmpty(request.getPhones())) {
				user.setPhones(new HashSet<Phone>(request.getPhones()));
			}
			
			final List<Role> userRoles = new LinkedList<Role>();
			if(CollectionUtils.isNotEmpty(request.getRoleIds())) {
				for(final String roleId : request.getRoleIds()) {
					final RoleEntity entity = roleDataService.getRole(roleId);
					if(entity != null) {
						final Role role = roleDozerConverter.convertToDTO(entity, false);
						userRoles.add(role);
					}
					/*
					final UserRole userRole = new UserRole(null, roleId);
					userRoles.add(userRole);
					*/
				}
			}
			user.setMemberOfRoles(userRoles);
			
			final List<Group> userGroups = new LinkedList<Group>();
			if(CollectionUtils.isNotEmpty(request.getGroupIds())) {
				for(final String groupId : request.getGroupIds()) {
					final GroupEntity entity = groupDataService.getGroup(groupId);
					if(entity != null) {
						final Group group = groupDozerConverter.convertToDTO(entity, false);
						userGroups.add(group);
					}
				}
			}
			user.setMemberOfGroups(userGroups);
			
			final List<Organization> userOrganizations = new LinkedList<Organization>();
			if(CollectionUtils.isNotEmpty(request.getOrganizationIds())) {
				for(final String organizationId : request.getOrganizationIds()) {
					final OrganizationEntity entity = organizationDataService.getOrganization(organizationId);
					if(entity != null) {
						final Organization organization = organizationDozerConverter.convertToDTO(entity, false);
						userOrganizations.add(organization);
					}
				}
			}
			user.setUserAffiliations(userOrganizations);
			
			final Set<User> userSupervisors = new HashSet<User>();
			if(CollectionUtils.isNotEmpty(request.getSupervisorIds())) {
				for(final String supervisorId : request.getSupervisorIds()) {
					final UserEntity entity = userDataService.getUser(supervisorId);
					if(entity != null) {
						final User supervisor = userDozerConverter.convertToDTO(entity, false);
						userSupervisors.add(supervisor);
					}
				}
			}
			user.setSuperiors(userSupervisors);
			
			final PageTemplateAttributeToken token = templateService.getAttributesFromTemplate(request);
			if(token != null && CollectionUtils.isNotEmpty(token.getSaveList())) {
				final List<UserAttribute> userAttributeList = userAttributeDozerConverter.convertToDTOList(token.getSaveList(), true);
				final HashMap<String, UserAttribute> userAttributes = new HashMap<String, UserAttribute>();
				for(final UserAttribute attribute : userAttributeList) {
					if(attribute != null) {
						userAttributes.put(attribute.getName(), attribute);
					}
				}
				user.setUserAttributes(userAttributes);
			}
		}
		return user;
	}
}
