package org.openiam.idm.srvc.batch;

import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.util.SpringContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

public class BatchTaskSpringThread extends AbstractBatchTaskThread {
	
	private static Logger LOG = Logger.getLogger(BatchTaskSpringThread.class);
	
	public BatchTaskSpringThread(final BatchTaskEntity entity, final ApplicationContext ctx) {
		super(entity, ctx);
	}

	@Override
	protected void doRun() {
        Date startDate = new Date();
		if(StringUtils.isNotBlank(entity.getSpringBean()) && StringUtils.isNotBlank(entity.getSpringBeanMethod())) {
			try {
			    final Object obj = ctx.getBean(entity.getSpringBean());
				if(obj != null) {
					final Method method = ReflectionUtils.findMethod(obj.getClass(), entity.getSpringBeanMethod());
					if(method != null) {
						method.invoke(obj, null);
                        BatchTaskEntity batchTaskEntity = batchService.findById(entity.getId());
                        if (batchTaskEntity != null) {
                            batchTaskEntity.setLastExecTime(startDate);
                            batchService.save(batchTaskEntity);
                            entity = batchTaskEntity;
                        }
					}
				}
			} catch(Throwable e) {
				LOG.error("Can't invoke spring thread", e);
			}
		}
	}

	
}
