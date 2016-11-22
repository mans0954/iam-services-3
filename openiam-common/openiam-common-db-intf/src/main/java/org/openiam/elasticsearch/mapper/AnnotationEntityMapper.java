package org.openiam.elasticsearch.mapper;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.netty.util.internal.ConcurrentHashMap;
import org.openiam.elasticsearch.annotation.ElasticsearchFieldBridge;
import org.openiam.elasticsearch.annotation.NestedCollectionType;
import org.openiam.elasticsearch.annotation.NestedMapType;
import org.openiam.elasticsearch.annotation.SimpleElasticSearchJSONMapping;
import org.openiam.elasticsearch.bridge.ElasticsearchBrigde;
import org.openiam.elasticsearch.converter.FieldMapper;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.util.SpringContextProvider;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.NestedField;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotationEntityMapper implements EntityMapper {
	
	private static final Log LOG = LogFactory.getLog(AnnotationEntityMapper.class);
	
	final ObjectMapper mapper = new CustomJacksonMapper();
	
	private Map<Class<?>, ElasticsearchBrigde> bridgeCache = new HashMap<Class<?>, ElasticsearchBrigde>();
	private Map<Field, FieldMapper<?>> keyMappers = new ConcurrentHashMap<Field, FieldMapper<?>>();
	private Map<Field, FieldMapper<?>> valueMappers = new ConcurrentHashMap<Field, FieldMapper<?>>();
	
	public AnnotationEntityMapper() {
		
	}
	
	private FieldMapper<?> getKeyMapper(final Field field, final NestedMapType type) throws InstantiationException, IllegalAccessException {
		FieldMapper<?> mapper = keyMappers.get(field);
		if(mapper == null) {
			mapper = type.keyMapper().newInstance();
			keyMappers.put(field, mapper);
		}
		return mapper;
	}
	
	private FieldMapper<?> getValueMapper(final Field field, final NestedMapType type) throws InstantiationException, IllegalAccessException {
		FieldMapper<?> mapper = valueMappers.get(field);
		if(mapper == null) {
			mapper = type.valueMapper().newInstance();
			valueMappers.put(field, mapper);
		}
		return mapper;
	}

	@Override
	public String mapToString(final Object entity) throws IOException {
		if(entity != null && entity.getClass().getAnnotation(SimpleElasticSearchJSONMapping.class) != null) {
			return mapper.writeValueAsString(entity);
		} else {
			final Map<String, Object> valueMap = new HashMap<String, Object>();
			populateMap(valueMap, entity, entity.getClass());
			return mapper.writeValueAsString(valueMap);
		}
	}

	@Override
	public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
		T retval = null;
		if(clazz.getAnnotation(SimpleElasticSearchJSONMapping.class) != null) {
			retval = mapper.readValue(source, clazz);
		} else {
			final Map<String, Object> valueMap = mapper.readValue(source, Map.class);
			T entity = null;
			try {
				entity = clazz.newInstance();
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			try {
				populateObject(valueMap, entity, clazz);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			retval = entity;
		}
		return retval;
	}
	
	private void populateObject(final Map<String, Object> valueMap, final Object entity, final Class<?> clazz) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException {
		if(entity != null) {
			final List<Field> fields = getDeclaredFields(clazz);
			if(CollectionUtils.isNotEmpty(fields)) {
				for(final Field field : fields) {
					final Object value = getValue(field, valueMap.get(field.getName()));
					if(value != null) {
						setValue(field, entity, value);
					}
				}
			}
		}
	}
	
	private Object getValue(final Field field, Object value) throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException {
		final org.springframework.data.elasticsearch.annotations.Field esField = 
				field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
		final NestedField nestedField = field.getAnnotation(NestedField.class);
		
		if(esField != null && value != null) {
			boolean isNestedField = (nestedField != null) || (FieldType.Nested.equals(esField.type()));
			if(isNestedField) {				
				if(Collection.class.isAssignableFrom(field.getType())) {
					final NestedCollectionType nestedFieldType = field.getAnnotation(NestedCollectionType.class);
					if(nestedFieldType == null) {
						throw new RuntimeException(String.format("%s annotation not present on %s", NestedCollectionType.class, field));
					}
					
					if(!field.getType().isInterface()) {
						throw new RuntimeException(String.format("Expected field %s to be an interface", field));
					}
					Collection<Object> collection = null;
					if(field.getType().equals(Set.class)) {
						collection = new HashSet<Object>();
					} else if(field.getType().equals(List.class)) {
						collection = new LinkedList<Object>();
					} else {
						throw new RuntimeException(String.format("Unsupported type %s for field %s and value %s", field.getType(), field, value));
					}
					
					if(value instanceof Collection) {
						for(final Object o : (Collection)value) {
							collection.add(mapper.convertValue(o, nestedFieldType.value()));
						}
					}
					value = collection;
				} else if(Map.class.isAssignableFrom(field.getType())) {
					final NestedMapType nestedFieldType = field.getAnnotation(NestedMapType.class);
					if(nestedFieldType == null) {
						throw new RuntimeException(String.format("%s annotation not present on %s", NestedMapType.class, field));
					}
					final Map map = new HashMap();
					final FieldMapper<?> keyMapper = getKeyMapper(field, nestedFieldType);
					final FieldMapper<?> valueMapper = getValueMapper(field, nestedFieldType);
					for(Object mapKey : ((Map)value).keySet()) {
						final Object transformedKey = keyMapper.map(mapKey);
						final Object transformedValue = valueMapper.map(((Map)value).get(mapKey));
						if(transformedKey != null && transformedValue != null) {
							map.put(transformedKey, transformedValue);
						}
					}
					value = map;
				}
			} else {
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
				
				if(FieldType.Date.equals(esField.type())) {
					value = new Date(Long.valueOf(value.toString()));
				}
			}
		}
		return value;
	}
	
	private ElasticsearchBrigde getBridge(final ElasticsearchFieldBridge bridge) {
		ElasticsearchBrigde esBridge = bridgeCache.get(bridge.impl());
		if(esBridge == null) {
			try {
				esBridge = ((ElasticsearchBrigde)bridge.impl().newInstance());
				SpringContextProvider.autowire(esBridge);
				bridgeCache.put(bridge.impl(), esBridge);
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return esBridge;
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
			LOG.error(String.format("Can't call method with field %s.  Value was %s.  Value class: %s", field, obj, (obj != null) ? obj.getClass() : "N/A"), e);
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
