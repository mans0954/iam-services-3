package org.openiam.util;

import java.lang.reflect.Field;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

@Component("springContextProvider")
public class SpringContextProvider implements ApplicationContextAware, EmbeddedValueResolverAware {

	private static StringValueResolver propertyResolver;
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

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		propertyResolver = resolver;
	}
	
	public static void resolveProperties(final Object obj) {
		if(obj != null) {
			Class<?> clazz = obj.getClass();
			while(clazz != null) {
				setPlaceholders(obj, clazz);
				clazz = clazz.getSuperclass();
			}
		}
	}
	
	private static void setPlaceholders(final Object obj, Class<?> clazz) {
		if(clazz.getDeclaredFields() != null) {
			for(final Field field : clazz.getDeclaredFields()) {
				if(field.isAnnotationPresent(Value.class)) {
					field.setAccessible(true);
					final Class<?> fieldClazz = field.getType();
					final Value valueAnnotation = field.getAnnotation(Value.class);
					Object parsedValue = null;
					final String value = propertyResolver.resolveStringValue(valueAnnotation.value());
					if(value != null) {
						if(Boolean.class.equals(fieldClazz)) {
							parsedValue = BooleanUtils.toBooleanObject(value);
						} else if(fieldClazz.isInstance(Number.class)) {
							parsedValue = NumberUtils.createNumber(value);
						} else if(fieldClazz.equals(String.class)) {
							parsedValue = value;
						}
						ReflectionUtils.setField(field, obj, parsedValue);
					}
				}
			}
		}
	}
}
