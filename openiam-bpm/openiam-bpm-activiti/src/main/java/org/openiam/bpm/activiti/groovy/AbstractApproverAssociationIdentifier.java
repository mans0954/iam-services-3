package org.openiam.bpm.activiti.groovy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractApproverAssociationIdentifier {
	
	@Autowired
	protected ApproverAssociationDAO approverAssociationDAO;
	
	@Autowired
	protected UserDataService userDataService;
	
	@Autowired
	protected CustomJacksonMapper jacksonMapper;
	
	@Value("${org.openiam.idm.activiti.default.approver.association.resource.name}")
	protected String defaultApproverAssociationResourceId;
	
	@Value("${org.openiam.idm.activiti.default.approver.user}")
	protected String defaultApproverUserId;
	
	protected static Logger LOG = Logger.getLogger(AbstractApproverAssociationIdentifier.class);
	
	protected final List<String> approverAssociationIds = new LinkedList<String>();
	protected final List<String> approverUserIds = new LinkedList<String>();
	
	protected void init(final Map<String, Object> bindingMap) {
		
	}

	protected AbstractApproverAssociationIdentifier() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	protected List<ApproverAssociationEntity> getDefaultApproverAssociations() {
		return approverAssociationDAO.getByAssociation(defaultApproverAssociationResourceId, AssociationType.RESOURCE);
	}
	

	public List<String> getApproverAssociationIds() {
		return approverAssociationIds;
	}
	
	public List<String> getApproverIds() {
		return approverUserIds;
	}
}