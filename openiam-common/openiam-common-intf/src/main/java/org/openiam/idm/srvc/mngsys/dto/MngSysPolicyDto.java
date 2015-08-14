package org.openiam.idm.srvc.mngsys.dto;

import org.openiam.base.AbstractMetadataTypeDTO;
import org.openiam.base.BaseObject;
import org.openiam.base.KeyDTO;
import org.openiam.base.KeyNameDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MngSysPolicyDto", propOrder = {
        "name",
        "createDate",
        "lastUpdate",
        "primary",
        "managedSysId",
        "attributeMaps"
})
@DozerDTOCorrespondence(ManagedSysEntity.class)
@XmlSeeAlso({KeyDTO.class, KeyNameDTO.class, BaseObject.class, BaseObject.class, AttributeMap.class})
public class MngSysPolicyDto extends AbstractMetadataTypeDTO {

    protected String name;

    @XmlSchemaType(name = "dateTime")
    private Date lastUpdate;

    @XmlSchemaType(name = "dateTime")
    private Date createDate;

    private boolean primary = false;

    private String managedSysId;

    private Set<AttributeMap> attributeMaps = new HashSet<AttributeMap>(0);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public Set<AttributeMap> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(Set<AttributeMap> attributeMaps) {
        this.attributeMaps = attributeMaps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MngSysPolicyDto)) return false;
        if (!super.equals(o)) return false;

        MngSysPolicyDto that = (MngSysPolicyDto) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (mdTypeId == null) {
            if (((MngSysPolicyDto) o).getMdTypeId() != null)
                return false;
        } else if (!mdTypeId.equals(((MngSysPolicyDto) o).getMdTypeId()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result
                + ((mdTypeId == null) ? 0 : mdTypeId.hashCode());
        return result;
    }
}