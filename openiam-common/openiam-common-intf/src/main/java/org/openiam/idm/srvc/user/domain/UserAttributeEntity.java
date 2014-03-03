package org.openiam.idm.srvc.user.domain;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER_ATTRIBUTES")
@DozerDTOCorrespondence(UserAttribute.class)
public class UserAttributeEntity implements Serializable {
    private static final long serialVersionUID = 6695609793883291213L;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", length = 32, nullable = false)
    private String id;

    /*
    @Column(name = "METADATA_ID", length = 20)
    private String metadataElementId;
	*/

    @Column(name = "NAME", length = 50)
    private String name;

    /*
    @Column(name = "USER_ID", length = 32)
    private String userId;
    */

    @Column(name = "VALUE", length = 1000)
    private String value;

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
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = false, nullable=true)
    private MetadataElementEntity element;

    public UserAttributeEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*
    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    */

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public void setMultivalued(boolean multivalued) {
        isMultivalued = multivalued;
    }

    public MetadataElementEntity getElement() {
		return element;
	}

	public void setElement(MetadataElementEntity element) {
		this.element = element;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAttributeEntity that = (UserAttributeEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (isMultivalued != that.isMultivalued) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (isMultivalued ? 1231 : 1237);
        return result;
    }
}
