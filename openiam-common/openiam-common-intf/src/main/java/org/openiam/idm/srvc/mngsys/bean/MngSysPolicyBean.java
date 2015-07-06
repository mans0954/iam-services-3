package org.openiam.idm.srvc.mngsys.bean;


import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.MngSysPolicyDto;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *  A flyweight version of MngSysPolicyDto
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso(AttributeMapBean.class)
@XmlType(name = "MngSysPolicyBean", propOrder = {
        "id",
        "name",
        "managedSysId",
        "mdTypeId",
        "primary",
        "attrMaps"
})
public class MngSysPolicyBean implements Serializable {

    private String id;
    private String name;
    private String managedSysId;
    @XmlTransient
    private String managedSysName;
    private String mdTypeId;
    private boolean primary;
    private List<AttributeMapBean> attrMaps = new ArrayList<>();

    public MngSysPolicyBean() {}

    public MngSysPolicyBean(MngSysPolicyDto mngSysPolicy) {
        this.id = mngSysPolicy.getId();
        this.name = mngSysPolicy.getName();
        this.managedSysId = mngSysPolicy.getManagedSysId();
        this.mdTypeId = mngSysPolicy.getMdTypeId();
        this.primary = mngSysPolicy.isPrimary();
        if (CollectionUtils.isNotEmpty(mngSysPolicy.getAttributeMaps())) {
            for (AttributeMap am: mngSysPolicy.getAttributeMaps()) {
                attrMaps.add(new AttributeMapBean(am));
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getManagedSysName() {
        return managedSysName;
    }

    public void setManagedSysName(String managedSysName) {
        this.managedSysName = managedSysName;
    }

    public String getMdTypeId() {
        return mdTypeId;
    }

    public void setMdTypeId(String mdTypeId) {
        this.mdTypeId = mdTypeId;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public List<AttributeMapBean> getAttrMaps() {
        return attrMaps;
    }

    public void setAttrMaps(List<AttributeMapBean> attrMaps) {
        this.attrMaps = attrMaps;
    }
}
