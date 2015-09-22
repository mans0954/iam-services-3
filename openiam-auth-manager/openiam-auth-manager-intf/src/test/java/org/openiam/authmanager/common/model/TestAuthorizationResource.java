package org.openiam.authmanager.common.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openiam.authmanager.common.xref.ResourceUserXref;

public class TestAuthorizationResource {
	
	private static final int RIGHT_BIT = 3;
	private static final int NUM_OF_RIGHTS = 3;
	private static final int RESOURCE_BIT = 6;

	private String random() {
		return RandomStringUtils.randomAlphanumeric(5);
	}
	
	@Test
	public void testResourceBitset() {
		final AuthorizationUser user = new AuthorizationUser();
		
		final ResourceUserXref xref = new ResourceUserXref();
		final AuthorizationAccessRight right = new AuthorizationAccessRight().setId(random()).setBitIdx(RIGHT_BIT);
		final AuthorizationResource resource = (AuthorizationResource)new AuthorizationResource().setBitSetIdx(RESOURCE_BIT).setId(random()).setName(random());
		xref.addRight(right);
		xref.setUser(user);
		xref.setResource(resource);
		user.addResource(xref);
		user.compile(NUM_OF_RIGHTS, 0);
		
		Assert.assertTrue(CollectionUtils.isNotEmpty(user.getLinearResources()));
		Assert.assertTrue(user.getLinearResources().contains(getResourceBit()));
		Assert.assertTrue(user.getLinearResources().contains(getRightBit()));
		
		user.getLinearResources().forEach(bit -> {
			if(bit.equals(new Integer(getResourceBit()))) {
				Assert.assertNotNull(AbstractAuthorizationEntity.getEntityBit(bit, NUM_OF_RIGHTS));
				Assert.assertEquals(AbstractAuthorizationEntity.getRightBit(bit, resource, NUM_OF_RIGHTS), 0);
				Assert.assertEquals(Integer.valueOf(RESOURCE_BIT), AbstractAuthorizationEntity.getEntityBit(bit, NUM_OF_RIGHTS));
			} else if(bit.equals(new Integer(getRightBit()))) {
				Assert.assertEquals(AbstractAuthorizationEntity.getRightBit(bit, resource, NUM_OF_RIGHTS), RIGHT_BIT);
				Assert.assertNull(AbstractAuthorizationEntity.getEntityBit(bit, NUM_OF_RIGHTS));
			}
		});
		
	}
	
	private int getResourceBit() {
		return RESOURCE_BIT * (NUM_OF_RIGHTS + 1);
	}
	
	private int getRightBit() {
		return getResourceBit() + RIGHT_BIT;
	}
}
