package org.openiam.internationalization;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.sf.saxon.om.Navigator.BaseEnumeration;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.annotations.common.reflection.ReflectionUtil;
import org.hibernate.proxy.HibernateProxyHelper;
import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.mchange.v2.codegen.bean.BeangenUtils;

@Component
public class InternationalizationProvider {
	
	@Autowired
	private LanguageMappingDAO languageDAO;
	
	private static Logger LOG = Logger.getLogger(InternationalizationProvider.class);
	
	
	@PostConstruct
	public void init() {
		
	}
	
	public void doServiceGet(final BaseIdentity object, final String languageId) {
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				final Field field = target.getField();
				final BaseIdentity entity = target.getEntity();
				//field.setAccessible(true);
				final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
				
				//final Object fieldObject = ReflectionUtils.getField(field, entity);
				final Object fieldObject = getMethodCallResult(field, entity);
				final Map transientMap = (Map)fieldObject;		
				if(transientMap != null && transientMap.containsKey(languageId)) {
					final String targetFieldName = metadata.targetField();
					if(StringUtils.isNotBlank(targetFieldName)) {
						final Field targetField = ReflectionUtils.findField(entity.getClass(), targetFieldName);
						if(targetField == null) {
							throw new IllegalArgumentException(String.format("Field with name '%s' not found on class %s", targetFieldName, entity.getClass().getCanonicalName()));
						} else if(!(targetField.getType().equals(String.class))) {
							throw new IllegalArgumentException(String.format("Field with name '%s'on class %s must be a String", targetFieldName, entity.getClass().getCanonicalName()));
						} else {
							//targetField.setAccessible(true);
							//ReflectionUtils.setField(targetField, entity, transientMap.get(languageId).getValue());
							final Object mapping = transientMap.get(languageId);
							String value = null;
							if(mapping instanceof LanguageMapping) {
								value = ((LanguageMapping)mapping).getValue();
							} else if(mapping instanceof LanguageMappingEntity) {
								value = ((LanguageMappingEntity)mapping).getValue();
							}
							setValue(targetField, entity, value);
						}
					}
				}
			}
		}
	}
	
	public void doDatabaseGet(final KeyEntity object) {
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				final Field field = target.getField();
				//field.setAccessible(true);
				final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
				final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(target.getEntity().getId(), metadata.referenceType());
				final Map<String, LanguageMappingEntity> dbMap = new HashMap<>();
				if(CollectionUtils.isNotEmpty(dbList)) {
					for(final LanguageMappingEntity dbRecord : dbList) {
						dbMap.put(dbRecord.getLanguageId(), dbRecord);
					}
				}
				//ReflectionUtils.setField(field, target.getEntity(), dbMap);
				setValue(field, target.getEntity(), dbMap);
			}
		}
	}

	public void doSaveUpdate(final KeyEntity object) {
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				doCRUDLogicOnField(target.getField(), (KeyEntity)target.getEntity());
			}
		}
	}
	

	
	public void doDelete(final KeyEntity object) {
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				final Field field = target.getField();
				//field.setAccessible(true);
				final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
				final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(target.getEntity().getId(), metadata.referenceType());
				if(CollectionUtils.isNotEmpty(dbList)) {
					for(final LanguageMappingEntity entity : dbList) {
						languageDAO.delete(entity);
					}
				}
			}
		}
	}
	
	private Set<TargetInternationalizedField> getTargetFields(final BaseIdentity object) {
		if(object != null) {
			return getTargetFields(object, new HashSet<VisitedField>());
		} else {
			return Collections.EMPTY_SET;
		}
	}
	
	private void setValue(final Field field, final BaseIdentity entity, final Object obj) {
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entity.getClass());
			final Method method = PropertyUtils.getWriteMethod(descriptor);
			if(method != null) {
				ReflectionUtils.invokeMethod(method, entity, obj);
			}
		} catch(Throwable e) {
			LOG.error("Can't call method", e);
		}
	}
	
	private Object getMethodCallResult(final Field field, final BaseIdentity entity) {
		Object retVal = null;
		try {
			final PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), entity.getClass());
			final Method method = PropertyUtils.getReadMethod(descriptor);
			if(method != null) {
				retVal = ReflectionUtils.invokeMethod(method, entity);
			}
		} catch(Throwable e) {
			LOG.error("Can't call method", e);
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
	
	private Set<TargetInternationalizedField> getTargetFields(final BaseIdentity entity, final Set<VisitedField> visitedSet) {
		final Set<TargetInternationalizedField> retVal = new HashSet<>();
		
		/* 
		 * you can't just get the class, because an incoming object can be a javassist/cglib object, modified by Hibernate.
		 * These objects won't have the expected annotations.  Therefore, it is necessary to un-proxy the Class Object.
		 * If the object is not of type HibernateProxy, the regular classname will be used.  
		 * See @HibernateProxyHelper.getClassWithoutInitializingProxy
		 */
		final Class<?> clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
		
		if(clazz.isAnnotationPresent(Internationalized.class)) {
			final List<Field> resultList = getDeclaredFields(clazz);
			if(resultList != null) {
				for(final Field field : resultList) {
					final VisitedField visitedField = new VisitedField(clazz, field, entity);
					if(!visitedSet.contains(visitedField)) {
						visitedSet.add(visitedField);
						if(field.isAnnotationPresent(InternationalizedCollection.class)) {
							if(StringUtils.isNotBlank(entity.getId())) {
								retVal.add(new TargetInternationalizedField(field, entity));
							}
						} else if(field.isAnnotationPresent(Internationalized.class)) {
							//field.setAccessible(true);
							//PropertyUtils.getReadMethod(new PropertyDescriptor(propertyName, beanClass))
							//final Object fieldObject = ReflectionUtils.getField(field, entity);
							final Object fieldObject = getMethodCallResult(field, entity);
							if(fieldObject != null) {
								if(fieldObject instanceof Collection) {
									for(final Object o : (Collection)fieldObject) {
										if(o instanceof BaseIdentity) {
											retVal.addAll(getTargetFields((BaseIdentity)o, visitedSet));
										}
									}
								} else if(fieldObject instanceof BaseIdentity) {
									retVal.addAll(getTargetFields((BaseIdentity)fieldObject, visitedSet));
								} else if(fieldObject instanceof Map) {
									for(final Object key : ((Map)fieldObject).keySet()) {
										if(key != null) {
											if(key instanceof BaseIdentity) {
												retVal.addAll(getTargetFields((BaseIdentity)key, visitedSet));
											}
											final Object value = ((Map)fieldObject).get(key);
											if(value != null) {
												retVal.addAll(getTargetFields((BaseIdentity)value, visitedSet));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return retVal;
	}
	
	private void doCRUDLogicOnField(final Field field, final KeyEntity object) {
		//field.setAccessible(true);
		final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
		if(StringUtils.isNotBlank(object.getId()) && StringUtils.isNotBlank(metadata.referenceType())) {
			Collection<LanguageMappingEntity> toDelete = new LinkedList<>();
			Collection<LanguageMappingEntity> toUpdate = new LinkedList<>();
			Collection<LanguageMappingEntity> toSave = new LinkedList<>();
			//final Object fieldObject = ReflectionUtils.getField(field, object);
			final Object fieldObject = getMethodCallResult(field, object);
			final Map<String, LanguageMappingEntity> transientMap = (Map<String, LanguageMappingEntity>)fieldObject;		
			final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(object.getId(), metadata.referenceType());
			if(MapUtils.isEmpty(transientMap)) {
				toDelete = dbList;
			} else {
				/* all new */
				if(CollectionUtils.isEmpty(dbList)) {
					for(final String languageId : transientMap.keySet()) {
						final LanguageMappingEntity entity = transientMap.get(languageId);
						setMetadata(entity, object, languageId, metadata);
						if(StringUtils.isNotBlank(entity.getValue())) {
							toSave.add(entity);
						}
					}
					//toSave = transientMap.values();
				} else {
					for(final LanguageMappingEntity dbEntity : dbList) {
						if(transientMap.containsKey(dbEntity.getLanguageId())) { /* update */
							final String value = transientMap.get(dbEntity.getLanguageId()).getValue();
							/* empty value means delete */
							if(StringUtils.isBlank(value)) {
								toDelete.add(dbEntity);
							} else {
								dbEntity.setValue(value);
								toUpdate.add(dbEntity);
							}
						} else { /* delete */
							toDelete.add(dbEntity);
						}
					}
					
					for(final String languageId : transientMap.keySet()) {
						boolean contains = false;
						final LanguageMappingEntity transientEntity = transientMap.get(languageId);
						for(final LanguageMappingEntity dbEntity : dbList) {
							if(StringUtils.equals(dbEntity.getLanguageId(), languageId)) {
								contains = true;
								break;
							}
						}
						if(!contains) { /* new */
							setMetadata(transientEntity, object, languageId, metadata);
							if(StringUtils.isNotBlank(transientEntity.getValue())) {
								toSave.add(transientEntity);
							}
						}
					}
				}
				
			}
			
			if(CollectionUtils.isNotEmpty(toSave)) {
				for(final LanguageMappingEntity entity : toSave) {
					languageDAO.save(entity);
				}
			}
			if(CollectionUtils.isNotEmpty(toUpdate)) {
				for(final LanguageMappingEntity entity : toUpdate) {
					languageDAO.update(entity);
				}
			}
			if(CollectionUtils.isNotEmpty(toDelete)) {
				for(final LanguageMappingEntity entity : toDelete) {
					languageDAO.delete(entity);
				}
			}
		}
	}
	
	private void setMetadata(final LanguageMappingEntity mappingEntity, final KeyEntity entity, final String languageId, final InternationalizedCollection metadata) {
		mappingEntity.setLanguageId(languageId);
		mappingEntity.setReferenceId(entity.getId());
		mappingEntity.setReferenceType(metadata.referenceType());
	}
	
	private class TargetInternationalizedField {
		private Field field;
		private BaseIdentity entity;
		
		public TargetInternationalizedField(final Field field, final BaseIdentity entity) {
			this.field = field;
			this.entity = entity;
		}

		public Field getField() {
			return field;
		}

		public BaseIdentity getEntity() {
			return entity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((entity == null) ? 0 : entity.hashCode());
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TargetInternationalizedField other = (TargetInternationalizedField) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (entity == null) {
				if (other.entity != null)
					return false;
			} else if (!entity.equals(other.entity))
				return false;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			return true;
		}

		private InternationalizationProvider getOuterType() {
			return InternationalizationProvider.this;
		}
		
		
	}
	
	private class VisitedField {
		private String clazz;
		private Field field;
		private BaseIdentity entity;
		
		public VisitedField(final Class<?> clazz, final Field field, final BaseIdentity entity) {
			this.clazz = clazz.getCanonicalName();
			this.field = field;
			this.entity = entity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result
					+ ((entity == null) ? 0 : entity.hashCode());
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VisitedField other = (VisitedField) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (entity == null) {
				if (other.entity != null)
					return false;
			} else if (!entity.equals(other.entity))
				return false;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			return true;
		}

		private InternationalizationProvider getOuterType() {
			return InternationalizationProvider.this;
		}
		
		
	}
}
