package org.openiam.bpm.activiti.groovy;

import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UserCentricApproverAssociationIdentifier {

	@Autowired
	protected ApproverAssociationDAO approverAssociationDAO;
	
	protected UserEntity user;
	
	protected UserCentricApproverAssociationIdentifier() {
		SpringContextProvider.autowire(this);
	}
	
	public final void init(final Map<String, Object> bindingMap) {
		this.user = (UserEntity)bindingMap.get("USER");
	}
	
	public List<ApproverAssociationEntity> getApproverAssociations() {
		return null;
	}
}
