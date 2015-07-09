package org.openiam.xacml.srvc.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.openiam.base.domain.KeyEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zaporozhec on 7/8/15.
 */
@Entity
@Table(name = "XACML_ATTRIBUTE_DESIGNATOR")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@DozerDTOCorrespondence(Organization.class)
@AttributeOverride(name = "id", column = @Column(name = "ATTRIB_DESIGNATOR_ID"))
public class XACMLAttributeDesignatorEntity extends KeyEntity {

    @Column(name = "FULFILL_ON", length = 255)
    private String category;
    @Column(name = "ATTRIBUTE_ID", length = 255)
    private String attributeId;
    @Column(name = "DATA_TYPE", length = 255)
    private String dataType;

    @Column(name = "ISSUER", length = 255)
    private String issuer;

    @Column(name = "MUST_BE_PRESENT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean mustBePresent;

    @Column(name = "IS_SELECTOR")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isSelector;


    @Column(name = "SELECTOR_PATH", length = 255)
    private String selectorPath;

    @Column(name = "SELECTOR_IDENTIFIER", length = 255)
    private String selectorIdentifier;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "attributeDesignatorEntity", fetch = FetchType.LAZY)
    private Set<XACMLObligationEntity> obligationEntities = new HashSet<XACMLObligationEntity>(0);

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "attributeDesignatorEntity", fetch = FetchType.LAZY)
    private Set<XACMLMatchAttributesEntity> matchAttributeEntities = new HashSet<XACMLMatchAttributesEntity>(0);

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Boolean getMustBePresent() {
        return mustBePresent;
    }

    public void setMustBePresent(Boolean mustBePresent) {
        this.mustBePresent = mustBePresent;
    }

    public Boolean getIsSelector() {
        return isSelector;
    }

    public void setIsSelector(Boolean isSelector) {
        this.isSelector = isSelector;
    }

    public String getSelectorPath() {
        return selectorPath;
    }

    public void setSelectorPath(String selectorPath) {
        this.selectorPath = selectorPath;
    }

    public String getSelectorIdentifier() {
        return selectorIdentifier;
    }

    public void setSelectorIdentifier(String selectorIdentifier) {
        this.selectorIdentifier = selectorIdentifier;
    }

    public Set<XACMLObligationEntity> getObligationEntities() {
        return obligationEntities;
    }

    public void setObligationEntities(Set<XACMLObligationEntity> obligationEntities) {
        this.obligationEntities = obligationEntities;
    }

    public Set<XACMLMatchAttributesEntity> getMatchAttributeEntities() {
        return matchAttributeEntities;
    }

    public void setMatchAttributeEntities(Set<XACMLMatchAttributesEntity> matchAttributeEntities) {
        this.matchAttributeEntities = matchAttributeEntities;
    }

}
