package org.openiam.am.srvc.searchbeans;

import org.openiam.idm.searchbeans.EntitlementsSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.role.dto.Role;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OauthScopeSearchBean", propOrder = {
        "name"

})
public class OauthScopeSearchBean  extends EntitlementsSearchBean<Role, String> implements SearchBean<Role, String>, Serializable {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
