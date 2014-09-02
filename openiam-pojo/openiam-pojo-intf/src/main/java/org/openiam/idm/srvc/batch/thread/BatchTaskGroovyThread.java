package org.openiam.idm.srvc.batch.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

public class BatchTaskGroovyThread extends AbstractBatchTaskThread {

    private static Logger LOG = Logger.getLogger(BatchTaskGroovyThread.class);

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    public BatchTaskGroovyThread(final BatchTaskEntity entity,
            final ApplicationContext ctx) {
        super(entity, ctx);
    }

    @Override
    protected void doRun() {
        Date startDate = new Date();
        String requestId = UUIDGen.getUUID();
        final Map<String, Object> bindingMap = new HashMap<String, Object>();
        bindingMap.put("context", ctx);
        bindingMap.put("taskObj", entity);
        bindingMap.put("lastExecTime", entity.getLastExecTime());
        bindingMap.put("parentRequestId", requestId);
        bindingMap.put("param1", entity.getParam1());
        bindingMap.put("param2", entity.getParam2());
        bindingMap.put("param3", entity.getParam3());
        bindingMap.put("param4", entity.getParam4());

        LOG.info(String.format("Running thread: %s", entity.getId()));
        try {
            Integer output = (Integer) scriptRunner.execute(bindingMap,
                    entity.getTaskUrl());

            if (output.intValue() == 0) {
                this.logSuccess();
            } else {
                this.logFail(null);
            }

            BatchTaskEntity batchTaskEntity = batchService.findById(entity.getId());
            if (batchTaskEntity != null) {
                batchTaskEntity.setLastExecTime(startDate);
                batchService.save(batchTaskEntity);
                entity = batchTaskEntity;
            }

        } catch (Throwable e) {
            LOG.error(e);
            this.logFail(e);
        }
    }

}
