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

    @Column(name = "USER_ID")
    private String userId;
    
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
    }

    @Override
    public int hashCode() {
		final int prime = 31;
        int result = super.hashCode();
		result = prime * result + (isMultivalued ? 1231 : 1237);
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserAttributeEntity other = (UserAttributeEntity) obj;
		if (isMultivalued != other.isMultivalued)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("UserAttributeEntity [values=%s, isMultivalued=%s, userId=%s, toString()=%s]",
						values, isMultivalued, userId, super.toString());
	}

	public void copyValues(UserAttribute userAttr) {
		setName(userAttr.getName());
		setIsMultivalued(userAttr.getIsMultivalued());
		setValue(userAttr.getValue());
		setValues(userAttr.getValues());
	}
}
