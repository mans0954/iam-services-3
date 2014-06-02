package org.openiam.idm.srvc.org.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.AbstractAttributeEntity;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.internationalization.Internationalized;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMPANY_ATTRIBUTE")
@AttributeOverride(name = "id", column = @Column(name = "COMPANY_ATTR_ID"))
@DozerDTOCorrespondence(OrganizationAttribute.class)
@Internationalized
public class OrganizationAttributeEntity extends AbstractAttributeEntity {
   
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", insertable = true, updatable = false)
    private OrganizationEntity organization;

    @ElementCollection
    @CollectionTable(name="COMPANY_ATTRIBUTE_VALUES", joinColumns=@JoinColumn(name="COMPANY_ATTRIBUTE_ID", referencedColumnName="COMPANY_ATTR_ID"))
    @Column(name="VALUE", length = 255)
    private List<String> values = new ArrayList<String>();

    @Column(name = "IS_MULTIVALUED", nullable = false)
    @Type(type = "yes_no")
    private boolean isMultivalued = false;

    public OrganizationAttributeEntity() {
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

	public OrganizationEntity getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isMultivalued ? 1231 : 1237);
		result = prime * result
				+ ((organization == null) ? 0 : organization.hashCode());
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
		OrganizationAttributeEntity other = (OrganizationAttributeEntity) obj;
		if (isMultivalued != other.isMultivalued)
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	
}
