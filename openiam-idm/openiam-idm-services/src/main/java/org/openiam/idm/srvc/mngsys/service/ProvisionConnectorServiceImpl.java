package org.openiam.idm.srvc.mngsys.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.constants.ProvisionConnectorConstant;
import org.openiam.dozer.converter.MetaDataTypeDozerConverter;
import org.openiam.dozer.converter.ProvisionConnectorConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageDataService;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.openiam.idm.srvc.meta.dto.MetadataElement;
import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisionConnectorServiceImpl implements ProvisionConnectorService {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ProvisionConnectorDao provisionConnectorDao;

	@Autowired
	private ProvisionConnectorConverter provisionConnectorConverter;

	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private MetaDataTypeDozerConverter metaDataTypeDozerConverter;
	
	@Autowired
	private LanguageDataService languageDataService;

	@Override
	@Transactional(readOnly = true)
	public List<ProvisionConnectorDto> getProvisionConnectorsByExample(ProvisionConnectorSearchBean searchBean, int from, int size) {
		final List<ProvisionConnectorEntity> entities = provisionConnectorDao.getByExample(searchBean, from, size);
		return provisionConnectorConverter.convertToDTOList(entities, (searchBean != null) ? searchBean.isDeepCopy() : false);
	}

	@Override
	@Transactional(readOnly = true)
	public int getProvisionConnectorsCountByExample(ProvisionConnectorSearchBean searchBean) {
		return provisionConnectorDao.count(searchBean);
	}



	@Override
	@Transactional(readOnly = true)
	public List<MetadataTypeEntity> getProvisionConnectorsMetadataTypes() {
		return provisionConnectorDao.getMetadataTypes();
	}

	@Override
	@Transactional
	public void save(final ProvisionConnectorEntity entity) {
		createMetadataForConnectorType(entity);
		if(StringUtils.isBlank(entity.getId())) {
			provisionConnectorDao.save(entity);
		} else {
			provisionConnectorDao.merge(entity);
		}
	}

	@Override
	@Transactional
	public void delete(String id) {
		ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(id);
		provisionConnectorDao.delete(connectorEntity);
	}

	@Override
	@Transactional
	public ProvisionConnectorDto getDto(String id) {
		ProvisionConnectorEntity connectorEntity = provisionConnectorDao.findById(id);
		return provisionConnectorConverter.convertToDTO(connectorEntity, true);
	}
	private void createMetadataForConnectorType(ProvisionConnectorEntity entity){
		MetadataType metadataType = null;
		List<Language> languages = languageDataService.getUsedLanguages();
		if(entity.getMetadataType() != null && entity.getMetadataType().getId() != null){
			metadataType = metadataService.findById(entity.getMetadataType().getId());	
		}
		//create if it does not exist
		if(metadataType == null){
			metadataType = new MetadataType();
			metadataType.setName(entity.getName());
			metadataType.setDisplayName(entity.getName());
			metadataType.setGrouping(MetadataTypeGrouping.CONNECTOR_TYPE);
			Map<String, LanguageMapping> displayNameMap = new LinkedHashMap<String, LanguageMapping>();
			for(Language language: languages){
				LanguageMapping languageMapping = new LanguageMapping();
				languageMapping.setLanguageId(language.getId());
				languageMapping.setValue(entity.getName());
				displayNameMap.put(languageMapping.getLanguageId(), languageMapping);				
			}
			metadataType.setDisplayNameMap(displayNameMap);
			try {
				metadataService.save(metadataType);
			} catch (BasicDataServiceException e) {
				log.error("Save metadata type failed.", e);
			}
			MetadataTypeEntity metadataTypeEntity = new MetadataTypeEntity();
			metadataTypeEntity = metaDataTypeDozerConverter.convertToEntity(metadataType, true);
			entity.setMetadataType(metadataTypeEntity);
			for(String field : ProvisionConnectorConstant.PROVISION_CONNECTOR_METADATA_CONSTANTS){
				addMetadataEntity(metadataTypeEntity, field, languages);
			}
		} 
	}
	private void addMetadataEntity(MetadataTypeEntity type, String fieldName, List<Language> languages ) {
		if(fieldName == null || fieldName.isEmpty()){
			return;
		}
		MetadataElement metadataElement = null;
		metadataElement = new MetadataElement();
		metadataElement.setMetadataTypeId(type.getId());
		metadataElement.setName(fieldName);
		metadataElement.setDisplayName(fieldName);
		metadataElement.setAttributeName(fieldName);
		metadataElement.setRequired(true);
		
		Map<String, LanguageMapping> displayNameMap = new LinkedHashMap<String, LanguageMapping>();
		for(Language language : languages){
			LanguageMapping languageMapping = new LanguageMapping();
			languageMapping.setLanguageId(language.getId());
			languageMapping.setValue(fieldName);
			displayNameMap.put(languageMapping.getLanguageId(), languageMapping);	
		}
		metadataElement.setLanguageMap(displayNameMap);
		try{
			metadataService.save(metadataElement);
		} catch(BasicDataServiceException e){
			log.error("Save metadata element failed.", e);
		}
	}
}
