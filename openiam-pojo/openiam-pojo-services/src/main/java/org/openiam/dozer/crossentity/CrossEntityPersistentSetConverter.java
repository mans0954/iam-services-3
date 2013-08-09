package org.openiam.dozer.crossentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dozer.ConfigurableCustomConverter;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.hibernate.collection.PersistentSet;
import org.openiam.util.SpringContextProvider;

public class CrossEntityPersistentSetConverter extends AbstractCrossEntityCollectionConverter implements ConfigurableCustomConverter {
	
	@Override
	public Object convert(Object destVal, Object sourceVal, Class<?> destClass, Class<?> sourceClass) {
		Set retVal = null;
		if(sourceVal != null) {
			retVal = new LinkedHashSet();
			final Set sourceSet = (Set)sourceVal;
			convert(sourceSet, retVal);
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
