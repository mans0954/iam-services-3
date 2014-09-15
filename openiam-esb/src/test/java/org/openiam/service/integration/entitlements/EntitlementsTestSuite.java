package org.openiam.service.integration.entitlements;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	GroupServiceTest.class,
	ResourceServiceTest.class,
	RoleServiceTest.class,
	OrganizationServiceTest.class
})
public class EntitlementsTestSuite {

}
