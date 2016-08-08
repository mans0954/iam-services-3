package org.openiam.concurrent;

/**
 * @author Alexander Dukkardt
 * 
 */
public interface IBaseRunnableBackgroundTask extends IBaseBackgroundTask, Runnable {

    public IBaseRunnableBackgroundTask cloneTask() throws CloneNotSupportedException;
    public boolean isDaemon();

}
