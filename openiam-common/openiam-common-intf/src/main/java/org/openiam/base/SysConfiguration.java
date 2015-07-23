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
    protected String defaultAuthPolicyId = null;
    protected String defaultPswdPolicyId = null;
    protected Boolean developmentMode = false;
    protected boolean provisionServiceFlag = true;
    protected String affiliationPrimaryTypeId;
    protected String affiliationDefaultTypeId;


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

    public String getAffiliationPrimaryTypeId() {
        return affiliationPrimaryTypeId;
    }

    public void setAffiliationPrimaryTypeId(String affiliationPrimaryTypeId) {
        this.affiliationPrimaryTypeId = affiliationPrimaryTypeId;
    }

    public String getAffiliationDefaultTypeId() {
        return affiliationDefaultTypeId;
    }

    public void setAffiliationDefaultTypeId(String affiliationDefaultTypeId) {
        this.affiliationDefaultTypeId = affiliationDefaultTypeId;
    }
}
