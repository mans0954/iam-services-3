package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.domain.RequestAttributeEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestAttribute", propOrder = {
    "id",
    "provRequestId",
    "name",
    "value",
    "metadataTypeId"
})
@DozerDTOCorrespondence(RequestAttributeEntity.class)
public class RequestAttribute implements java.io.Serializable {

	private String id;
	private String provRequestId;
	private String name;
	private String value;
	private String metadataTypeId;

	public RequestAttribute() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getProvRequestId() {
		return provRequestId;
	}



	public void setProvRequestId(String provRequestId) {
		this.provRequestId = provRequestId;
	}



	public String getMetadataTypeId() {
		return metadataTypeId;
	}



	public void setMetadataTypeId(String metadataTypeId) {
		this.metadataTypeId = metadataTypeId;
	}

}
