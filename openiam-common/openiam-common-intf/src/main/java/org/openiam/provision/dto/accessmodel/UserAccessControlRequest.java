package org.openiam.provision.dto.accessmodel;

import org.openiam.provision.dto.srcadapter.UserSearchKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/27/16.
 */
@XmlType(propOrder = {"key", "filter", "namedTypes", "treeRepresentation"})
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlRequest {
    private UserSearchKey key;
    private UserAccessControlFilter filter;
    private boolean namedTypes;
    private boolean treeRepresentation;

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

    public boolean getNamedTypes() {
        return namedTypes;
    }

    public void setNamedTypes(boolean namedTypes) {
        this.namedTypes = namedTypes;
    }

    public boolean getTreeRepresentation() {
        return treeRepresentation;
    }

    public void setTreeRepresentation(boolean treeRepresentation) {
        this.treeRepresentation = treeRepresentation;
    }
}
