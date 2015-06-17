package org.openiam.idm.srvc.property.service;

import java.util.List;

import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.property.domain.PropertyValueEntity;

public interface PropertyValueService {

	void save(final List<PropertyValueEntity> entityList);
	List<PropertyValueEntity>  getAll();
	PropertyValueEntity get(final String id);
}
