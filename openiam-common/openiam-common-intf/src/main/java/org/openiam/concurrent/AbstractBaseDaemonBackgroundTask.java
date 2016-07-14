package org.openiam.concurrent;

/**
 * @author Alexander Dukkardt
 *
 */
public abstract class AbstractBaseDaemonBackgroundTask extends AbstractBaseRunnableBackgroundTask {

    @Override
    public boolean isDaemon() {
        return true;
    }

}
