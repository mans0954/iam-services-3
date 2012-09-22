package org.openiam.dozer;

import org.dozer.DozerBeanMapper;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.factory.annotation.Required;

public class ProvisionDozerUtils {
	private DozerBeanMapper deepMapper;
	private DozerBeanMapper shallowMapper;
	
	@Required
	public void setDeepMapper(final DozerBeanMapper deepMapper) {
		this.deepMapper = deepMapper;
	}
	
	@Required
	public void setShallowMapper(final DozerBeanMapper shallowMapper) {
		this.shallowMapper = shallowMapper;
	}
	
	public ProvisionUser getDozerDeepedMappedProvisionUser(final ProvisionUser provisionUser) {
		ProvisionUser retVal = null;
		if(provisionUser != null) {
			retVal = deepMapper.map(provisionUser, ProvisionUser.class);
		}
		return retVal;
	}
}
