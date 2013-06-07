package org.openiam.idm.srvc.mngsys.dto;

// Generated Nov 3, 2008 12:14:43 AM by Hibernate Tools 3.2.2.GA

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysRuleEntity;

/**
 * Domain object representing a managed resource. Managed systems include items
 * such as AD, LDAP, etc which are managed by the IDM system. Managed Resource
 * can also be forms
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSysRuldDto", propOrder = { "managedSysRuleId",
        "managedSysId", "name", "value" })
@DozerDTOCorrespondence(ManagedSysRuleEntity.class)
public class ManagedSysRuleDto implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -648884785253890053L;
    private String managedSysRuleId;
    private String managedSysId;
    private String name;
    private String value;

    public String getManagedSysRuleId() {
        return managedSysRuleId;
    }

    public void setManagedSysRuleId(String managedSysRuleId) {
        this.managedSysRuleId = managedSysRuleId;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
