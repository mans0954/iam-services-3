package org.openiam.idm.srvc.user.domain;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.internationalization.Internationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER_ATTRIBUTES")
@DozerDTOCorrespondence(UserAttribute.class)
@Internationalized
@AttributeOverrides(value={
	@AttributeOverride(name = "id", column = @Column(name = "ID")),
	@AttributeOverride(name = "value", column = @Column(name="VALUE", length=4096))
})
public class UserAttributeEntity extends AbstractAttributeEntity {
    private static final long serialVersionUID = 6695609793883291213L;

    @ElementCollection
    @CollectionTable(name="USER_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="USER_ATTRIBUTE_ID", referencedColumnName="ID"))
    @Column(name="VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", insertable = true, updatable = false)
    private UserEntity user;
    
    public UserAttributeEntity() {
    }

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public boolean getIsMultivalued() {
		return isMultivalued;
	}

	public void setIsMultivalued(boolean isMultivalued) {
		this.isMultivalued = isMultivalued;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserAttributeEntity that = (UserAttributeEntity) o;

        if (isMultivalued != that.isMultivalued) return false;
        if (user != null ? !user.getId().equals(that.user.getId()) : that.user != null) return false;
        if (values != null ? !values.equals(that.values) : that.values != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (values != null ? values.hashCode() : 0);
        result = 31 * result + (isMultivalued ? 1 : 0);
        result = 31 * result + (user != null ? user.getId().hashCode() : 0);
        return result;
    }

    @Override
	public String toString() {
		return String
				.format("UserAttributeEntity [values=%s, isMultivalued=%s, user=%s, toString()=%s]",
						values, isMultivalued, user, super.toString());
	}

	public void copyValues(UserAttribute userAttr) {
		setName(userAttr.getName());
		setIsMultivalued(userAttr.getIsMultivalued());
		setValue(userAttr.getValue());
		setValues(userAttr.getValues());
	}
}
