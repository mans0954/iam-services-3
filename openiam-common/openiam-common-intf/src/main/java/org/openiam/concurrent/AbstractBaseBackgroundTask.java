package org.openiam.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

/**
 * @author Alexander Dukkardt
 */
public abstract class AbstractBaseBackgroundTask implements IBaseBackgroundTask {
    private EnumMap<? extends Enum<?>, Object> backgroundTaskArgs;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 
     */
    public AbstractBaseBackgroundTask() {
    }

    /**
     * @param backgroundTaskArgs
     */
    public AbstractBaseBackgroundTask(EnumMap<? extends Enum<?>, Object> backgroundTaskArgs) throws Exception {
        setBackgroundTaskArgs(backgroundTaskArgs);
    }

    /**
     * @return the backgroundTaskArgs
     */
    protected EnumMap<? extends Enum<?>, Object> getBackgroundTaskArgs() {
        return backgroundTaskArgs;
    }

    @Override
    public void setBackgroundTaskArgs(
            EnumMap<? extends Enum<?>, Object> backgroundTaskArgs)
            throws Exception {
        this.backgroundTaskArgs = backgroundTaskArgs;
    }

}
