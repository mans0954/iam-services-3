package org.openiam.idm.srvc.continfo.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Store;
import org.openiam.core.dao.lucene.bridge.UserBridge;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by vitalia on 5/31/16.
 */

@Entity
@Table(name = "EMAIL")
public class EmailEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "EMAIL_ID")
    private String emailId;

//    @Column(name = "EMAIL_NAME")
//    private String name;

    @Column(name = "EMAIL_BODY")
    private String emailBody;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "SUBJECT")
    private String subject;


    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    @Field(name="parent", bridge=@FieldBridge(impl=UserBridge.class), store= Store.YES)
    private UserEntity parent;


    public EmailEntity() {
    }

    public String getEmailId() {

        return emailId;
    }


    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public UserEntity getParent() {
        return parent;
    }

    public void setParent(UserEntity parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailEntity)) return false;

        EmailEntity that = (EmailEntity) o;

        if (getEmailId() != null ? !getEmailId().equals(that.getEmailId()) : that.getEmailId() != null) return false;
        if (getEmailBody() != null ? !getEmailBody().equals(that.getEmailBody()) : that.getEmailBody() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(that.getAddress()) : that.getAddress() != null) return false;
        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
        if (getTimeStamp() != null ? !getTimeStamp().equals(that.getTimeStamp()) : that.getTimeStamp() != null)
            return false;
        return !(getParent() != null ? !getParent().equals(that.getParent()) : that.getParent() != null);

    }

    @Override
    public int hashCode() {
        int result = getEmailId() != null ? getEmailId().hashCode() : 0;
        result = 31 * result + (getEmailBody() != null ? getEmailBody().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getTimeStamp() != null ? getTimeStamp().hashCode() : 0);
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        return result;
    }
}
