package org.openiam.idm.srvc.batch;

import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.util.SpringContextProvider;
import org.springframework.context.ApplicationContext;

public abstract class AbstractBatchTaskThread implements Runnable {

	protected ApplicationContext ctx;
	protected BatchTaskEntity entity;
	
	public AbstractBatchTaskThread(final BatchTaskEntity entity, final ApplicationContext ctx) {
		this.entity = entity;
		this.ctx = ctx;
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	@Override
	public void run() {
		onStart();
		doRun();
		onDone();
	}
	
	protected void onStart() {
		
	}
	
	protected void onDone() {
		
	}
	
	protected abstract void doRun();
}
