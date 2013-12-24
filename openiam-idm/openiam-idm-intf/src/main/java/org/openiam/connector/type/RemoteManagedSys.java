package org.openiam.connector.type;


import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteManagedSys", propOrder = {
        "scriptName"
})
public class RemoteManagedSys extends ManagedSysDto {
    private static final long serialVersionUID = 6023119460436970680L;
    @XmlElement
    protected String scriptName;

    public RemoteManagedSys() {
    }

    public RemoteManagedSys(String managedSysId, String connectorId) {
        super(managedSysId, connectorId);
    }

    public RemoteManagedSys(String managedSysId, String name, String description, String status, String connectorId, String hostUrl, Integer port, String commProtocol, String userId, String pswd, Date startDate, Date endDate) {
        super(managedSysId, name, description, status, connectorId, hostUrl, port, commProtocol, userId, pswd, startDate, endDate);
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}
