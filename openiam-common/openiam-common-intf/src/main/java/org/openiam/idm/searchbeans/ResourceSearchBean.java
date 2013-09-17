package org.openiam.idm.searchbeans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.res.dto.Resource;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceSearchBean", propOrder = {
        "name",
        "resourceTypeId",
        "rootsOnly",
        "attributes"
})
public class ResourceSearchBean extends AbstractSearchBean<Resource, String> implements SearchBean<Resource, String>, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String resourceTypeId;
	private Boolean rootsOnly;
	private List<Tuple<String, String>> attributes;
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}

	public String getResourceTypeId() {
		return resourceTypeId;
	}

	public void setResourceTypeId(String resourceTypeId) {
		this.resourceTypeId = resourceTypeId;
	}

	public Boolean getRootsOnly() {
		return rootsOnly;
	}

	public void setRootsOnly(Boolean rootsOnly) {
		this.rootsOnly = rootsOnly;
	}
	
	public void addAttribute(final String key, final String value) {
		if(StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
			if(this.attributes == null) {
				this.attributes = new LinkedList<Tuple<String,String>>();
			}
			final Tuple<String, String> tuple = new Tuple<String, String>(key, value);
			this.attributes.add(tuple);
		}
	}

	public List<Tuple<String, String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Tuple<String, String>> attributes) {
		this.attributes = attributes;
	}
}
