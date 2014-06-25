package org.openiam.elasticsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by: Alexander Duckardt
 * Date: 6/21/14.
 */
public abstract class ESAbstractFactoryBean<T> implements FactoryBean<T>, InitializingBean, DisposableBean {
    protected final Log logger = LogFactory.getLog(getClass());
    protected T object;

    @Override
    public T getObject() throws Exception {
        return this.object;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.object = initialize();
    }

    protected abstract T initialize() throws Exception;
}
