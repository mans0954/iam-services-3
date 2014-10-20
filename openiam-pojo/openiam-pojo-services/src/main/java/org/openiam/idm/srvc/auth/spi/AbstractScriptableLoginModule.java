package org.openiam.idm.srvc.auth.spi;

import org.openiam.util.SpringContextProvider;

public abstract class AbstractScriptableLoginModule extends AbstractLoginModule {

	public AbstractScriptableLoginModule() {
		super();
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
}
