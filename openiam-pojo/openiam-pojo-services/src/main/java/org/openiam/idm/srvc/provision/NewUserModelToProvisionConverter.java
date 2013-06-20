package org.openiam.idm.srvc.provision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.PageTemplateAttributeToken;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
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
	private RoleDozerConverter roleDozerConverter;
	
	@Autowired
	private UserAttributeDozerConverter userAttributeDozerConverter;
	
	@Transactional
	public ProvisionUser convertNewProfileModel(final NewUserProfileRequestModel request) {
		ProvisionUser user = null;
		if(request.getUser() != null) {
			user = new ProvisionUser(request.getUser());
			if(CollectionUtils.isNotEmpty(request.getAddresses())) {
				user.setAddresses(new HashSet<Address>(request.getAddresses()));
			}
			if(CollectionUtils.isNotEmpty(request.getEmails())) {
				user.setEmailAddresses(new HashSet<EmailAddress>(request.getEmails()));
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
			
			final PageTemplateAttributeToken token = templateService.getAttributesFromTemplate(request);
			if(token != null && CollectionUtils.isNotEmpty(token.getSaveList())) {
				final List<UserAttribute> userAttributeList = userAttributeDozerConverter.convertToDTOList(token.getSaveList(), true);
				final HashMap<String, UserAttribute> userAttributes = new HashMap<String, UserAttribute>();
				for(final UserAttribute attribute : userAttributeList) {
					if(attribute != null) {
						userAttributes.put(attribute.getId(), attribute);
					}
				}
				user.setUserAttributes(userAttributes);
			}
		}
		return user;
	}
}
