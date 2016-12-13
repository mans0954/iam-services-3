package org.openiam.provision.dto.accessmodel;

import org.openiam.provision.dto.srcadapter.UserSearchKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by zaporozhec on 7/27/16.
 */
@XmlType(propOrder = {"key", "filter", "namedTypes", "treeRepresentation", "ipAdress", "requesterId", "requesterLogin"})
@XmlRootElement(name = "request")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlRequest {
    private UserSearchKey key;
    private UserAccessControlFilter filter;
    private boolean namedTypes;
    private boolean treeRepresentation;
    private String requesterId;
    private String requesterLogin;
    private String ipAdress;

    public String getRequesterLogin() {
        return requesterLogin;
    }

    public void setRequesterLogin(String requesterLogin) {
        this.requesterLogin = requesterLogin;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

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

    public String getIpAdress() {
        return ipAdress;
    }

    public void setIpAdress(String ipAdress) {
        this.ipAdress = ipAdress;
    }
}
