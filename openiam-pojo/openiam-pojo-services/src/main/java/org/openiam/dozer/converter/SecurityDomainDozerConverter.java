package org.openiam.dozer.converter;

import java.util.List;

import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;
import org.springframework.stereotype.Component;

@Component("securityDomainDozerMapper")
public class SecurityDomainDozerConverter extends AbstractDozerEntityConverter<SecurityDomain, SecurityDomainEntity> {
	
	@Override
	public SecurityDomainEntity convertEntity(SecurityDomainEntity entity,
	        boolean isDeep) {
	    return convert(entity, isDeep, SecurityDomainEntity.class);
	}
	
	@Override
	public SecurityDomain convertDTO(SecurityDomain entity, boolean isDeep) {
	    return convert(entity, isDeep, SecurityDomain.class);
	}
	
	@Override
	public SecurityDomainEntity convertToEntity(SecurityDomain entity,
	        boolean isDeep) {
	    return convertToCrossEntity(entity, isDeep, SecurityDomainEntity.class);
	}
	
	@Override
	public SecurityDomain convertToDTO(SecurityDomainEntity entity,
	        boolean isDeep) {
	    return convertToCrossEntity(entity, isDeep, SecurityDomain.class);
	}
	
	@Override
	public List<SecurityDomainEntity> convertToEntityList(List<SecurityDomain> list,
	        boolean isDeep) {
	    return convertListToCrossEntity(list, isDeep, SecurityDomainEntity.class);
	}
	
	@Override
	public List<SecurityDomain> convertToDTOList(List<SecurityDomainEntity> list,
	        boolean isDeep) {
	    return convertListToCrossEntity(list, isDeep, SecurityDomain.class);
	}

}
