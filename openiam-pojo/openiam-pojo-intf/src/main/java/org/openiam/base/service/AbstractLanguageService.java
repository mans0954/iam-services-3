package org.openiam.base.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;

public abstract class AbstractLanguageService {
	
	protected void setReferenceType(final LanguageMappingEntity entity, final String referenceType, final String referenceId) {
		if(entity != null) {
			entity.setReferenceType(referenceType);
			entity.setReferenceId(referenceId);
		}
	}

	/* assumes same referenceId and referenceType */
	protected Map<String, LanguageMappingEntity> mergeLanguageMaps(final Map<String, LanguageMappingEntity> persistentMap, Map<String, LanguageMappingEntity> transientMap) {
		//final Map<String, Set<String>> deleteMap = new HashMap<String, Set<String>>();
		
		transientMap = (transientMap != null) ? transientMap : new HashMap<String, LanguageMappingEntity>();
		final Map<String, LanguageMappingEntity> retVal = (persistentMap != null) ? persistentMap : new HashMap<String, LanguageMappingEntity>();
		
		/* remove empty strings */
		for(final Iterator<Entry<String, LanguageMappingEntity>> it = transientMap.entrySet().iterator(); it.hasNext();) {
			final Entry<String, LanguageMappingEntity> entry = it.next();
			final LanguageMappingEntity entity = entry.getValue();
			if(StringUtils.isBlank(entity.getValue())) {
				it.remove();
			}
		}
		
		/* update existing entries */
		for(final LanguageMappingEntity transientEntry : transientMap.values()) {
			for(final LanguageMappingEntity persistentEntry : retVal.values()) {
				if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
					persistentEntry.setValue(transientEntry.getValue());
				}
			}
		}
		
		/* remove old entries */
		for(final Iterator<Entry<String, LanguageMappingEntity>> it = retVal.entrySet().iterator(); it.hasNext();) {
			final Entry<String, LanguageMappingEntity> entry = it.next();
			final LanguageMappingEntity persistentEntry = entry.getValue();
			boolean contains = false;
			for(final LanguageMappingEntity transientEntry : transientMap.values()) {
				if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				it.remove();
			}
		}
		
		/* add new entries */
		for(final LanguageMappingEntity transientEntry : transientMap.values()) {
			boolean found = false;
			for(final LanguageMappingEntity persistentEntry : retVal.values()) {
				if(StringUtils.isNotEmpty(transientEntry.getValue())) {
					if(StringUtils.equals(transientEntry.getLanguageId(), persistentEntry.getLanguageId())) {
						found = true;
						break;
					}
				}
			}
			if(!found) {
				retVal.put(transientEntry.getLanguageId(), transientEntry);
			}
		}
		
		return retVal;
	}
}
