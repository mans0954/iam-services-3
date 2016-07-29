package org.openiam.provision.dto.accessmodel;

import org.openiam.provision.dto.common.UserSearchKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/27/16.
 */
@XmlType(propOrder = {"key", "filter"})
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlRequest {
    private UserSearchKey key;
    private UserAccessControlFilter filter;

    public UserSearchKey getKey() {
        return key;
    }

    public void setKey(UserSearchKey key) {
        this.key = key;
    }

    public UserAccessControlFilter getFilter() {
        return filter;
    }

    public void setFilter(UserAccessControlFilter filter) {
        this.filter = filter;
    }
}
