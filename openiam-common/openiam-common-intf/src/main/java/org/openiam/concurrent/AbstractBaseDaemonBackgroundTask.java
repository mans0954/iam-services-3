package org.openiam.concurrent;

/**
 * @author Sergei Barinov
 *
 */
public abstract class AbstractBaseDaemonBackgroundTask extends
        AbstractBaseRunnableBackgroundTask {

    /*
     * (non-Javadoc)
     * 
     * @see com.eharvest.erp.common.service.task.IErpRunnableBackgroundTask#isDaemon()
     */
    @Override
    public boolean isDaemon() {
        return true;
    }

}
