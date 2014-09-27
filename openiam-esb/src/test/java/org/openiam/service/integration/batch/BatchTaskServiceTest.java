package org.openiam.service.integration.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.service.BatchDataService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BatchTaskServiceTest extends AbstractKeyNameServiceTest<BatchTask, BatchTaskSearchBean> {
	
	@Autowired
	@Qualifier("batchServiceClient")
	private BatchDataService batchServiceClient;

	@Override
	protected BatchTask newInstance() {
		final BatchTask task = new BatchTask();
		
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, 1);
		task.setRunOn(calendar.getTime());
		task.setTaskUrl("batch/accountLockedNotification.groovy"); // temp
		return task;
	}

	@Override
	protected BatchTaskSearchBean newSearchBean() {
		return new BatchTaskSearchBean();
	}

	@Override
	protected Response save(BatchTask t) {
		return batchServiceClient.save(t);
	}

	@Override
	protected Response delete(BatchTask t) {
		return batchServiceClient.removeBatchTask(t.getId());
	}

	@Override
	protected BatchTask get(String key) {
		return batchServiceClient.getBatchTask(key);
	}

	@Override
	public List<BatchTask> find(BatchTaskSearchBean searchBean, int from,
			int size) {
		return batchServiceClient.findBeans(searchBean, from, size);
	}

}
