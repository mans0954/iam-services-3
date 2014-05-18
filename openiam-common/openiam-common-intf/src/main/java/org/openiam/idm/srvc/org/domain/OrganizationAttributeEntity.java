package org.openiam.idm.srvc.org.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMPANY_ATTRIBUTE")
@DozerDTOCorrespondence(OrganizationAttribute.class)
public class OrganizationAttributeEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "COMPANY_ATTR_ID", length = 32, nullable = false)
    private String id;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},fetch=FetchType.LAZY)
    @JoinColumn(name = "METADATA_ID", insertable = true, updatable = true, nullable=true)
    private MetadataElementEntity element;
    
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false)
    private OrganizationEntity organization;

    @Column(name = "ATTR_VALUE", length=4096)
    private String value;

    @ElementCollection
    @CollectionTable(name="COMPANY_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="COMPANY_ATTRIBUTE_ID", referencedColumnName="COMPANY_ATTR_ID"))
    @Column(name="VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    @Column(name = "NAME", length = 100)
    private String name;

    public OrganizationAttributeEntity() {
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean getIsMultivalued() {
        return isMultivalued;
    }

    public void setIsMultivalued(boolean isMultivalued) {
        this.isMultivalued = isMultivalued;
    }

    public MetadataElementEntity getElement() {
		return element;
	}

	public void setElement(MetadataElementEntity element) {
		this.element = element;
	}

	public OrganizationEntity getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + (isMultivalued ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrganizationAttributeEntity other = (OrganizationAttributeEntity) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
        if (isMultivalued != other.isMultivalued) return false;
		return true;
	}
	
	
}
