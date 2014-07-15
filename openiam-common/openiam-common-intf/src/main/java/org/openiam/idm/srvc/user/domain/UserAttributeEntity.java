package org.openiam.idm.srvc.user.domain;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.internationalization.Internationalized;

import java.io.Serializable;
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

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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

	public boolean isMultivalued() {
		return isMultivalued;
	}

	public void setMultivalued(boolean isMultivalued) {
		this.isMultivalued = isMultivalued;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMultivalued ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
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
				.format("UserAttributeEntity [values=%s, isMultivalued=%s, user=%s, toString()=%s]",
						values, isMultivalued, user, super.toString());
	}

    
}
