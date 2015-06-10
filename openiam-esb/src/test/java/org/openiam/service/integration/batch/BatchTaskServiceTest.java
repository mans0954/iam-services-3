package org.openiam.service.integration.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
//import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;
//import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.openiam.idm.srvc.batch.service.BatchDataService;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.Assert;
import org.testng.annotations.Test;

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

	@Override
	protected String getId(BatchTask bean) {
		return bean.getId();
	}

	@Override
	protected void setId(BatchTask bean, String id) {
		bean.setId(id);
	}

	@Override
	protected void setName(BatchTask bean, String name) {
		bean.setName(name);
	}

	@Override
	protected String getName(BatchTask bean) {
		return bean.getName();
	}

	@Override
	protected void setNameForSearch(BatchTaskSearchBean searchBean, String name) {
		searchBean.setName(name);
	}

	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<BatchTask, BatchTaskSearchBean> key = doClusterTest();
		BatchTask instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
//	public ClusterKey<T, S> doClusterTest() throws Exception {
//		/* create and save */
//		T instance = createBean();
//		Response response = saveAndAssert(instance);
//		instance.setId((String)response.getResponseValue());
//
//		/* find */
//		final S searchBean = newSearchBean();
//		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
//		searchBean.setKey(instance.getId());
//
//		/* confirm save on both nodes */
//		instance = assertClusteredSave(searchBean);
//		return new ClusterKey<T, S>(instance, searchBean);
//	}

//	@Test
//	public void testScheduledTaskFind() throws Exception {
//		final BatchTask task = super.doClusterTest().getDto();
//		try {
//			Response response = batchServiceClient.schedule(task.getId(), new Date());
//			Assert.assertNotNull(response);
//			Assert.assertTrue(response.isSuccess());
//
//			final BatchTaskScheduleSearchBean searchBean = new BatchTaskScheduleSearchBean();
//			searchBean.setTaskId(task.getId());
//
//			List<BatchTaskSchedule> scheduleList = batchServiceClient.getSchedulesForTask(searchBean, 0, Integer.MAX_VALUE);
//			CollectionUtils.isNotEmpty(scheduleList);
//
//			scheduleList = batchServiceClient.getSchedulesForTask(searchBean, 0, Integer.MAX_VALUE);
//			CollectionUtils.isNotEmpty(scheduleList);
//
//			Assert.assertEquals(batchServiceClient.getNumOfSchedulesForTask(searchBean), 1);
//
//			batchServiceClient.deleteScheduledTask(scheduleList.get(0).getId());
//
//			batchServiceClient.schedule(task.getId(), new Date());
//			batchServiceClient.schedule(task.getId(), new Date());
//			Assert.assertEquals(batchServiceClient.getNumOfSchedulesForTask(searchBean), 2);
//			Assert.assertEquals(batchServiceClient.getNumOfSchedulesForTask(searchBean), 2);
//		} finally {
//			if(task != null) {
//				delete(task);
//			}
//		}
//	}
}
