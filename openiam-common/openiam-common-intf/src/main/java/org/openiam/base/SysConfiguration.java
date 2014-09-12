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
	
	@Value("${org.openiam.default.auth.policy}")
    protected String defaultAuthPolicyId;
	
	@Value("${org.openiam.default.password.policy}")
    protected String defaultPswdPolicyId;
	
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

    public String getDefaultAuthPolicyId() {
        return defaultAuthPolicyId;
    }

    public void setDefaultAuthPolicyId(String defaultAuthPolicyId) {
        this.defaultAuthPolicyId = defaultAuthPolicyId;
    }

    public String getDefaultPswdPolicyId() {
        return defaultPswdPolicyId;
    }

    public void setDefaultPswdPolicyId(String defaultPswdPolicyId) {
        this.defaultPswdPolicyId = defaultPswdPolicyId;
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
}
