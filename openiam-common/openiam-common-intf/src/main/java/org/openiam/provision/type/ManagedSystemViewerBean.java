package org.openiam.provision.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSystemViewerBean", propOrder = {
        "attributeName",
        "idmAttribute",
        "mngSysAttribute",
        "readOnly"
})
public class ManagedSystemViewerBean implements Serializable {

    private String attributeName;
    private ExtensibleAttribute idmAttribute;
    private ExtensibleAttribute mngSysAttribute;
    private boolean readOnly;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public ExtensibleAttribute getIdmAttribute() {
        return idmAttribute;
    }

    public void setIdmAttribute(ExtensibleAttribute idmAttribute) {
        this.idmAttribute = idmAttribute;
    }

    public ExtensibleAttribute getMngSysAttribute() {
        return mngSysAttribute;
    }

    public void setMngSysAttribute(ExtensibleAttribute mngSysAttribute) {
        this.mngSysAttribute = mngSysAttribute;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
