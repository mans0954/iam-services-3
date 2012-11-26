package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.springframework.stereotype.Component;

@Component("groupSearchBeanConverter")
public class GroupSearchBeanConverter implements SearchBeanConverter<GroupEntity, GroupSearchBean> {

	@Override
	public GroupEntity convert(GroupSearchBean searchBean) {
		final GroupEntity groupEntity = new GroupEntity();
		groupEntity.setGrpId(searchBean.getKey());
		groupEntity.setGrpName(searchBean.getName());
		return groupEntity;
	}

}
