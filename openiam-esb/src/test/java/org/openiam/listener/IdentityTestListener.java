package org.openiam.listener;

import org.openiam.test.config.IdentityHolder;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class IdentityTestListener implements IInvokedMethodListener {

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		IdentityHolder.getInstance().setUserId("3000");
		IdentityHolder.getInstance().setPrincipal("sysadmin");
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		IdentityHolder.remove();
	}

}
