/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.provision.type;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.*;


/**
 * Base object whose descendants will be used in Add, Update and Delete requests
 * @author suneet
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensibleObject", propOrder = {
    "objectId",
    "name",
    "operation",
    "attributes",
    "principalFieldName",
    "principalFieldDataType",
    "extensibleObjectType"
})
@XmlSeeAlso({
    ArrayList.class
})
public class ExtensibleObject implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6512595735853659295L;
	protected String objectId;
	protected String name;
	protected int operation;
            // holds the name of the field or column to which the principalName will be assigned
    protected String principalFieldName;
    protected String principalFieldDataType;

    protected ExtensibleObjectType extensibleObjectType;
	
	protected List<ExtensibleAttribute> attributes = new ArrayList<ExtensibleAttribute>();

    protected static final String[] PROTECTED_PROPERTIES = {"password", "accountpassword", "unicodepwd", "userpassword"};

	public ExtensibleObject() {
		operation = 0;
	}

    public ExtensibleObject(ExtensibleObjectType extensibleObjectType) {
        this.extensibleObjectType = extensibleObjectType;
    }

    public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public List<ExtensibleAttribute> getAttributes() {
		return attributes;
	}

    public String getAttributesAsJSON() throws IOException {
        Properties attrVals = new Properties();
        ObjectMapper mapper = new ObjectMapper();
        for(ExtensibleAttribute attribute : this.getAttributes()) {
            Object val = null;
            if (ArrayUtils.contains(PROTECTED_PROPERTIES, attribute.getName().toLowerCase())) { //TODO: Consider using of 'HIDDEN' mark from lookupAttributes instead of hardcoded array
                val = "PROTECTED";
            } else if (attribute.getValue() != null) {
                val = attribute.getValue();
            } else if (attribute.getAttributeContainer() != null) {
                val = attribute.getAttributeContainer();
            } else if (CollectionUtils.isNotEmpty(attribute.getValueList())) {
                val = attribute.getValueList();
            }  else if (attribute.getValueAsByteArray() != null) {
                val = "BINARY DATA";
            }
            attrVals.put(attribute.getName(), val);
        }
        return mapper.writeValueAsString(attrVals);
    }

	public void setAttributes(List<ExtensibleAttribute> attributes) {
		this.attributes = attributes;
	}

    public String getPrincipalFieldName() {
        return principalFieldName;
    }

    public void setPrincipalFieldName(String principalFieldName) {
        this.principalFieldName = principalFieldName;
    }

    public String getPrincipalFieldDataType() {
        return principalFieldDataType;
    }

    public void setPrincipalFieldDataType(String principalFieldDataType) {
        this.principalFieldDataType = principalFieldDataType;
    }

    public ExtensibleObjectType getExtensibleObjectType() {
        return extensibleObjectType;
    }

    @Override
    public String toString() {
        return "ExtensibleObject{" +
                "objectId='" + objectId + '\'' +
                ", name='" + name + '\'' +
                ", operation=" + operation +
                ", principalFieldName='" + principalFieldName + '\'' +
                ", principalFieldDataType='" + principalFieldDataType + '\'' +
                ", attributes=" + attributes + '\'' +
                ", extensibleObjectType=" + extensibleObjectType +
                '}';
    }


}
