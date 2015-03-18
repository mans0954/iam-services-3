package org.openiam.bpm.activiti.groovy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openiam.bpm.request.ActivitiRequestDecision;
import org.openiam.bpm.request.GenericWorkflowRequest;
import org.openiam.bpm.util.ActivitiRequestType;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
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

	@Value("${org.openiam.idm.activiti.merge.custom.approver.with.approver.associations}")
	protected Boolean mergeCustomApproverIdsWithApproverAssociations;
	
	protected static Logger LOG = Logger.getLogger(AbstractApproverAssociationIdentifier.class);
	
	protected final List<String> approverAssociationIds = new LinkedList<String>();
	protected final List<String> approverUserIds = new LinkedList<String>();
	
	@Autowired
	protected PropertyValueSweeper propertyValueSweeper;
	
	protected String getDefaultApproverAssociationResourceId() {
		return propertyValueSweeper.getString("org.openiam.idm.activiti.default.approver.association.resource.id");
	}
	
	protected void init(final Map<String, Object> bindingMap) {
		
	}
	
	protected void postInit() {
		
	}

	protected AbstractApproverAssociationIdentifier() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	protected List<ApproverAssociationEntity> getDefaultApproverAssociations() {
		return approverAssociationDAO.getByAssociation(getDefaultApproverAssociationResourceId(), AssociationType.RESOURCE);
	}
	

	public List<String> getApproverAssociationIds() {
		return approverAssociationIds;
	}
	
	public List<String> getApproverIds() {
		return approverUserIds;
	}
	
	protected boolean isRequestForEntityCreation(final GenericWorkflowRequest request) {
		boolean retVal = false;
		if(request.getActivitiRequestType() != null) {
			switch(ActivitiRequestType.getByName(request.getActivitiRequestType())) {
				case NEW_GROUP:
					retVal = true;
					break;
				case NEW_ROLE:
					retVal = true;
					break;
				case NEW_ORGANIZATION:
					retVal = true;
					break;
				case NEW_RESOURCE:
					retVal = true;
					break;
				default:
					break;
			}
		}
		return retVal;
	}
	
	protected Set<String> getApproversForEntityCreation(final GenericWorkflowRequest request) {
		final Set<String> approvers = new HashSet<String>();
		final String defaultApproverUserId = propertyValueSweeper.getString("org.openiam.idm.activiti.default.approver.user");
		if(defaultApproverUserId != null) {
			approvers.add(defaultApproverUserId);
		}
		return approvers;
	}
	
	/**
	 * Returns custom attributes to Activit that will be passed to the workflow 
	 * @return
	 */
	public Map<String, Object> getCustomActivitiAttributes() {
		return null;
	}
}
