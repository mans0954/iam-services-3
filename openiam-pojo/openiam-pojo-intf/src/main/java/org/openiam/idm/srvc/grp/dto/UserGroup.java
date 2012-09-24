package org.openiam.idm.srvc.grp.dto;

// Generated Jul 18, 2009 8:49:09 AM by Hibernate Tools 3.2.2.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

/**
 * Object used for representing the Group-User relationship
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usergroup", propOrder = {
        "userGrpId",
        "grpId",
        "userId",
        "createDate",
        "status",
        "createdBy"
})
@Entity
@Table(name="USER_GRP")
public class UserGroup implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5686110876382504665L;
    protected String userGrpId;
    protected String grpId;
    protected String userId;
    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;

    public UserGroup() {
    }

    public UserGroup(String grpId, String userId) {
        this.grpId = grpId;
        this.userId = userId;
        status = "ACTIVE";
        createDate = new Date(System.currentTimeMillis());
    }

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name="USER_GRP_ID", length=32)
    public String getUserGrpId() {
        return this.userGrpId;
    }

    public void setUserGrpId(String userGrpId) {
        this.userGrpId = userGrpId;
    }

    @Column(name="GRP_ID",length=32,nullable=false)
    public String getGrpId() {
        return this.grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    @Column(name="USER_ID",length=32,nullable=false)
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="STATUS",length=20,nullable=false)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name="CREATE_DATE",length=19)
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name="CREATED_BY",length=20)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
