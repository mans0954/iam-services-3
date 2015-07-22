package org.openiam.service.integration.provisioning;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.openiam.service.integration.entitlements.ApproverAssociationServiceTest;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by anton on 22.07.15.
 */
public class ApproverAssocationSearchBeanServiseTest extends ApproverAssociationServiceTest {

    private String apprAssocId = null;
    private ApproverAssociation approverAssociationFirst;

    @BeforeClass(alwaysRun = true)
    public void first() {
        createApproverAssociation();
    }

    @AfterClass(alwaysRun = true)
    public void last() {
        deleteApproverAssociation();
    }

    private void createApproverAssociation() {
        ApproverAssociation approverAssociation = new ApproverAssociation();
        approverAssociation = newInstance();
        approverAssociation.setApproverEntityId("TEST_APPR_ENTITY_ID");
        approverAssociation.setOnApproveEntityId("TEST_ONAPPR_ENTITY_ID");
        approverAssociation.setOnRejectEntityId("TEST_ONREJ_ENTITY_ID");

        Response response = save(approverAssociation);
        approverAssociationFirst = approverAssociation;

        Assert.assertNotNull(response, "Response can not be null");
        Assert.assertTrue(response.isSuccess(), "Response should be successful");

    }

    @Test
    public void searchApproverAssociation() throws Exception {
        ApproverAssocationSearchBean searchBean = newSearchBean();
        searchBean.setApproverEntityId("TEST_APPR_ENTITY_ID");
        searchBean.setApproverEntityType(AssociationType.RESOURCE);
        searchBean.setOnApproveEntityId("TEST_ONAPPR_ENTITY_ID");
        searchBean.setOnApproveEntityType(AssociationType.RESOURCE);
        searchBean.setOnRejectEntityId("TEST_ONREJ_ENTITY_ID");
        searchBean.setOnRejectEntityType(AssociationType.RESOURCE);

        List<ApproverAssociation> apprAssocList = find(searchBean, 0, Integer.MAX_VALUE);
        ApproverAssociation approverAssociation = (CollectionUtils.isNotEmpty(apprAssocList)) ? apprAssocList.get(0) : null;

        Assert.assertNotNull(approverAssociation);

        Assert.assertTrue(approverAssociation.getApproverEntityId().equals(searchBean.getApproverEntityId()) &&
                approverAssociation.getApproverEntityType() == searchBean.getApproverEntityType() &&
                approverAssociation.getOnApproveEntityId().equals(searchBean.getOnApproveEntityId()) &&
                approverAssociation.getOnApproveEntityType() == searchBean.getOnApproveEntityType() &&
                approverAssociation.getOnRejectEntityId().equals(searchBean.getOnRejectEntityId()) &&
                approverAssociation.getOnRejectEntityType() == searchBean.getOnRejectEntityType());

        apprAssocId = approverAssociation.getId();

    }

    private void deleteApproverAssociation() {
        ApproverAssocationSearchBean searchBean = new ApproverAssocationSearchBean();
        searchBean.setId(apprAssocId);

        List<ApproverAssociation> approverAssociationList = find(searchBean, 0, Integer.MAX_VALUE);
        ApproverAssociation approverAssociation = (CollectionUtils.isNotEmpty(approverAssociationList)) ? approverAssociationList.get(0) : null;

        if (approverAssociation != null) {
            Response response = delete(approverAssociation);
            Assert.assertTrue(response.isSuccess());
        }
    }
}
