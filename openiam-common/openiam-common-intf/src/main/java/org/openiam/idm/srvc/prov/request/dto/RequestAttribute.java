package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.prov.request.domain.RequestAttributeEntity;

// Generated Jan 9, 2009 5:33:58 PM by Hibernate Tools 3.2.2.GA

/**
 * Object representing attributes of a request. By associating these attributes to Metadata, a request may be extended with new attributes without 
 * having to alter the underlying schema.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestAttribute", propOrder = {
    "id",
    "provRequestId",
    "name",
    "value",
    "metadataTypeId",
    "attrGroup"
})
@DozerDTOCorrespondence(RequestAttributeEntity.class)
public class RequestAttribute implements java.io.Serializable {

	private String id;
	private String provRequestId;
	private String name;
	private String value;
	private String metadataTypeId;
	private String attrGroup;

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


	public String getAttrGroup() {
		return this.attrGroup;
	}

	public void setAttrGroup(String attrGroup) {
		this.attrGroup = attrGroup;
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
