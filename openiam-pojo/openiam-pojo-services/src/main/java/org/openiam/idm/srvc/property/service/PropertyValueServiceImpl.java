package org.openiam.idm.srvc.property.service;

import java.util.List;

import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.property.dao.PropertyValueDAO;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.property.domain.PropertyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {
	
	
	@Autowired
	private PropertyValueDAO propertyDAO;

	@Override
	@Transactional
	public void save(List<PropertyValueEntity> entityList) {
		if(entityList != null) {
			entityList.forEach(e -> {
				if(e.getId() != null) {
					final PropertyValueEntity entity = propertyDAO.findById(e.getId());
					if(entity != null && !entity.isReadOnly()) {
						entity.setValue(e.getValue());
						entity.setInternationalizedValues(e.getInternationalizedValues());
						propertyDAO.update(entity);
					}
				}
			});
		}
		propertyDAO.flush();
		propertyDAO.evictCache();
	}

	@Override
	@Transactional(readOnly=true)
	public List<PropertyValueEntity>  getAll() {
		final List<PropertyValueEntity> entityList = propertyDAO.findAll();
		return entityList;
		
	}

	@Override
	@Transactional(readOnly=true)
	public PropertyValueEntity get(String id) {
		return propertyDAO.findById(id);
	}

}
