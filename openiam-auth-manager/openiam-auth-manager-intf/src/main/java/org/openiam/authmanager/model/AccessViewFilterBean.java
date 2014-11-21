package org.openiam.authmanager.model;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by: Alexander Duckardt
 * Date: 1/11/14.
 */
public class AccessViewFilterBean implements Serializable {
    private String userId;
    private String name;
    private String description;
    private String risk;
    private Boolean showExceptionsFlag=false;
    private Boolean showRolesFlag=false;
    private Boolean showGroupsFlag=false;
    private Boolean showManagesSysFlag=false;
    private int compiledFlag =0;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getShowRolesFlag() {
        return showRolesFlag;
    }

    public void setShowRolesFlag(Boolean showRolesFlag) {
        this.showRolesFlag = showRolesFlag;
    }

    public Boolean getShowGroupsFlag() {
        return showGroupsFlag;
    }

    public void setShowGroupsFlag(Boolean showGroupsFlag) {
        this.showGroupsFlag = showGroupsFlag;
    }

    public Boolean getShowManagesSysFlag() {
        return showManagesSysFlag;
    }

    public void setShowManagesSysFlag(Boolean showManagesSysFlag) {
        this.showManagesSysFlag = showManagesSysFlag;
    }

    public Boolean getShowExceptionsFlag() {
        return showExceptionsFlag;
    }

    public void setShowExceptionsFlag(Boolean showExceptionsFlag) {
        this.showExceptionsFlag = showExceptionsFlag;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public void computeCompiledFlag(){
        compiledFlag = 0;

        if(showRolesFlag!=null && showRolesFlag)
            compiledFlag+=1;
        if(showGroupsFlag!=null && showGroupsFlag)
            compiledFlag+=2;
        if(showManagesSysFlag!=null && showManagesSysFlag)
            compiledFlag+=4;
    }

    public int getCompiledFlag(){
        return compiledFlag;
    }


    public boolean isEmpty(){
        return StringUtils.isBlank(this.name) && StringUtils.isBlank(this.description);
    }
}
