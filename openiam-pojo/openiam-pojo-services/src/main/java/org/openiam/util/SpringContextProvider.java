package org.openiam.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextProvider implements ApplicationContextAware {

	private static ApplicationContext ctx;
	
	@SuppressWarnings("static-access")
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	public static Object getBean(final String beanName) {
		return ctx.getBean(beanName);
	}
	
	public static <T> T getBean(final String beanName, final Class<T> clazz) {
		return ctx.getBean(beanName, clazz);
	}

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }
	
	public static void autowire(final Object toAutowire) {
		if(toAutowire != null) {
			ctx.getAutowireCapableBeanFactory().autowireBean(toAutowire);
		}
	}
	
	@Override
	public void finalize() {
		ctx = null;
	}
}
