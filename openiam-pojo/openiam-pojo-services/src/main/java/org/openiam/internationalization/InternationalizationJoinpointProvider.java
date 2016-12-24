package org.openiam.internationalization;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.util.SpringSecurityHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InternationalizationJoinpointProvider implements InitializingBean, ApplicationContextAware {
	
	private static final Log LOG = LogFactory.getLog(InternationalizationJoinpointProvider.class);
	
	private ApplicationContext ctx;
	
	@Autowired
	private InternationalizationProvider internationalizationProvider;

	@After(value="@annotation(org.openiam.internationalization.LocalizedDatabaseOperation)")
	public void afterLocalizedCRUD(final JoinPoint joinpoint) throws Throwable {
		final String methodName = joinpoint.getSignature().getName();
		final MethodSignature methodSignature = (MethodSignature)joinpoint.getSignature();
		Method method = methodSignature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
		    method = joinpoint.getTarget().getClass().getMethod(methodName, method.getParameterTypes());    
		}
		final LocalizedDatabaseOperation annotation = method.getAnnotation(LocalizedDatabaseOperation.class);
		if(annotation == null) {
			throw new IllegalStateException(String.format("Method does not have %s annotation", LocalizedDatabaseOperation.class.getCanonicalName()));
		}
		
		if(annotation.saveOrUpdate()) {
			doSaveOrUpdate(joinpoint);
		} else if(annotation.delete()) {
			doDelete(joinpoint);
		} else {
			throw new IllegalStateException(String.format("Method has %s annotation, but no flag is set", LocalizedDatabaseOperation.class.getCanonicalName()));
		}
	}
	
	@AfterReturning(pointcut="@annotation(org.openiam.internationalization.LocalizedDatabaseGet) || execution(* org.openiam.elasticsearch.dao.*.find*(..))", returning="returnValue")
	public void afterDatabaseGet(final Object returnValue) {
		if(returnValue != null) {
			if(returnValue instanceof Collection) {
				for(final Object obj : (Collection)returnValue) {
					if(obj instanceof KeyEntity) {
						internationalizationProvider.doDatabaseGet((KeyEntity)obj);
					}
				}
			} else if(returnValue instanceof Page) {
				final Collection collection = ((Page)returnValue).getContent();
				if(CollectionUtils.isNotEmpty(collection)) {
					for(final Object obj : collection) {
						if(obj instanceof KeyEntity) {
							internationalizationProvider.doDatabaseGet((KeyEntity)obj);
						}
					}
				}
			} else {
				if(returnValue instanceof KeyEntity) {
					internationalizationProvider.doDatabaseGet((KeyEntity)returnValue);
				}
			}
		}
	}
	
	@AfterReturning(pointcut="@annotation(org.openiam.internationalization.LocalizedServiceGet)", returning="returnValue")
	public void afterServiceGet(final JoinPoint joinPoint, final Object returnValue) {		
        final String languageId = SpringSecurityHelper.getLanguageId();

        if(languageId != null) {
            if(returnValue != null) {
                if(returnValue instanceof Collection) {
                    for(final Object obj : (Collection)returnValue) {
                        if(obj instanceof BaseIdentity) {
                            internationalizationProvider.doServiceGet((BaseIdentity)obj, languageId);
                        }
                    }
                } else {
                    if(returnValue instanceof BaseIdentity) {
                        internationalizationProvider.doServiceGet((BaseIdentity)returnValue, languageId);
                    }
                }
            }
        }
	}
	
	private void checkArguments(final JoinPoint joinpoint) throws Throwable {
		if(joinpoint.getArgs() == null || joinpoint.getArgs().length == 0) {
			throw new IllegalStateException("No arguments specified on joinpoint");
		}
	}
	
	private void doSaveOrUpdate(final JoinPoint joinpoint) throws Throwable {
		checkArguments(joinpoint);
		for(final Object obj : joinpoint.getArgs()) {
			if(obj instanceof KeyEntity) {
				internationalizationProvider.doSaveUpdate((KeyEntity)obj);
			} else if(obj instanceof Collection) {
				for(final Object o : (Collection)obj) {
					if(o instanceof KeyEntity)  {
						internationalizationProvider.doSaveUpdate((KeyEntity)o);
					}
				}
			}
		}
	}
	
	private void doDelete(final JoinPoint joinpoint) throws Throwable {
		checkArguments(joinpoint);
		for(final Object obj : joinpoint.getArgs()) {
			if(obj instanceof KeyEntity) {
				internationalizationProvider.doDelete((KeyEntity)obj);
			}
		}
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
}
