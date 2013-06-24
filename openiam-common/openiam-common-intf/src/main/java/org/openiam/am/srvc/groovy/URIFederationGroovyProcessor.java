package org.openiam.am.srvc.groovy;

import java.net.URI;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.springframework.context.ApplicationContext;

public abstract class URIFederationGroovyProcessor {
	
	protected ApplicationContext context;
	
	protected URIFederationGroovyProcessor() {
		
	}
	
	public void setContext(final ApplicationContext context) {
		this.context = context;
	}

	public abstract String getValue(final String userId, 
									final ContentProvider contentProvider, 
									final URIPattern pattern, 
									final URIPatternMetaValue metaValue,
									final URI uri);
}
