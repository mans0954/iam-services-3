package org.openiam.idm.srvc.synch.dto;

import org.openiam.base.KeyDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SynchConfigDataMapping", 
		propOrder = {
		"synchConfigId",
		"idmFieldName", 
		"srcFieldName"})
public class SynchConfigDataMapping extends KeyDTO {

	private String synchConfigId;
	private String idmFieldName;
	private String srcFieldName;

	public SynchConfigDataMapping() {
	}

	public SynchConfigDataMapping(String id) {
		this.id = id;
	}

	public SynchConfigDataMapping(String id, String synchConfigId,
			String idmFieldName, String srcFieldName) {
		this.id = id;
		this.synchConfigId = synchConfigId;
		this.idmFieldName = idmFieldName;
		this.srcFieldName = srcFieldName;
	}

	public String getSynchConfigId() {
		return this.synchConfigId;
	}

	public void setSynchConfigId(String synchConfigId) {
		this.synchConfigId = synchConfigId;
	}

	public String getIdmFieldName() {
		return this.idmFieldName;
	}

	public void setIdmFieldName(String idmFieldName) {
		this.idmFieldName = idmFieldName;
	}

	public String getSrcFieldName() {
		return this.srcFieldName;
	}

	public void setSrcFieldName(String srcFieldName) {
		this.srcFieldName = srcFieldName;
	}

    @Override
    public String toString() {
        return "SynchConfigDataMapping{" +
                "mappingId='" + id + '\'' +
                ", synchConfigId='" + synchConfigId + '\'' +
                ", idmFieldName='" + idmFieldName + '\'' +
                ", srcFieldName='" + srcFieldName + '\'' +
                '}';
    }
}
