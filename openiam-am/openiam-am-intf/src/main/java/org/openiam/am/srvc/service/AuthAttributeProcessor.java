package org.openiam.am.srvc.service;

import java.util.EnumMap;

import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;

public interface AuthAttributeProcessor {

	public String process(final String amAttributeId, final EnumMap<AmAttributes, Object> objectMap) throws Exception;
	public String process(final String amAttributeId, final String userId) throws Exception;
}
