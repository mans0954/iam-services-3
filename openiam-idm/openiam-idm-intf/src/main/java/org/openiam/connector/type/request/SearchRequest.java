package org.openiam.connector.type.request;

import org.openiam.connector.type.constant.ReturnData;
import org.openiam.provision.type.ExtensibleObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchRequest", propOrder = {
        "searchValue",
        "searchQuery",
        "returnData"
})
public class SearchRequest<ExtObject extends ExtensibleObject> extends RequestType<ExtObject>{
    @XmlElement(required = true)
    protected String searchValue;                          // the value that we are searching for

    protected String searchQuery;                          // query to search for - if its ldap or AD then this is a search filter

    protected ReturnData returnData;                       // attributes that should be returned.


    /**
     * Gets the value of the returnData property.
     *
     * @return
     *     possible object is
     *     {@link org.openiam.connector.type.constant.ReturnData }
     *
     */
    public ReturnData getReturnData() {
        if (returnData == null) {
            return ReturnData.EVERYTHING;
        } else {
            return returnData;
        }
    }

    /**
     * Sets the value of the returnData property.
     *
     * @param value
     *     allowed object is
     *     {@link org.openiam.connector.type.constant.ReturnData }
     *
     */
    public void setReturnData(ReturnData value) {
        this.returnData = value;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
}
