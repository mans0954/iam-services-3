package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.org.dto.Organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "organizationName",
		"typeId",
		"classification"
})
public class OrganizationSearchBean extends AbstractSearchBean<Organization, String> implements SearchBean<Organization, String>,
        Serializable {
    private static final long serialVersionUID = 1L;

    private String organizationName;
    private String typeId;
    private String classification;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
}
