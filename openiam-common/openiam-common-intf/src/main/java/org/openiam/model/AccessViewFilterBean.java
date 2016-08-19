package org.openiam.model;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 1/11/14.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessViewFilterBean", propOrder = {
        "userId",
        "name",
        "description",
        "risk",
        "maxHierarchyLevel",
        "showExceptionsFlag",
        "showRolesFlag",
        "showGroupsFlag",
        "showManagesSysFlag",
        "compiledFlag",
        "attestationTaskId"
})
public class AccessViewFilterBean implements Serializable {
    private String userId;
    private String name;
    private String description;
    private String risk;
    private String attestationTaskId;
    private Integer maxHierarchyLevel = 10;
    private Boolean showExceptionsFlag=false;
    private Boolean showRolesFlag=false;
    private Boolean showGroupsFlag=false;
    private Boolean showManagesSysFlag=false;
    private int compiledFlag =0;
    @XmlTransient
    private Set<String> attestationManagedSysFilter;

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

    public Integer getMaxHierarchyLevel() {
        return maxHierarchyLevel;
    }

    public void setMaxHierarchyLevel(Integer maxHierarchyLevel) {
        this.maxHierarchyLevel = maxHierarchyLevel;
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

    public String getAttestationTaskId() {
        return attestationTaskId;
    }

    public void setAttestationTaskId(String attestationTaskId) {
        this.attestationTaskId = attestationTaskId;
    }

    public Set<String> getAttestationManagedSysFilter() {
        return attestationManagedSysFilter;
    }

    public void setAttestationManagedSysFilter(Set<String> attestationManagedSysFilter) {
        this.attestationManagedSysFilter = attestationManagedSysFilter;
    }
}
