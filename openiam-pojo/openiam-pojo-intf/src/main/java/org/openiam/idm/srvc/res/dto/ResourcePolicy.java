package org.openiam.idm.srvc.res.dto;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

/**
 * Object representing the association between a resource and a policy.
 */
@Entity
@Table(name="RESOURCE_POLICY")
public class ResourcePolicy implements java.io.Serializable {

    private String resourcePolicyId;
    private String resourceId;
    private String roleId;
    private Date policyStart;
    private Date policyEnd;
    private Integer applyToChildren;

    public ResourcePolicy() {
    }

    public ResourcePolicy(String resourcePolicyId) {
        this.resourcePolicyId = resourcePolicyId;
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="RESOURCE_POLICY_ID", length=32)
    public String getResourcePolicyId() {
        return this.resourcePolicyId;
    }

    public void setResourcePolicyId(String resourcePolicyId) {
        this.resourcePolicyId = resourcePolicyId;
    }

    @Column(name="RESOURCE_ID",length=32)
    public String getResourceId() {
    	return resourceId;
    }
    
    public void setResourceId(final String resourceId) {
    	this.resourceId = resourceId;
    }

    @Column(name="ROLE_ID",length=20)
    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name="POLICY_START",length=19)
    public Date getPolicyStart() {
        return this.policyStart;
    }

    public void setPolicyStart(Date policyStart) {
        this.policyStart = policyStart;
    }

    @Column(name="POLICY_END",length=19)
    public Date getPolicyEnd() {
        return this.policyEnd;
    }

    public void setPolicyEnd(Date policyEnd) {
        this.policyEnd = policyEnd;
    }

    @Column(name="APPLY_TO_CHILDREN")
    public Integer getApplyToChildren() {
        return this.applyToChildren;
    }

    public void setApplyToChildren(Integer applyToChildren) {
        this.applyToChildren = applyToChildren;
    }

}
