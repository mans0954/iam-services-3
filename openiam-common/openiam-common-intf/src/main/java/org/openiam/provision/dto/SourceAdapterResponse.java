package org.openiam.provision.dto;

import org.openiam.base.KeyNameDTO;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"status", "error"})
@XmlRootElement(name = "response")
public class SourceAdapterResponse {
    private ResponseStatus status;
    private String error;

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
