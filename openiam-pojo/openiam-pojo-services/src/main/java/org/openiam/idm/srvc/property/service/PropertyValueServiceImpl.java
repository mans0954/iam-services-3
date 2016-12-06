package org.openiam.idm.srvc.property.service;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.property.converter.PropertyValueConverter;
import org.openiam.idm.srvc.property.dao.PropertyValueDAO;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {
	@Autowired
	private PropertyValueDAO propertyDAO;
	@Autowired
	private PropertyValueConverter converter;
	@Autowired
	private LanguageDAO languageDAO;

	@Override
	@Transactional
	public void save(List<PropertyValue> dtoList) throws BasicDataServiceException {
		if(CollectionUtils.isEmpty(dtoList)) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		//validate
		final Iterator<PropertyValue> it = dtoList.iterator();
		while(it.hasNext()) {
			final PropertyValue dto = it.next();
			if(StringUtils.isBlank(dto.getId())) {
				it.remove();
			} else {
//				response.addFieldMapping("dto", dto.getId());

				if(!dto.isReadOnly()) {
					if(dto.isMultilangual()) {
						dto.setValue(null);
						if(MapUtils.isEmpty(dto.getInternationalizedValues())) {
							throw new BasicDataServiceException(ResponseCode.PROPERTY_I18_VALUE_MISSING);
						} else {
							for(final LanguageMapping mapping : dto.getInternationalizedValues().values()) {
								final BasicDataServiceException e = getException(dto, mapping.getValue());
								if(e != null) {
									throw e;
								}
							}

							for(final LanguageEntity language : languageDAO.getUsedLanguages()) {
								if(!dto.getInternationalizedValues().containsKey(language.getId())) {
									throw new BasicDataServiceException(ResponseCode.PROPERTY_I18_VALUE_MISSING);
								}
							}
						}
					} else {
						dto.setInternationalizedValues(null);
						final BasicDataServiceException e = getException(dto, dto.getValue());
						if(e != null) {
							throw e;
						}
					}
				}
			}
		}
		final List<PropertyValueEntity> entityList = converter.convertToEntityList(dtoList, true);

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
	public List<PropertyValue>  getAll() {
		final List<PropertyValueEntity> entityList = propertyDAO.findAll();
		final List<PropertyValue> dtoList = converter.convertToDTOList(entityList, true);
		return dtoList;
	}

	@Override
	@Transactional(readOnly=true)
	public PropertyValueEntity get(String id) {
		return propertyDAO.findById(id);
	}


	private BasicDataServiceException getException(final PropertyValue dto, final String value) {
		BasicDataServiceException e = null;
		if(StringUtils.isBlank(value)) {
			if(!dto.isEmptyValueAllowed()) {
				e =  new BasicDataServiceException(ResponseCode.PROPERTY_VALUE_REQUIRED);
			}
		} else {
			switch (dto.getType()) {
				case RegularExpression:
					try {
						Pattern.compile(value);
					} catch(Throwable ex) {
						e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
					}
					break;
				case Boolean:
					if(!StringUtils.equalsIgnoreCase(value, "true") && !StringUtils.equalsIgnoreCase(value, "false")) {
						e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
					}
					break;
				case Double:
					try {
						Double.parseDouble(value);
					} catch(NumberFormatException ex) {
						e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
					}
					break;
				case Integer:
					try {
						Integer.parseInt(value);
					} catch(NumberFormatException ex) {
						e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
					}
					break;
				case Long:
					try {
						Long.parseLong(value);
					} catch(NumberFormatException ex) {
						e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
					}
					break;
				case String:
					break;
				default:
					break;
			}
		}
		return e;
	}
}
