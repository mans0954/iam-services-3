package org.openiam.provision.dto.accessmodel;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlFilter {
    //name of managed system
    @XmlElementWrapper(name = "managed-systems")
    @XmlElements({
            @XmlElement(name = "value")}
    )
    private List<String> managedSystemNames;
    //name of roles metadata types
    @XmlElementWrapper(name = "role-metadatas")
    @XmlElements({
            @XmlElement(name = "value")}
    )
    private List<String> roleMetadataTypes;
    //name of group metadatatypes
    @XmlElementWrapper(name = "group-metadatas")
    @XmlElements({
            @XmlElement(name = "value")}
    )
    private List<String> groupMetadataTypes;
    //name of group metadatatypes
    @XmlElementWrapper(name = "resource-metadatas")
    @XmlElements({
            @XmlElement(name = "value")}
    )
    private List<String> resourceMetadataTypes;
    //name of resource types
    @XmlElementWrapper(name = "resource-types")
    @XmlElements({
            @XmlElement(name = "value")}
    )
    private List<String> resourceTypes;


    public List<String> getManagedSystemNames() {
        return managedSystemNames;
    }

    public void setManagedSystemNames(List<String> managedSystemNames) {
        this.managedSystemNames = managedSystemNames;
    }

    public List<String> getRoleMetadataTypes() {
        return roleMetadataTypes;
    }

    public void setRoleMetadataTypes(List<String> roleMetadataTypes) {
        this.roleMetadataTypes = roleMetadataTypes;
    }

    public List<String> getGroupMetadataTypes() {
        return groupMetadataTypes;
    }

    public void setGroupMetadataTypes(List<String> groupMetadataTypes) {
        this.groupMetadataTypes = groupMetadataTypes;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getResourceMetadataTypes() {
        return resourceMetadataTypes;
    }

    public void setResourceMetadataTypes(List<String> resourceMetadataTypes) {
        this.resourceMetadataTypes = resourceMetadataTypes;
    }
}
