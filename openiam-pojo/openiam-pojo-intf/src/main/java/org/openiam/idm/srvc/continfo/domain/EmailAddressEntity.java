package org.openiam.idm.srvc.continfo.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_ADDRESS")
@DozerDTOCorrespondence(EmailAddress.class)
public class EmailAddressEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "EMAIL_ID", length = 32, nullable = false)
    private String emailId;

    @Column(name = "ACTIVE")
    @Type(type = "yes_no")
    private boolean isActive = true;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "EMAIL_ADDRESS", length = 320)
    private String emailAddress;

    @Column(name = "IS_DEFAULT")
    @Type(type = "yes_no")
    private boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private UserEntity parent;

    @Column(name = "PARENT_TYPE", length = 30)
    private String parentType;

    @Column(name = "NAME", length = 40)
    private String name;

    public EmailAddressEntity() {
    }

//    public EmailAddressEntity(final EmailAddress emailAddress, final UserEntity parent) {
//        this.emailId = emailAddress.getEmailId();
//        this.isActive = emailAddress.isActive();
//        this.description = emailAddress.getDescription();
//        this.isDefault = emailAddress.getIsDefault();
//        this.parentType = emailAddress.getParentType();
//        this.name = emailAddress.getName();
//        this.parent = parent;
//    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UserEntity getParent() {
        return parent;
    }

    public void setParent(UserEntity parent) {
        this.parent = parent;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAddressEntity that = (EmailAddressEntity) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (emailId != null ? !emailId.equals(that.emailId) : that.emailId != null) return false;
        if (isActive != that.isActive) return false;
        if (isDefault != that.isDefault) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (parentType != null ? !parentType.equals(that.parentType) : that.parentType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = emailId != null ? emailId.hashCode() : 0;
        result = 31 * result + Boolean.valueOf(isActive).hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        result = 31 * result +  Boolean.valueOf(isDefault).hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (parentType != null ? parentType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
