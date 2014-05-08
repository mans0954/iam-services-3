package org.openiam.provision.resp;

import org.openiam.base.ws.Response;
import org.openiam.provision.type.ManagedSystemViewerBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManagedSystemViewerResponse", propOrder = {
        "viewerBeanList",
        "userId",
        "managedSysId",
        "exist"
})
public class ManagedSystemViewerResponse extends Response {
    private List<ManagedSystemViewerBean> viewerBeanList;
    private String userId;
    private String managedSysId;
    private boolean exist;

    public List<ManagedSystemViewerBean> getViewerBeanList() {
        return viewerBeanList;
    }

    public void setViewerBeanList(List<ManagedSystemViewerBean> viewerBeanList) {
        this.viewerBeanList = viewerBeanList;
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

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}
