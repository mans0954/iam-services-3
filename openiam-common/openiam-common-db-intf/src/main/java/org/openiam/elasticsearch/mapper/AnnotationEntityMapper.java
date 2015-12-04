package org.openiam.elasticsearch.mapper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.bridge.ElasticsearchBrigde;
import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationEntityMapper implements EntityMapper {
	
	private static final Log LOG = LogFactory.getLog(AnnotationEntityMapper.class);
	
	final ObjectMapper mapper = new CustomJacksonMapper();
	
	public AnnotationEntityMapper() {
		
	}

	@Override
	public String mapToString(final Object entity) throws IOException {
		final Map<String, Object> valueMap = new HashMap<String, Object>();
		populateMap(valueMap, entity, entity.getClass());
		return mapper.writeValueAsString(valueMap);
	}

	@Override
	public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
		final Map<String, Object> valueMap = mapper.readValue(source, Map.class);
		T entity = null;
		try {
			entity = clazz.newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		populateObject(valueMap, entity, clazz);
		return entity;
	}
	
	private void populateObject(final Map<String, Object> valueMap, final Object entity, final Class<?> clazz) {
		if(entity != null) {
			final List<Field> fields = getDeclaredFields(clazz);
			if(CollectionUtils.isNotEmpty(fields)) {
				for(final Field field : fields) {
					final org.springframework.data.elasticsearch.annotations.Field esField = 
							field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
					Object value = valueMap.get(field.getName());
					if(esField != null && value != null) {
						final ElasticsearchFieldBridge bridge = field.getAnnotation(ElasticsearchFieldBridge.class);
						if(bridge != null) {
							value = getBridge(bridge).stringToObject(value.toString());
						}
						if(field.getType().isEnum()) {
							final Object[] constants = field.getType().getEnumConstants();
							if(constants == null || constants.length == 0) {
								throw new RuntimeException(String.format("Field %s did not have any enum constants", field));
							}
							for(final Object o : constants) {
								if(o.toString().equals(value.toString())) {
									value = o;
									break;
								}
							}
						}
						setValue(field, entity, value);
					}
				}
			}
		}
	}
	
	private ElasticsearchBrigde getBridge(final ElasticsearchFieldBridge bridge) {
		try {
			return ((ElasticsearchBrigde)bridge.impl().newInstance());
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void populateMap(final Map<String, Object> valueMap, final Object entity, final Class<?> clazz) {
		if(entity != null) {
			final List<Field> fields = getDeclaredFields(clazz);
			if(CollectionUtils.isNotEmpty(fields)) {
				for(final Field field : fields) {
					//TODO:  getAnnotation is extermely slow.  Need to cache it.  This is NOT prod ready until then
					final org.springframework.data.elasticsearch.annotations.Field esField = 
							field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
					if(esField != null) {
						Object value = getMethodCallResult(field, entity);
						final ElasticsearchFieldBridge bridge = field.getAnnotation(ElasticsearchFieldBridge.class);
						if(bridge != null) {
							value = getBridge(bridge).objectToString(value);
						}
						valueMap.put(field.getName(), value);
					}
				}
			}
		}
	}
	
	private void setValue(final Field field, final Object entity, final Object obj) {
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entity.getClass());
			final Method method = PropertyUtils.getWriteMethod(descriptor);
			if(method != null) {
				ReflectionUtils.invokeMethod(method, entity, obj);
			}
		} catch(Throwable e) {
			LOG.error(String.format("Can't call method with field %s", field), e);
		}
	}
	
	private Object getMethodCallResult(final Field field, final Object entity) {
		Object retVal = null;
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entity.getClass());
			final Method method = PropertyUtils.getReadMethod(descriptor);
			if(method != null) {
				retVal = ReflectionUtils.invokeMethod(method, entity);
			}
		} catch(Throwable e) {
			LOG.error(String.format("Can't call method.  Field %s, entity: %s, class: %s", field, entity, entity.getClass()), e);
		}
		return retVal;
	}
	
	private List<Field> getDeclaredFields(final Class<?> clazz) {
		final List<Field> resultList = new LinkedList<>();
		if(clazz != null) {
			if(clazz.getDeclaredFields() != null) {
				for(final Field field : clazz.getDeclaredFields()) {
					resultList.add(field);
				}
			}
			
			resultList.addAll(getDeclaredFields(clazz.getSuperclass()));
		}
		return resultList;
	}
}
