package org.openiam.bpm.activiti.delegate.user.attestation;

import java.util.HashSet;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.bpm.activiti.delegate.core.AbstractActivitiJob;
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

public class AttestationWorkDelegate extends AbstractActivitiJob {
	private static Logger LOG = Logger.getLogger(AttestationWorkDelegate.class);
	
	public AttestationWorkDelegate() {
		super();
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
		
		final String employeeId = getStringVariable(execution, ActivitiConstants.EMPLOYEE_ID);
		final Set<String> disassociatedGroupIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_GROUP_IDS.getName()));
		final Set<String> disassociatedRoleIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_ROLE_IDS.getName()));
		final Set<String> disassociatedResourceIds = objectToSet(execution.getVariable(ActivitiConstants.DISASSOCIATED_RESOURCE_IDS.getName()));
		
		final User dto = userDataService.getUserDto(employeeId);
		if(dto != null) {
			final ProvisionUser provisionUser = new ProvisionUser(dto);
			if(CollectionUtils.isNotEmpty(disassociatedGroupIds)) {
				for(final String groupId : disassociatedGroupIds) {
					provisionUser.markGroupAsDeleted(groupId);
				}
			}
			
			if(CollectionUtils.isNotEmpty(disassociatedRoleIds)) {
				for(final String roleId : disassociatedRoleIds) {
					provisionUser.markRoleAsDeleted(roleId);
				}
			}
			
			if(CollectionUtils.isNotEmpty(disassociatedResourceIds)) {
				for(final String resourceId : disassociatedResourceIds) {
					provisionUser.markResourceAsDeleted(resourceId);
				}
			}
			provisionService.modifyUser(provisionUser);
		}
		sw.stop();
		LOG.info(String.format("Took %s ms to send process request for user %s", sw.getTime(), employeeId));
	}
}
