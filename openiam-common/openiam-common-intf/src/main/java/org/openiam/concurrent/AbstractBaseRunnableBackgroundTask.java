package org.openiam.concurrent;


/**
 * @author Alexander Dukkardt
 * 
 */
public abstract class AbstractBaseRunnableBackgroundTask extends  AbstractBaseBackgroundTask implements IBaseRunnableBackgroundTask {

    @Override
    public boolean isDaemon() {
        return false;
    }
    
    @Override
    public IBaseRunnableBackgroundTask cloneTask() throws CloneNotSupportedException {
        return (IBaseRunnableBackgroundTask) super.clone();
    }
}
