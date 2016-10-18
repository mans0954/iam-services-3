package org.openiam.base;

import org.openiam.idm.srvc.property.service.PropertyValueSweeper;
import org.openiam.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Value("${openiam.dbType}")
    private String dbType;
	
	@Autowired
	private PropertyValueSweeper propertyValueSweeper;
	
	@Value("${org.openiam.default.auth.provider.id}")
	private String defaultAuthProviderId;
	
	@Value("${org.openiam.core.login.login.module.default}")
	private String defaultLoginModule;
	
	@Value("${openiam.development_mode}")
    protected Boolean developmentMode;
	
    @Value("${org.openiam.idm.system.user.id}")
    private String systemUserId;

    public String getAffiliationPrimaryTypeId() {
        return propertyValueSweeper.getString("org.openiam.affiliation.primary.type.id");
    }

    public boolean isCaseInSensitiveDatabase() {
    	return "ORACLE_INSENSITIVE".equalsIgnoreCase(dbType);
    }
    
    public String getSystemUserId() {
    	return systemUserId;
    }

    public String getDefaultManagedSysId() {
    	return propertyValueSweeper.getString("openiam.default_managed_sys");
    }

    public Boolean isDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Boolean developmentMode) {
        this.developmentMode = developmentMode;
    }

    public Boolean isProvisionServiceFlag() {
        return propertyValueSweeper.getBoolean("org.openiam.provision.service.flag");
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

	public String getProjectVersion() {
		return  SystemUtils.getManifestInfo(this.getClass(), "Openiam-Version");
	}
}
