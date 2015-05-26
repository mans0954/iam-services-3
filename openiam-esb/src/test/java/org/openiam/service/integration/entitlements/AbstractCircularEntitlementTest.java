package org.openiam.service.integration.entitlements;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.service.integration.AbstractEntitlementsTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractCircularEntitlementTest<Parent extends KeyDTO> extends AbstractEntitlementsTest<Parent, Parent> {

	@Test
	public void testCircularDependency() {
		Parent entity1 = null;
		Parent entity2 = null;
		Parent entity3 = null;
		Response response = null;
		try {
			entity1 = createParent();
			entity2 = createParent();
			entity3 = createParent();
			doAddChildToParent(entity1, entity2, null);
			doAddChildToParent(entity2, entity3, null);
			doAddChildToParent(entity1, entity3, null);
			response = addChildToParent(entity3, entity1, null);
			Assert.assertTrue(response.isFailure(), "Adding a child should have triggered a circular dependency");
			Assert.assertEquals(ResponseCode.CIRCULAR_DEPENDENCY, response.getErrorCode());
			response = addChildToParent(entity3, entity2, null);
			Assert.assertTrue(response.isFailure(), "Adding a child should have triggered a circular dependency");
			Assert.assertEquals(ResponseCode.CIRCULAR_DEPENDENCY, response.getErrorCode());
		} finally {
			if(entity1 != null) {
				response = deleteParent(entity1);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			if(entity2 != null) {
				response = deleteParent(entity2);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
			if(entity3 != null) {
				response = deleteParent(entity3);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
		}
	}
	
	@Test
	public void testAddingToItself() {
		Parent entity1 = null;
		Response response = null;
		try {
			entity1 = createParent();
			response = addChildToParent(entity1, entity1, null);
			Assert.assertTrue(response.isFailure(), "Adding a child to itself should have failed");
			Assert.assertEquals(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD, response.getErrorCode());
		} finally {
			if(entity1 != null) {
				response = deleteParent(entity1);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
		}
	}
}
