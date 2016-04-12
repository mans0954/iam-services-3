package org.openiam.cache;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationAttribute;
import org.openiam.idm.srvc.org.service.OrganizationAttributeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrgAttributeToOrganizationKeyGenerator implements OpeniamKeyGenerator {

	@Autowired
	private OrganizationAttributeDAO dao;
	
	@Override
	@Transactional
	public List<String> generateKey(Object parameter) {
		if(parameter != null) {
			final List<String> keys = new LinkedList<String>();
			if(parameter instanceof String) {
				final OrganizationAttributeEntity entity = dao.findById((String)parameter);
				if(entity != null) {
					keys.add(entity.getOrganization().getId());
				}
			} else if(parameter instanceof OrganizationAttributeEntity) {
				keys.add(((OrganizationAttributeEntity)parameter).getOrganization().getId());
			} else if(parameter instanceof OrganizationAttribute) {
				keys.add(((OrganizationAttribute)parameter).getOrganizationId());
			}
			return keys;
		}
		return null;
	}

}
