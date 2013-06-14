package org.openiam.bpm.activiti.delegate.entitlements;

import org.activiti.engine.delegate.DelegateExecution;
import org.openiam.bpm.util.ActivitiConstants;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceUserDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class EntitleUserToResource extends AbstractEntitlementsDelegate {

	@Autowired
	private ResourceUserDAO resourceUserDAO;
	
	@Autowired
	private ResourceDAO resourceDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	public EntitleUserToResource() {
		super();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String resourceId = (String)execution.getVariable(ActivitiConstants.ASSOCIATION_ID);
		final String userId = (String)execution.getVariable(ActivitiConstants.MEMBER_ASSOCIATION_ID);	
		final ResourceEntity resource = resourceDAO.findById(resourceId);
		final UserEntity user = userDAO.findById(userId);
		if(resource != null && user != null) {
			final ResourceUserEntity toSave = new ResourceUserEntity();
			toSave.setUserId(userId);
			toSave.setResourceId(resourceId);
			resourceUserDAO.save(toSave);
		}
	}

	@Override
	protected String getNotificationType() {
		return null;
	}
}
