package org.openiam.idm.srvc.prov.request.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BulkOperationRequest", propOrder = {
        "requesterId",
        "userIds",
        "operations"
})
public class BulkOperationRequest implements Serializable {

    private String requesterId;
    private Set<String> userIds = new HashSet<String>();
    private Set<OperationBean> operations = new LinkedHashSet<OperationBean>();

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }

    public Set<OperationBean> getOperations() {
        return operations;
    }

    public void setOperations(Set<OperationBean> operations) {
        this.operations = operations;
    }
}
