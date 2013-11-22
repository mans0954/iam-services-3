package org.openiam.dozer.crossentity;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.dozer.ConfigurableCustomConverter;
import org.dozer.Mapper;
import org.openiam.util.SpringContextProvider;

public class CrossEntityPersistentListConverter extends AbstractCrossEntityCollectionConverter implements ConfigurableCustomConverter {
	
	@Override
	public Object convert(Object destVal, Object sourceVal, Class<?> destClass, Class<?> sourceClass) {
		Collection retVal = null;
		if(sourceVal != null) {
            if(sourceVal instanceof List) {
			    final List sourceList = (List)sourceVal;
			    retVal = new ArrayList(sourceList.size());
                convert(sourceList, retVal);
            } else if(sourceVal instanceof Set) {
                final Set sourceList = (Set)sourceVal;
                retVal = new HashSet(sourceList.size());
                convert(sourceList, retVal);
            }
		}
		return retVal;
	}

	@Override
	public void setParameter(final String mapperBeanName) {
		if(StringUtils.isNotBlank(mapperBeanName)) {
			mapper = (Mapper)SpringContextProvider.getBean(mapperBeanName);
		}
	}
}
