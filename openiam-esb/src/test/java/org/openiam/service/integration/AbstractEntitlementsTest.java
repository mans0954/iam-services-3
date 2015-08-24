package org.openiam.service.integration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractEntitlementsTest<Parent extends KeyDTO, Child extends KeyDTO> extends AbstractServiceTest {

	@Test
	public void testDeleteWithRelationship() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()));
			
			if(parent != null) { /* should still work - relationship should be removed */
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			
			Assert.assertNull(getParentById(parent));
			Assert.assertNotNull(getChildById(child));
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
			Assert.assertNull(getChildById(child));
		} finally {
			
		}
	}
	
	@Test
	public void testDeleteWithInverseRelationship() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()));
			
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
			Assert.assertNull(getChildById(child));
			Assert.assertNotNull(getParentById(parent));
			
			if(parent != null) { /* should still work - relationship should be removed */
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			Assert.assertNull(getParentById(parent));
		} finally {
			
		}
	}
	
	@Test
	public void clusterTest() {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddAndRemove(parent, child, null);
			
			final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
			doAddAndRemove(parent, child, rightIds);
		} finally {
			if(parent != null) {
				response = deleteParent(parent);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete parent.  %s", response));
			}
			if(child != null) {
				response = deleteChild(child);
				Assert.assertTrue(response.isSuccess(), String.format("Could not delete child.  %s", response));
			}
		}
	}
	
	protected void doAddAndRemove(final Parent parent, final Child child, final Set<String> rightIds) {
		doAddChildToParent(parent, child, rightIds);
		doRemoveChildFromParent(parent, child, rightIds);
	}
	
	protected void doAddChildToParent(final Parent parent, final Child child, final Set<String> rightIds) {
		Response response = addChildToParent(parent, child, rightIds);
		refreshAuthorizationManager();
		Assert.assertTrue(response.isSuccess(), String.format("Could not add child to parent.  %s", response));
		Assert.assertTrue(isChildInParent(parent, child, rightIds), String.format("Child %s not in parent %s", child, parent));
		Assert.assertTrue(isChildInParent(parent, child, rightIds), String.format("Child %s not in parent %s", child, parent));
		Assert.assertTrue(parentHasChild(parent, child, rightIds), String.format("Parent does not have child", parent, child));
		Assert.assertTrue(parentHasChild(parent, child, rightIds), String.format("Parent does not have child", parent, child));
	}
	
	protected void doRemoveChildFromParent(final Parent parent, final Child child, final Set<String> rightIds) {
		Response response = removeChildFromParent(parent, child);
		refreshAuthorizationManager();
		Assert.assertTrue(response.isSuccess(), String.format("Could remove child from parent.  %s", response));
		Assert.assertFalse(isChildInParent(parent, child, rightIds), String.format("Child %s in parent %s", child, parent));
		Assert.assertFalse(isChildInParent(parent, child, rightIds), String.format("Child %s in parent %s", child, parent));
		Assert.assertFalse(parentHasChild(parent, child, rightIds), String.format("Parent has child", parent, child));
		Assert.assertFalse(parentHasChild(parent, child, rightIds), String.format("Parent has child", parent, child));
	}

	protected abstract Parent getParentById(final Parent parent);
	protected abstract Child getChildById(final Child child);
	protected abstract Parent createParent();
	protected abstract Child createChild();
	protected abstract Response addChildToParent(final Parent parent, final Child child, final Set<String> rights);
	protected abstract Response removeChildFromParent(final Parent parent, final Child child);
	protected abstract Response deleteParent(final Parent parent);
	protected abstract Response deleteChild(final Child child);
	protected abstract boolean isChildInParent(final Parent parent, final Child child, final Set<String> rights);
	protected abstract boolean parentHasChild(final Parent parent, final Child child, final Set<String> rights);
}
