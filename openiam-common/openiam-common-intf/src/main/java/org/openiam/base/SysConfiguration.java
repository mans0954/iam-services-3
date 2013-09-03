package org.openiam.base;

/**
 * Obtains configuration information for password change and reset from spring
 * configuration files.
 * 
 * @author suneet
 * @version 2
 */
public class SysConfiguration {
    protected String defaultManagedSysId = null;
    protected String defaultSecurityDomain = null;
    protected Boolean developmentMode = false;
    protected boolean provisionServiceFlag = true;

    public String getDefaultManagedSysId() {
        return defaultManagedSysId;
    }

    public void setDefaultManagedSysId(String defaultManagedSysId) {
        this.defaultManagedSysId = defaultManagedSysId;
    }

    public String getDefaultSecurityDomain() {
        return defaultSecurityDomain;
    }

    public void setDefaultSecurityDomain(String defaultSecurityDomain) {
        this.defaultSecurityDomain = defaultSecurityDomain;
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
