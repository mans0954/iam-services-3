package org.openiam.provision.resp;

import org.openiam.base.ws.Response;
import org.openiam.provision.type.ManagedSystemViewerBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSystemViewerResponse", propOrder = {
        "viewerList",
        "userId",
        "managedSysId"
})
public class ManagedSystemViewerResponse extends Response {
    List<ManagedSystemViewerBean> viewerList;
    String userId;
    String managedSysId;

    public List<ManagedSystemViewerBean> getViewerList() {
        return viewerList;
    }

    public void setViewerList(List<ManagedSystemViewerBean> viewerList) {
        this.viewerList = viewerList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getManagedSysId() {
        return managedSysId;
    }

    public void setManagedSysId(String managedSysId) {
        this.managedSysId = managedSysId;
    }
}
