package org.openiam.idm.searchbeans;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.Tuple;
import org.openiam.idm.srvc.org.dto.Organization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 02.11.12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationSearchBean", propOrder = {
        "keySet",
        "organizationTypeIdSet",
//		"userId",
//		"parentId",
//		"childId",
        "validParentTypeId",
        "internalOrgId",
        "attributes",
        "metadataType",
        "isSelectable",
        "adminResourceId",
        "abbreviation"
})
public class OrganizationSearchBean extends EntitlementsSearchBean<Organization, String> implements SearchBean<Organization, String>,
        Serializable {
    private static final long serialVersionUID = 1L;
    private Set<String> keySet;
    private Set<String> organizationTypeIdSet;
    private String validParentTypeId;
    private String internalOrgId;
    private String metadataType;
    private List<Tuple<String, String>> attributes;
    private Boolean isSelectable = null;
    private String adminResourceId;
    private String abbreviation;

    public String getOrganizationTypeId() {
        return (CollectionUtils.isNotEmpty(organizationTypeIdSet)) ? organizationTypeIdSet.iterator().next() : null;
    }

    public void setOrganizationTypeId(String organizationTypeId) {
        this.addOrganizationTypeId(organizationTypeId);
    }

    public void addOrganizationTypeId(final String organizationTypeId) {
        if (organizationTypeIdSet == null) {
            organizationTypeIdSet = new HashSet<String>();
        }
        organizationTypeIdSet.add(organizationTypeId);
    }

    public Set<String> getOrganizationTypeIdSet() {
        return organizationTypeIdSet;
    }

    public void setOrganizationTypeIdSet(final List<String> organizationTypeIdSet) {
        if (organizationTypeIdSet != null) {
            setKeys(new HashSet<String>(organizationTypeIdSet));
        }
    }

    public void setOrganizationTypeIdSet(final Set<String> organizationTypeIdSet) {
        this.organizationTypeIdSet = organizationTypeIdSet;
    }

    @Override
    public String getKey() {
        return (CollectionUtils.isNotEmpty(keySet)) ? keySet.iterator().next() : null;
    }

    @Override
    public void setKey(final String key) {
        this.addKey(key);
    }

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public Set<String> getKeys() {
        return keySet;
    }

    public void addKey(final String key) {
        if (this.keySet == null) {
            this.keySet = new HashSet<String>();
        }
        this.keySet.add(key);
    }

    public boolean hasMultipleKeys() {
        return (keySet != null && keySet.size() > 1);
    }

    public void setKeys(final List<String> keySet) {
        if (keySet != null) {
            setKeys(new HashSet<String>(keySet));
        }
    }

    public void setKeys(final Set<String> keySet) {
        this.keySet = keySet;
    }

    public Boolean getIsSelectable() {
        return isSelectable;
    }

    public void setIsSelectable(Boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    public String getValidParentTypeId() {
        return validParentTypeId;
    }

    public void setValidParentTypeId(String validParentTypeId) {
        this.validParentTypeId = validParentTypeId;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public void addAttribute(final String key, final String value) {
        if (StringUtils.isNotBlank(key) || StringUtils.isNotBlank(value)) {
            if (this.attributes == null) {
                this.attributes = new LinkedList<Tuple<String, String>>();
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

    public String getAdminResourceId() {
        return adminResourceId;
    }

    public void setAdminResourceId(String adminResourceId) {
        this.adminResourceId = adminResourceId;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        OrganizationSearchBean that = (OrganizationSearchBean) o;

        if (internalOrgId != null ? !internalOrgId.equals(that.internalOrgId) : that.internalOrgId != null)
            return false;
        return !(validParentTypeId != null ? !validParentTypeId.equals(that.validParentTypeId) : that.validParentTypeId != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (validParentTypeId != null ? validParentTypeId.hashCode() : 0);
        result = 31 * result + (internalOrgId != null ? internalOrgId.hashCode() : 0);
        return result;
    }

}
