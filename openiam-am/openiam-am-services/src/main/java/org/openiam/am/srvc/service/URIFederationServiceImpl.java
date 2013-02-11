package org.openiam.am.srvc.service;

import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.springframework.beans.factory.InitializingBean;

public class URIFederationServiceImpl implements URIFederationService, InitializingBean {

	private ContentProviderTree contentProviderTree;
	
	public void sweep() {
		final ContentProviderTree tempTree = new ContentProviderTree();
		
		synchronized(this) {
			contentProviderTree = tempTree;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
	}
}
