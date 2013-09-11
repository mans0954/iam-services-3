package org.openiam.bpm.activiti.delegate.user.attestation;

import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.dto.UserResourceAssociation;
import org.openiam.provision.service.ProvisionService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thoughtworks.xstream.XStream;

public class AttestationWorkDelegate implements JavaDelegate {

	@Autowired
	private GroupDataService groupService;
	
	@Autowired
	private RoleDataService roleService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private GroupDozerConverter groupDozerConverter;
	
	@Autowired
	private RoleDozerConverter roleDozerConverter;
	
	@Autowired
	private ResourceDozerConverter resourceDozerConverter;
	
	@Autowired
	@Qualifier("defaultProvision")
	private ProvisionService provisionService;
	
	private static Logger LOG = Logger.getLogger(AttestationWorkDelegate.class);
	
	public AttestationWorkDelegate() {
		SpringContextProvider.autowire(this);
	}
	
	private Set<String> objectToSet(final Object obj) {
		Set<String> retVal = new HashSet<String>();
		if(obj != null) {
			if(obj instanceof String) {
				retVal = (Set<String>)new XStream().fromXML((String)obj);
			} else if (obj instanceof Set) {
				retVal = (Set<String>)obj;
			}
		}
		return retVal;
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final String employeeId = (String)execution.getVariable(ActivitiConstants.EMPLOYEE_ID);
		final Set<String> disassociatedGroupIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_GROUP_IDS));
		final Set<String> disassociatedRoleIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_ROLE_IDS));
		final Set<String> disassociatedResourceIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_RESOURCE_IDS));
		
		final User dto = userDataService.getUserDto(employeeId);
		if(dto != null) {
			final ProvisionUser provisionUser = new ProvisionUser(dto);
			if(CollectionUtils.isNotEmpty(disassociatedGroupIds)) {
				for(final String groupId : disassociatedGroupIds) {
					final GroupEntity groupEntity = groupService.getGroup(groupId);
					if(groupEntity != null) {
						final Group group = groupDozerConverter.convertToDTO(groupEntity, false);
						group.setOperation(AttributeOperationEnum.DELETE);
						provisionUser.getGroups().add(group);
					}
				}
			}
			
			if(CollectionUtils.isNotEmpty(disassociatedRoleIds)) {
				for(final String roleId : disassociatedRoleIds) {
					final RoleEntity roleEntity = roleService.getRole(roleId);
					if(roleEntity != null) {
						final Role role = roleDozerConverter.convertToDTO(roleEntity, false);
						role.setOperation(AttributeOperationEnum.DELETE);
						provisionUser.getRoles().add(role);
					}
				}
			}
			
			if(CollectionUtils.isNotEmpty(disassociatedResourceIds)) {
				for(final String resourceId : disassociatedResourceIds) {
					final ResourceEntity resourceEntity = resourceService.findResourceById(resourceId);
					if(resourceEntity != null) {
						final Resource resource = resourceDozerConverter.convertToDTO(resourceEntity, false);
                        resource.setOperation(AttributeOperationEnum.DELETE);
						provisionUser.getResources().add(resource);
					}
				}
			}
			provisionService.modifyUser(provisionUser);
		}
		sw.stop();
		LOG.info(String.format("Took %s ms to send process request for user %s", sw.getTime(), employeeId));
	}
}
