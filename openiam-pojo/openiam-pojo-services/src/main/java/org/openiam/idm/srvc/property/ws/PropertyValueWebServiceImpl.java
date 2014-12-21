package org.openiam.idm.srvc.property.ws;

import java.util.Iterator;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.property.converter.PropertyValueConverter;
import org.openiam.idm.srvc.property.service.PropertyValueService;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.property.domain.PropertyType;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("propertyValueWS")
@WebService(
	endpointInterface = "org.openiam.idm.srvc.property.ws.PropertyValueWebService", 
	targetNamespace = "urn:idm.openiam.org/srvc/property/service", 
	portName = "PropertyValueWebServicePort", 
	serviceName = "PropertyValueWebService"
)
public class PropertyValueWebServiceImpl extends AbstractBaseService implements PropertyValueWebService {
	
	 private static final Log log = LogFactory.getLog(PropertyValueWebServiceImpl.class);
	 
	 @Autowired
	 private PropertyValueConverter converter;
	 
	 @Autowired
	 private LanguageDAO languageDAO;
	 
	 private BasicDataServiceException getException(final PropertyValue dto, final String value) {
		 BasicDataServiceException e = null;
		 if(StringUtils.isBlank(value)) {
			 if(!dto.isEmptyValueAllowed()) {
				 e =  new BasicDataServiceException(ResponseCode.PROPERTY_VALUE_REQUIRED);
			 }
		 } else {
			 switch (dto.getType()) {
			  	case BOOLEAN:
			  		if(!StringUtils.equalsIgnoreCase(value, "true") && !StringUtils.equalsIgnoreCase(value, "false")) {
			  			e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
			  		}
			  		break;
			  	case DOUBLE:
			  		try {
			  			Double.parseDouble(value);
			  		} catch(NumberFormatException ex) {
			  			e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
			  		}
			  		break;
			  	case INTEGER:
			  		try {
			  			Integer.parseInt(value);
			  		} catch(NumberFormatException ex) {
			  			e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
			  		}
			  		break;
			  	case LONG:
			  		try {
			  			Long.parseLong(value);
			  		} catch(NumberFormatException ex) {
			  			e =  new BasicDataServiceException(ResponseCode.PROPERTY_TYPE_INVALID);
			  		}
			  		break;
			  	case STRING:
			  		break;
			  	default:
			  		break;
			 }
		 }
		 return e;
	 }

	 @Override
	 public Response save(List<PropertyValue> dtoList, final String requestorId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		IdmAuditLog idmAuditLog = new IdmAuditLog();
        idmAuditLog.setRequestorUserId(requestorId);
        idmAuditLog.setAction(AuditAction.MODIFY_PROPERTIES.value());
        try {
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
        			final PropertyValueEntity entity = propertyValueService.get(dto.getId());
        			dto.setEmptyValueAllowed(entity.isEmptyValueAllowed());
        			dto.setMultilangual(entity.isMultilangual());
        			dto.setReadOnly(entity.isReadOnly());
        			dto.setType(entity.getType());
        			if(dto.isReadOnly()) {
        				dto.setValue(entity.getValue());
        				dto.setInternationalizedValues(converter.convertToDTO(entity, true).getInternationalizedValues());
        			} else {
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
        	propertyValueService.save(entityList);
        	idmAuditLog.succeed();
        } catch (BasicDataServiceException e) {
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            idmAuditLog.fail();
            idmAuditLog.setFailureReason(e.getCode());
            idmAuditLog.setException(e);
        } catch (Throwable e) {
            log.error("Can't save or update resource property", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
            idmAuditLog.fail();
            idmAuditLog.setException(e);
        } finally {
            auditLogService.enqueue(idmAuditLog);
        }
        return response;
	 }

	@Override
	public List<PropertyValue> getAll() {
		final List<PropertyValueEntity> entityList = propertyValueService.getAll();
		final List<PropertyValue> dtoList = converter.convertToDTOList(entityList, true);
		return dtoList;
	}

}
