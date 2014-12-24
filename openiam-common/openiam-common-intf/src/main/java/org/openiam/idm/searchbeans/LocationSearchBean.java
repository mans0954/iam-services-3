package org.openiam.idm.searchbeans;

import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.loc.dto.Location;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationSearchBean", propOrder = {
        "organizationId",
        "country"
})
public class LocationSearchBean extends AbstractSearchBean<Location, String> implements SearchBean<Location, String>,
        Serializable {
    private String organizationId;
    private String country;


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }


}
