package org.openiam.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Obtains configuration information for password change and reset from spring
 * configuration files.
 * 
 * @author suneet
 * @version 2
 */
@Component("sysConfiguration")
public class SysConfiguration {
	
	
	@Value("${openiam.default_managed_sys}")
    protected String defaultManagedSysId;
	
	@Value("${org.openiam.default.auth.provider.id}")
	private String defaultAuthProviderId;
	
	@Value("${org.openiam.core.login.login.module.default}")
	private String defaultLoginModule;
	
	@Value("${openiam.development_mode}")
    protected Boolean developmentMode;
	
	@Value("${org.openiam.provision.service.flag}")
    protected boolean provisionServiceFlag = true;

    public String getDefaultManagedSysId() {
        return defaultManagedSysId;
    }

    public void setDefaultManagedSysId(String defaultManagedSysId) {
        this.defaultManagedSysId = defaultManagedSysId;
    }

    public Boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public Boolean isProvisionServiceFlag() {
        return provisionServiceFlag;
    }

    public void setProvisionServiceFlag(boolean provisionServiceFlag) {
        this.provisionServiceFlag = provisionServiceFlag;
    }

	public String getDefaultAuthProviderId() {
		return defaultAuthProviderId;
	}

	public void setDefaultAuthProviderId(String defaultAuthProviderId) {
		this.defaultAuthProviderId = defaultAuthProviderId;
	}

	public Boolean getDevelopmentMode() {
		return developmentMode;
	}

	public String getDefaultLoginModule() {
		return defaultLoginModule;
	}

	public void setDefaultLoginModule(String defaultLoginModule) {
		this.defaultLoginModule = defaultLoginModule;
	}
    
    
}
