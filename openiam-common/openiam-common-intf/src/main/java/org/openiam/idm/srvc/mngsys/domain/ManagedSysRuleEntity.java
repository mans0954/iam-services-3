package org.openiam.idm.srvc.mngsys.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysRuleDto;

@Entity
@Table(name = "MANAGED_SYS_RULE")
@DozerDTOCorrespondence(ManagedSysRuleDto.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ManagedSysRuleEntity implements Serializable {
    private static final long serialVersionUID = -648884785253890053L;
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "MANAGED_SYS_RULE_ID", length = 32, nullable = false)
    private String managedSysRuleId;
    @Column(name = "MANAGED_SYS_ID", length = 32, nullable = false)
    private String managedSysId;
    @Column(name = "MANAGED_SYS_RULE_NAME", length = 45, nullable = false)
    private String name;
    @Column(name = "MANAGED_SYS_RULE_VALUE", length = 45, nullable = false)
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
