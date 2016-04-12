package org.openiam.cache;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.stereotype.Component;

@Component
public class ProvisionUserResourceKeyGenerator implements OpeniamKeyGenerator {

	@Override
	public List<String> generateKey(Object parameter) {
		if(parameter != null) {
			if(parameter instanceof ProvisionUser) {
				final ProvisionUser pUser = (ProvisionUser)parameter;
				if(CollectionUtils.isNotEmpty(pUser.getResources())) {
					return pUser.getResources().stream().map(e -> e.getEntityId()).collect(Collectors.toList());
				}
			}
		}
		return null;
	}

}
