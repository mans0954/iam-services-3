package org.openiam.cache;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.Table;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ResolvableType;

/**
 * Ensures that no developer caches
 * Hibernate Entity objects using @Cacheable
 * 
 * fixes AM-851 - Custom @Cacheable annotations should *never* cache Entity objects
 * 
 * @author lbornova
 *
 */
public class CacheBeanPostProcessor implements BeanPostProcessor {
	
	private Class<?> getTargetClass(Object target) {
		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
		if (targetClass == null && target != null) {
			targetClass = target.getClass();
		}
		return targetClass;
	}
	
	private Method getActualMethod(Method method, Class<?> targetClass) {
		/*
		 * The method is from the interface, which will *not* have the annotations that we
		 * need.  As a result, let's get it from the class instead
		 */
		try {
			method = targetClass.getMethod(method.getName(), method.getParameterTypes());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		return method;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		// TODO Auto-generated method stub
		final Class<?> targetClass = getTargetClass(bean);
		final Method[] methods = targetClass.getMethods();
		if(methods != null) {
			for(Method method : methods) {
				Caching caching = method.getAnnotation(Caching.class);
				Cacheable cacheable = method.getAnnotation(Cacheable.class);
				if(caching == null && cacheable == null) {
					method = getActualMethod(method, targetClass);
					caching = method.getAnnotation(Caching.class);
					cacheable = method.getAnnotation(Cacheable.class);
				}
				
				if(caching != null) {
					if(caching.cacheable() != null && caching.cacheable().length > 0) {
						cacheable = caching.cacheable()[0];
					}
				}
				
				/* 
				 * now, we've established that the method has a custom @Cacheable
				 * 
				 *  Make sure that the return type does *not* have any JPA classes
				 */
				if(cacheable != null) {
					ResolvableType type = ResolvableType.forMethodReturnType(method);
					/* in case the return value is a Collection<?>, figure out what "?" is */
					if(Collection.class.isAssignableFrom(type.resolve()) && type.hasGenerics()) {
						type = type.getGeneric();
					}
					final Class<?> genericReturnType = type.resolve();
					if(genericReturnType.isAnnotationPresent(Table.class)) {
						/* NEVER CACHE JPA STUFF, EVER!!!! */
						throw new IllegalStateException(String.format("%s:%s has is defined as @Cacheable,but contains a JPA return type.  "
														+ "OpenIAM does not support this, as it may lead to LazyInitializationExceptions of collections.  "
														+ "See AM-851", targetClass, method));
					}
				}
			}
		}
		return bean;
	}
}
