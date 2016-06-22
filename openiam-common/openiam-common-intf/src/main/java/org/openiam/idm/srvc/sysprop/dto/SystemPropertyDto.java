package org.openiam.idm.srvc.sysprop.dto;

import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.sysprop.domain.SystemPropertyEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * Created by zaporozhec on 6/16/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "systemProperty", propOrder = {
        "name", "value", "mdTypeId", "mdTypeName"
})
public class SystemPropertyDto implements Serializable {
    private String name;
    private String mdTypeId;
    private String mdTypeName;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMdTypeId() {
        return mdTypeId;
    }

    public void setMdTypeId(String mdTypeId) {
        this.mdTypeId = mdTypeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMdTypeName() {
        return mdTypeName;
    }

    public void setMdTypeName(String mdTypeName) {
        this.mdTypeName = mdTypeName;
    }
}
