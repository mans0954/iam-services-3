package org.openiam.idm.srvc.org.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

@Entity
@Table(name = "COMPANY_ATTRIBUTE")
@DozerDTOCorrespondence(OrganizationAttribute.class)
public class OrganizationAttributeEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "COMPANY_ATTR_ID", length = 32, nullable = false)
    private String attrId;

    @Column(name = "METADATA_ID", length = 20)
    private String metadataElementId;

    @Column(name = "NAME", length = 20)
    private String name;

    @Column(name = "COMPANY_ID", length = 32)
    private String organizationId;

    @Column(name = "VALUE")
    private String value;

    public OrganizationAttributeEntity() {
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getMetadataElementId() {
        return metadataElementId;
    }

    public void setMetadataElementId(String metadataElementId) {
        this.metadataElementId = metadataElementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationAttributeEntity that = (OrganizationAttributeEntity) o;

        if (attrId != null ? !attrId.equals(that.attrId) : that.attrId != null) return false;
        if (metadataElementId != null ? !metadataElementId.equals(that.metadataElementId) : that.metadataElementId != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (organizationId != null ? !organizationId.equals(that.organizationId) : that.organizationId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attrId != null ? attrId.hashCode() : 0;
        result = 31 * result + (metadataElementId != null ? metadataElementId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (organizationId != null ? organizationId.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
