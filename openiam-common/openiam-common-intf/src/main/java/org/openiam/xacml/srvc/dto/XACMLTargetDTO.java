package org.openiam.xacml.srvc.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.openiam.base.KeyDTO;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.xacml.srvc.domain.XACMLTargetEntity;

/**
 * Created by zaporozhec on 7/10/15.
 */
@JsonRootName(value = "Target")
@DozerDTOCorrespondence(XACMLTargetEntity.class)
public class XACMLTargetDTO extends KeyDTO {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
