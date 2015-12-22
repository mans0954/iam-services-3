package org.openiam.service.integration;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.openiam.base.KeyDTO;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.testng.Assert;
import org.testng.annotations.Test;

public abstract class AbstractEntitlementsTest<Parent extends KeyDTO, Child extends KeyDTO> extends AbstractServiceTest {

	@Test
	public void testDeleteWithRelationshipNoRange() {
		testDeleteWithRelationship(null, null);
	}
	
	@Test
	public void testDeleteWithRelationshipWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithRelationship(startDate, endDate);
	}
	
	@Test
	public void testDeleteWithRelationshipStartDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithRelationship(startDate, null);
	}
	
	@Test
	public void testDeleteWithRelationshipEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithRelationship(null, endDate);
	}
	
	private void testDeleteWithRelationship(final Date startDate, final Date endDate) {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()), startDate, endDate);
			
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
	public void testDeleteWithInverseRelationshipNoRange() {
		testDeleteWithInverseRelationship(null, null);
	}
	
	@Test
	public void testDeleteWithInverseRelationshipWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithInverseRelationship(startDate, endDate);
	}
	
	@Test
	public void testDeleteWithInverseRelationshipStartDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithInverseRelationship(startDate, null);
	}
	
	@Test
	public void testDeleteWithInverseRelationshipEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		testDeleteWithInverseRelationship(null, endDate);
	}
	
	private void testDeleteWithInverseRelationship(final Date startDate, final Date endDate) {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddChildToParent(parent, child, rights.stream().map(e -> e.getId()).collect(Collectors.toSet()), startDate, endDate);
			
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
	public void clusterTestNoRange() {
		clusterTest(null, null);
	}
	
	@Test
	public void clusterTestWithRange() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		clusterTest(startDate, endDate);
	}
	
	@Test
	public void clusterTestStartDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		clusterTest(startDate, null);
	}
	
	@Test
	public void clusterTestEndDate() {
		final Date startDate = new Date();
		final Date endDate = DateUtils.addDays(startDate, 1);
		clusterTest(null, endDate);
	}
	
	private void clusterTest(final Date startDate, final Date endDate) {
		Parent parent = null;
		Child child = null;
		Response response = null;
		try {
			parent = createParent();
			child = createChild();
			final List<AccessRight> rights = accessRightServiceClient.findBeans(null, 0, 1, getDefaultLanguage());
			doAddAndRemove(parent, child, null, startDate, endDate);
			
			final Set<String> rightIds = rights.stream().map(e -> e.getId()).collect(Collectors.toSet());
			doAddAndRemove(parent, child, rightIds, startDate, endDate);
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
	
	protected void doAddAndRemove(final Parent parent, final Child child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		doAddChildToParent(parent, child, rightIds, startDate, endDate);
		doRemoveChildFromParent(parent, child, rightIds);
	}
	
	protected void doAddChildToParent(final Parent parent, final Child child, final Set<String> rightIds, final Date startDate, final Date endDate) {
		Response response = addChildToParent(parent, child, rightIds, startDate, endDate);
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
	protected abstract Response addChildToParent(final Parent parent, final Child child, final Set<String> rights, final Date startDate, final Date endDate);
	protected abstract Response removeChildFromParent(final Parent parent, final Child child);
	protected abstract Response deleteParent(final Parent parent);
	protected abstract Response deleteChild(final Child child);
	protected abstract boolean isChildInParent(final Parent parent, final Child child, final Set<String> rights);
	protected abstract boolean parentHasChild(final Parent parent, final Child child, final Set<String> rights);
}
