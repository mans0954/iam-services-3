package org.openiam.bpm.activiti.delegate.user.newuser.displaymapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.bpm.activiti.delegate.user.displaymapper.AbstractUserDisplayMapper;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.meta.dto.PageElement;
import org.openiam.idm.srvc.meta.dto.PageElementValue;
import org.openiam.idm.srvc.meta.dto.PageTempate;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.service.RequestDataService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class NewUserDisplayMapperDelegate extends AbstractUserDisplayMapper {
	
	@Autowired
	@Qualifier("provRequestService")
	private RequestDataService provRequestService;

	public NewUserDisplayMapperDelegate() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String provisionRequestId = getStringVariable(execution, ActivitiConstants.PROVISION_REQUEST_ID);
		final ProvisionRequestEntity provisionRequest = provRequestService.getRequest(provisionRequestId);
		final NewUserProfileRequestModel request = (NewUserProfileRequestModel)new XStream().fromXML(provisionRequest.getRequestXML());
		
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
				final OrganizationEntity organization = organizationService.getOrganization(organizationId);
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
