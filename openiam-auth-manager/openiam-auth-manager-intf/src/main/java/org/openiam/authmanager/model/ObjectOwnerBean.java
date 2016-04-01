package org.openiam.authmanager.model;

import org.openiam.authmanager.ws.request.AuthorizationMatrixMapAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;
import java.util.Set;

/**
 * Created by anton on 09.10.15.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectOwnerBean", propOrder = {
        "objectOwnerMap"
})

public class ObjectOwnerBean  {
    @XmlJavaTypeAdapter(AuthorizationMatrixMapAdapter.class)
    private Map<String, Set<String>> objectOwnerMap;

    public Map<String, Set<String>> getObjectOwnerMap() {
        return objectOwnerMap;
    }

    public void setObjectOwnerMap(Map<String, Set<String>> objectOwnerMap) {
        this.objectOwnerMap = objectOwnerMap;
    }
}
