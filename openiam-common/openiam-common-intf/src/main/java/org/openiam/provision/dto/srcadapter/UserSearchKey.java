package org.openiam.provision.dto.srcadapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by zaporozhec on 10/29/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserSearchKey implements Serializable {
    @XmlElement(required = true)
    UserSearchKeyEnum name;
    @XmlElement(required = true)
    String value;

    public UserSearchKeyEnum getName() {
        return name;
    }

    public void setName(UserSearchKeyEnum name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
