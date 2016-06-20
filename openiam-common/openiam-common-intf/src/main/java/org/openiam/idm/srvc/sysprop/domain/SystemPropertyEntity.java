package org.openiam.idm.srvc.sysprop.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.sysprop.dto.SystemPropertyDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaporozhec on 6/16/16.
 */
@Entity
@Table(name = "SYSTEM_PROPERTY")
public class SystemPropertyEntity implements Serializable {

    @Id
    @Column(name = "NAME", length = 40)
    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE", referencedColumnName = "TYPE_ID", insertable = false, updatable = false)
    private MetadataTypeEntity type;

    @Column(name = "VALUE", length = 255)
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetadataTypeEntity getType() {
        return type;
    }

    public void setType(MetadataTypeEntity type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SystemPropertyDto toDTO() {
        SystemPropertyDto systemPropertyDto = new SystemPropertyDto();
        systemPropertyDto.setName(this.getName());
        systemPropertyDto.setValue(this.getValue());
        MetadataTypeEntity mdtype = this.getType();
        if (mdtype != null) {
            systemPropertyDto.setMdTypeId(mdtype.getId());
            systemPropertyDto.setMdTypeName(mdtype.getDescription());
        }
        return systemPropertyDto;
    }

    public static List<SystemPropertyDto> toDtoList(List<SystemPropertyEntity> source) {
        if (source == null) {
            return null;
        }
        List<SystemPropertyDto> target = new ArrayList<>();
        for (SystemPropertyEntity entity : source) {
            target.add(entity.toDTO());
        }
        return target;
    }
}
