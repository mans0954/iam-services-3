package org.openiam.idm.srvc.property.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;

public interface PropertyValueService {

	void save(final List<PropertyValue> entityList) throws BasicDataServiceException;
	List<PropertyValue>  getAll();
	PropertyValueEntity get(final String id);
}
