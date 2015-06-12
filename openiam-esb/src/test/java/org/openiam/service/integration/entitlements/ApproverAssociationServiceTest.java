package org.openiam.service.integration.entitlements;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ApproverAssociationServiceTest extends AbstractKeyServiceTest<ApproverAssociation, ApproverAssocationSearchBean> {
	
    @Autowired
    @Qualifier("managedSysServiceClient")
    private ManagedSystemWebService managedSysServiceClient;

	@Override
	protected ApproverAssociation newInstance() {
		final ApproverAssociation association = new ApproverAssociation();
		association.setApproverEntityId("abc");
		association.setApproverEntityType(AssociationType.RESOURCE);
		association.setApproverLevel(0);
		association.setOnApproveEntityId("abbc");
		association.setOnApproveEntityType(AssociationType.RESOURCE);
		association.setOnRejectEntityId("abc");
		association.setOnRejectEntityType(AssociationType.RESOURCE);
		return association;
	}

	@Override
	protected ApproverAssocationSearchBean newSearchBean() {
		return new ApproverAssocationSearchBean();
	}

	@Override
	protected Response save(ApproverAssociation t) {
		return managedSysServiceClient.saveApproverAssociation(t);
	}

	@Override
	protected Response delete(ApproverAssociation t) {
		return managedSysServiceClient.removeApproverAssociation(t.getId());
	}

	@Override
	protected ApproverAssociation get(String key) {
		final ApproverAssocationSearchBean searchBean = newSearchBean();
		searchBean.setKey(key);
		final List<ApproverAssociation> associations = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(associations)) ? associations.get(0) : null;
	}

	@Override
	public List<ApproverAssociation> find(
			ApproverAssocationSearchBean searchBean, int from, int size) {
		return managedSysServiceClient.getApproverAssociations(searchBean, from, size);
	}

	@Override
	protected String getId(ApproverAssociation bean) {
		return bean.getId();
	}

	@Override
	protected void setId(ApproverAssociation bean, String id) {
		bean.setId(id);
	}

}
