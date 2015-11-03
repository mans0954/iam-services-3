package org.openiam.provision.dto.srcadapter;

import javax.xml.bind.annotation.*;

/**
 * Created by zaporozhec on 10/29/15.
 */

@XmlType(propOrder = {"metadataTypeId", "organizationTypeId"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceAdapterOrganizationRequest extends SourceAdapterEntityRequest {
    private String metadataTypeId;
    private String organizationTypeId;

    public String getMetadataTypeId() {
        return metadataTypeId;
    }

    public void setMetadataTypeId(String metadataTypeId) {
        this.metadataTypeId = metadataTypeId;
    }

    public String getOrganizationTypeId() {
        return organizationTypeId;
    }

    public void setOrganizationTypeId(String organizationTypeId) {
        this.organizationTypeId = organizationTypeId;
    }
}
