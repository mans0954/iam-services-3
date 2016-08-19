package org.openiam.idm.srvc.user.dto;

import org.openiam.base.ws.SortParam;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

@Entity
public class LightSearchDelegationAttributeModel implements Serializable {
    @Id
    private String id;
    private String value;
    @ElementCollection
    @CollectionTable(name = "USER_ATTRIBUTE_VALUES", joinColumns = @JoinColumn(name = "USER_ATTRIBUTE_ID", referencedColumnName = "ID"))
    @Column(name = "VALUE", length = 255)
    private List<String> values;
    private String isMultivalued;
    private String userId;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getIsMultivalued() {
        return isMultivalued;
    }

    public void setIsMultivalued(String isMultivalued) {
        this.isMultivalued = isMultivalued;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
