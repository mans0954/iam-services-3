package org.openiam.provision.dto.srcadapter;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"newName", "addIfNotExistsInOpenIAM", "metadataTypeId", "organizationTypeId", "abbreviation", "alias", "classification", "description",
        "domainName", "internalOrgId", "ldapString",
        "symbol", "status", "entityAttributes"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterOrganizationRequest extends SourceAdapterEntityRequest {

    private String metadataTypeId;
    private String organizationTypeId;
    private String newName;
    private String alias;
    @XmlElementWrapper(name = "entity-attributes-set")
    @XmlElements({@XmlElement(name = "entity-attribute")})
    private Set<SourceAdapterAttributeRequest> entityAttributes;
    private String description;
    private String domainName;
    private String ldapString;
    private String internalOrgId;
    private String abbreviation;
    private String classification;
    private String symbol;
    private String status;
    private boolean addIfNotExistsInOpenIAM = false;

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(String organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Set<SourceAdapterAttributeRequest> getEntityAttributes() {
        return entityAttributes;
    }

    public void setEntityAttributes(Set<SourceAdapterAttributeRequest> entityAttributes) {
        this.entityAttributes = entityAttributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getLdapString() {
        return ldapString;
    }

    public void setLdapString(String ldapString) {
        this.ldapString = ldapString;
    }

    public String getInternalOrgId() {
        return internalOrgId;
    }

    public void setInternalOrgId(String internalOrgId) {
        this.internalOrgId = internalOrgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    public boolean isAddIfNotExistsInOpenIAM() {
        return addIfNotExistsInOpenIAM;
    }

    public void setAddIfNotExistsInOpenIAM(boolean addIfNotExistsInOpenIAM) {
        this.addIfNotExistsInOpenIAM = addIfNotExistsInOpenIAM;
    }
}
