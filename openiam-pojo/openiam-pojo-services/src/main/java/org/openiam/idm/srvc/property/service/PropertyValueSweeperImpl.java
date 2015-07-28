package org.openiam.idm.srvc.property.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.property.converter.PropertyValueConverter;
import org.openiam.property.domain.PropertyValueEntity;
import org.openiam.property.dto.PropertyValue;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service("propertyValueSweeper")
public class PropertyValueSweeperImpl implements Sweepable, PropertyValueSweeper {
	
	private static Logger log = Logger.getLogger(PropertyValueSweeperImpl.class);

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    
    @Autowired
	private PropertyValueService propertyValueService;
    
    @Autowired
	private PropertyValueConverter converter;
    
    private Map<String, PropertyValue> valueCache;
	
	@Override
	@Scheduled(fixedRateString="${org.openiam.property.value.threadsweep}", initialDelayString="${org.openiam.property.value.threadsweep}")
	public void sweep() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				final Map<String, PropertyValue> tempValueCache = new HashMap<String, PropertyValue>();
				final List<PropertyValueEntity> valueList = propertyValueService.getAll();
				converter.convertToDTOList(valueList, true).forEach(dto -> {
					tempValueCache.put(dto.getId(), dto);
				});
				valueCache = tempValueCache;
				return null;
			}
			
		});
	}

	public String getValue(final String key, final Language language) {
		if(key == null) {
			throw new RuntimeException("Key is null");
		}
		
		final PropertyValue value = valueCache.get(key);
		if(value == null) {
			throw new RuntimeException(String.format("Key '%s' does not exist in cache", key));
		}
		
		if(language == null) {
			if(value.isMultilangual()) {
				throw new RuntimeException(String.format("Key '%s' has a value that is marked as multilangual, but the input language is null", key));
			}
		}

		String retval = null;
		if(value.isMultilangual()) {
			if(value.getInternationalizedValues() != null) {
				final LanguageMapping mapping = value.getInternationalizedValues().get(language.getId());
				if(mapping != null) {
					retval = mapping.getValue();
				}
			}
		} else {
			retval = value.getValue();
		}
		return retval;
	}

	@Override
	public String getString(String key) {
		return getValue(key, null);
	}

	@Override
	public boolean getBoolean(String key) {
		return StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), getString(key));
	}

	@Override
	public int getInt(String key) {
		return Integer.valueOf(getString(key)).intValue();
	}
}
