package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.springframework.stereotype.Component;

@Component
public class BatchTaskSearchBeanConverter implements SearchBeanConverter<BatchTaskEntity, BatchTaskSearchBean> {

	@Override
	public BatchTaskEntity convert(final BatchTaskSearchBean searchBean) {
		final BatchTaskEntity entity = new BatchTaskEntity();
		if(searchBean != null) {
			entity.setId(StringUtils.trimToNull(searchBean.getKey()));
			entity.setName(searchBean.getName());
		}
		return entity;
	}

}