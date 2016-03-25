package org.openiam.idm.srvc.meta.service;

import org.openiam.idm.srvc.meta.dto.PageElementValidValue;

import java.util.List;

/**
 * Created by alexander on 21/12/15.
 */
public interface FieldDataModel {
    public String getDefaultValue(String requesterId);
    public List<PageElementValidValue> getDataModel(String requesterId);
}
