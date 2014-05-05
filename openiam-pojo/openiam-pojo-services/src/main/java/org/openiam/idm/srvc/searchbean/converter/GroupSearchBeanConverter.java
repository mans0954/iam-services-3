package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Component;

@Component("groupSearchBeanConverter")
public class GroupSearchBeanConverter implements SearchBeanConverter<GroupEntity, GroupSearchBean> {

	@Override
	public GroupEntity convert(GroupSearchBean searchBean) {
		final GroupEntity groupEntity = new GroupEntity();
		groupEntity.setId(searchBean.getKey());
		groupEntity.setName(searchBean.getName());
		if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
			final ManagedSysEntity mSys = new ManagedSysEntity();
			mSys.setId(searchBean.getManagedSysId());
			groupEntity.setManagedSystem(mSys);
		}
		return groupEntity;
	}

}
