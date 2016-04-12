package org.openiam.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.springframework.stereotype.Component;

@Component
public class ResourceToResourcePropKeyGenerator implements OpeniamKeyGenerator {

	@Override
	public List<String> generateKey(Object parameter) {
		if(parameter != null) {
			List<String> ids = Collections.EMPTY_LIST;
			if(parameter instanceof Resource) {
				final Collection<ResourceProp> properties = ((Resource)parameter).getResourceProps();
				if(CollectionUtils.isNotEmpty(properties)) {
					ids = properties.stream().map(e -> e.getId()).collect(Collectors.toList());
				}
			}
		}
		return null;
	}

}
