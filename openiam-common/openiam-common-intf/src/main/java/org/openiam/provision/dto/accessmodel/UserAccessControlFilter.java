package org.openiam.provision.dto.accessmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Set;

/**
 * Created by zaporozhec on 7/28/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccessControlFilter {
    //name of managed system
    private Set<String> managedSystemNames;
    //name of roles metadata types
    private Set<String> roleMetadataTypes;
    //name of group metadatatypes
    private Set<String> groupMetadataTypes;
    //name of resource types
    private Set<String> resourceTypes;


    public Set<String> getManagedSystemNames() {
        return managedSystemNames;
    }

    public void setManagedSystemNames(Set<String> managedSystemNames) {
        this.managedSystemNames = managedSystemNames;
    }

    public Set<String> getRoleMetadataTypes() {
        return roleMetadataTypes;
    }

    public void setRoleMetadataTypes(Set<String> roleMetadataTypes) {
        this.roleMetadataTypes = roleMetadataTypes;
    }

    public Set<String> getGroupMetadataTypes() {
        return groupMetadataTypes;
    }

    public void setGroupMetadataTypes(Set<String> groupMetadataTypes) {
        this.groupMetadataTypes = groupMetadataTypes;
    }

    public Set<String> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(Set<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
