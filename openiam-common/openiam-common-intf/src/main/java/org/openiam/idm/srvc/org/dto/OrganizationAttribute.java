package org.openiam.idm.srvc.org.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.AbstractAttributeDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "organizationAttribute", propOrder = {
        "organizationId",
        "values",
        "isMultivalued"
})
@DozerDTOCorrespondence(OrganizationAttributeEntity.class)
public class OrganizationAttribute extends AbstractAttributeDTO {

    private static final long serialVersionUID = -231974705360001659L;

    private String organizationId;
    protected List<String> values = new ArrayList<String>();
    protected Boolean isMultivalued = Boolean.FALSE;

    public OrganizationAttribute() {
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Boolean getIsMultivalued() {
        return isMultivalued;
    }

    public void setIsMultivalued(Boolean isMultivalued) {
        this.isMultivalued = isMultivalued;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((isMultivalued == null) ? 0 : isMultivalued.hashCode());
		result = prime * result
				+ ((organizationId == null) ? 0 : organizationId.hashCode());
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
		OrganizationAttribute other = (OrganizationAttribute) obj;
		if (isMultivalued == null) {
			if (other.isMultivalued != null)
				return false;
		} else if (!isMultivalued.equals(other.isMultivalued))
			return false;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	
}
