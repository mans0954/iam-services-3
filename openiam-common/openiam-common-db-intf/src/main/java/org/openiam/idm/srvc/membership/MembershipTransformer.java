package org.openiam.idm.srvc.membership;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;
import org.openiam.idm.srvc.res.domain.ResourceToResourceMembershipXrefEntity;
import org.openiam.membership.MembershipDTO;

public class MembershipTransformer implements ResultTransformer {
	
	public static final MembershipTransformer INSTANCE = new MembershipTransformer();

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		final MembershipDTO dto = new MembershipDTO();
		for(int i = 0; i < aliases.length; i++) {
			final String alias = aliases[i];
			final Object value = tuple[i];
			if(StringUtils.equals(alias, "id")) {
				dto.setId((String)value);
			} else if(StringUtils.equals(alias, "entityId")) {
				dto.setEntityId((String)value);
			} else if(StringUtils.equals(alias, "memberEntityId")) {
				dto.setMemberEntityId((String)value);
			}
		}
		return dto;
	}

	@Override
	public List<MembershipDTO> transformList(List collection) {
		if(CollectionUtils.isNotEmpty(collection)) {
			final List<MembershipDTO> retVal = new LinkedList<MembershipDTO>();
			for(final Object o : collection) {
				retVal.add((MembershipDTO)o);
			}
			return retVal;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

}
