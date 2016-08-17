package org.openiam.bpm.activiti.delegate.user.newuser.displaymapper;

import java.util.LinkedHashMap;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.user.displaymapper.AbstractUserDisplayMapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;

public class NewUserDisplayMapperDelegate extends AbstractUserDisplayMapper {

	public NewUserDisplayMapperDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final NewUserProfileRequestModel request = getObjectVariable(execution, ActivitiConstants.REQUEST, NewUserProfileRequestModel.class);
		
		final LinkedHashMap<String, String> metadataMap = getMetadataMap(request, execution);
	
		final List<Login> logins = request.getLoginList();
		if(CollectionUtils.isNotEmpty(logins)) {
			for(int i = 0; i < logins.size(); i++) {
				final Login login = logins.get(i);
				if(login != null) {
					final String str = toString(login);
					if(StringUtils.isNotBlank(str)) {
						metadataMap.put(String.format("Login %s", i + 1), str);
					}
				}
			}
		}
		
		final List<String> roleIds = request.getRoleIds();
		if(CollectionUtils.isNotEmpty(roleIds)) {
			int idx = 1;
			for(final String roleId : roleIds) {
				if(StringUtils.isNotBlank(roleId)) {
					final RoleEntity role = roleDataService.getRole(roleId);
					if(role != null) {
						metadataMap.put(String.format("Role %s", idx), role.getName());
						idx++;
					}
				}
			}
		}
		
		final List<String> groupIds = request.getGroupIds();
		if(CollectionUtils.isNotEmpty(groupIds)) {
			int idx = 1;
			for(final String groupId : groupIds) {
				final GroupEntity group = groupDataService.getGroup(groupId);
				if(group != null) {
					metadataMap.put(String.format("Group %s", idx), group.getName());
					idx++;
				}
			}
		}
		
		final List<String> organizationIds = request.getOrganizationIds();
		if(CollectionUtils.isNotEmpty(organizationIds)) {
			int idx = 1;
			for(final String organizationId : organizationIds) {
				final OrganizationEntity organization = organizationService.getOrganization(organizationId, null);
				if(organization != null) {
					metadataMap.put(String.format("Organization %s", idx), organization.getName());
					idx++;
				}
			}
		}
		
		final List<String> supervisorIds = request.getSupervisorIds();
		if(CollectionUtils.isNotEmpty(supervisorIds)) {
			int idx = 1;
			for(final String supervisorId : supervisorIds) {
				final UserEntity supervisor = userDataService.getUser(supervisorId);
				if(supervisor != null) {
					metadataMap.put(String.format("Supervisor %s", idx), supervisor.getDisplayName());
					idx++;
				}
			}
		}
		
		execution.setVariable(ActivitiConstants.REQUEST_METADATA_MAP.getName(), metadataMap);
	}
	
	
	private String toString(final Login login) {
		return StringUtils.trimToNull(login.getLogin());
	}
}
