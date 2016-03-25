package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.idm.searchbeans.AbstractKeyNameSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "ProvisionConnectorSearchBean", propOrder = {
        "typeId"
})
public class ProvisionConnectorSearchBean extends AbstractKeyNameSearchBean<ProvisionConnectorDto, String> implements SearchBean {
	private String typeId;

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}


	@Override
	public String getCacheUniqueBeanKey() {
		return new StringBuilder()
				.append(getKey() != null ? getKey() : "")
				.toString();	}
}
