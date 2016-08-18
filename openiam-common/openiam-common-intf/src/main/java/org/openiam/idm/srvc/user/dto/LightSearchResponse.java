package org.openiam.idm.srvc.user.dto;

import org.openiam.base.ws.ResponseStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.List;

/**
 * Created by zaporozhec on 8/16/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lightUserSearchResponse", propOrder = {
        "status",
        "lightUserSearchModels",
        "count"
})
@XmlRootElement(name = "light-search-response", namespace = "")
public class LightSearchResponse implements Serializable {
    ResponseStatus status;
    List<LightUserSearchModel> lightUserSearchModels;
    int count;

    public List<LightUserSearchModel> getLightUserSearchModels() {
        return lightUserSearchModels;
    }

    public void setLightUserSearchModels(List<LightUserSearchModel> lightUserSearchModels) {
        this.lightUserSearchModels = lightUserSearchModels;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
