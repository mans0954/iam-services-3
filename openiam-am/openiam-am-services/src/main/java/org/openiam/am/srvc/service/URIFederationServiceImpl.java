package org.openiam.am.srvc.service;

import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.springframework.beans.factory.InitializingBean;

public class URIFederationServiceImpl implements URIFederationService, InitializingBean {

	private ContentProviderTree contentProviderTree;
	
	public void sweep() {
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
