package org.openiam.internationalization;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxyHelper;
import org.openiam.base.BaseIdentity;
import org.openiam.base.domain.KeyEntity;
import org.openiam.hibernate.HibernateUtils;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
import org.openiam.idm.srvc.lang.service.LanguageMappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InternationalizationProvider {
	
	@Autowired
	private LanguageMappingDAO languageDAO;
	
	private static Logger LOG = Logger.getLogger(InternationalizationProvider.class);
	
	private class ClassAnnotationKey {
		
		private Class<?> clazz;
		private Class<?> annotation;
		
		ClassAnnotationKey(final Class<?> clazz, final Class<?> annotation) {
			this.clazz = clazz;
			this.annotation = annotation;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public Class<?> getAnnotation() {
			return annotation;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((annotation == null) ? 0 : annotation.hashCode());
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
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
			ClassAnnotationKey other = (ClassAnnotationKey) obj;
			if (annotation == null) {
				if (other.annotation != null)
					return false;
			} else if (!annotation.equals(other.annotation))
				return false;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FieldAnnotationKey [clazz=" + clazz + ", annotation="
					+ annotation + "]";
		}		
		
		
 	}
	
	private class FieldAnnotationKey {
		
		private Field field;
		private Class<?> annotation;
		
		FieldAnnotationKey(final Field field, final Class<?> annotation) {
			this.field = field;
			this.annotation = annotation;
		}

		public Field getField() {
			return field;
		}

		public Class<?> getAnnotation() {
			return annotation;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((annotation == null) ? 0 : annotation.hashCode());
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
			FieldAnnotationKey other = (FieldAnnotationKey) obj;
			if (annotation == null) {
				if (other.annotation != null)
					return false;
			} else if (!annotation.equals(other.annotation))
				return false;
			if (field == null) {
				if (other.field != null)
					return false;
			} else if (!field.equals(other.field))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FieldAnnotationKey [field=" + field + ", annotation="
					+ annotation + "]";
		}		
		
		
 	}
	
	private Map<ClassAnnotationKey, Boolean> classAnnotationCache = 
			new HashMap<InternationalizationProvider.ClassAnnotationKey, Boolean>();
	private Map<FieldAnnotationKey, Boolean> fieldAnnotationCache = 
			new HashMap<InternationalizationProvider.FieldAnnotationKey, Boolean>();
	
	private boolean isAnnotationPresent(final Class<?> clazz, final Class<? extends Annotation> annotation) {
		final ClassAnnotationKey key = new ClassAnnotationKey(clazz, annotation);
		if(!classAnnotationCache.containsKey(key)) {
			final Boolean result = new Boolean(clazz.isAnnotationPresent(annotation));
			synchronized(this) {
				classAnnotationCache.put(key, result);
			}
			return result;
		} else {
			return classAnnotationCache.get(key);
		}
	}
	
	private boolean isAnnotationPresent(final Field field, final Class<? extends Annotation> annotation) {
		final FieldAnnotationKey key = new FieldAnnotationKey(field, annotation);
		if(!fieldAnnotationCache.containsKey(key)) {
			final Boolean result = new Boolean(field.isAnnotationPresent(annotation));
			synchronized(this) {
				fieldAnnotationCache.put(key, result);
			}
			return result;
		} else {
			return fieldAnnotationCache.get(key);
		}
	}
	
	
	@PostConstruct
	public void init() {
		
	}
	
	public void doServiceGet(final BaseIdentity object, final String languageId) {
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object, false);
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
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object, false);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				final Field field = target.getField();
				//field.setAccessible(true);
				//final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
				final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(target.getEntity().getId(), getReferenceType(target.getEntity(), field));
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
	
	private String getReferenceType(final BaseIdentity entity, final Field field) {
		final String referenceType = String.format("%s.%s", HibernateProxyHelper.getClassWithoutInitializingProxy(
                entity).getSimpleName(), field.getName());
		return referenceType;
	}

	public void doSaveUpdate(final KeyEntity object) {
		LOG.debug("Start doSaveUpdate method");
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object, false);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				doCRUDLogicOnField(target.getField(), (KeyEntity)target.getEntity());
			}
		}
	}
	

	
	public void doDelete(final KeyEntity object) {
		LOG.debug("Start doDelete method");
		final Set<TargetInternationalizedField> fieldList = getTargetFields(object, true);
		if(CollectionUtils.isNotEmpty(fieldList)) {
			for(final TargetInternationalizedField target : fieldList) {
				final Field field = target.getField();
				//field.setAccessible(true);
				//final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
				StringBuilder sb = new StringBuilder();
				sb.append("ThreadId=").append(Thread.currentThread().getId());
				sb.append("; REFERENCE_ID=").append(target.getEntity().getId());
				sb.append("; REFERENCE_TYPE=").append(getReferenceType(target.getEntity(), field));
				sb.append("; Entity={").append((target != null)?(target.getEntity() != null)?target.getEntity().toString():"null":"null").append("}");
				LOG.debug(sb.toString());
				final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(target.getEntity().getId(), getReferenceType(target.getEntity(), field));
				if(CollectionUtils.isNotEmpty(dbList)) {
					for(final LanguageMappingEntity entity : dbList) {
						sb = new StringBuilder();
						sb.append("ThreadId=").append(Thread.currentThread().getId());
						sb.append("; LanguageMappingEntity=").append((entity != null)?entity.toString():"null");
						LOG.debug("ThreadId=" + Thread.currentThread().getId() + "LanguageMappingEntity : " + entity.toString());
						languageDAO.delete(entity);
					}
				}
			}
		}
	}
	
	private Set<TargetInternationalizedField> getTargetFields(final BaseIdentity object, final boolean isDelete) {
		if(object != null) {
			return getTargetFields(object, new HashSet<VisitedField>(), isDelete);
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
	
	private boolean isCascadeDeletePresent(final Field field) {
		CascadeType[] cascadeTypes = null;
		if(field.getAnnotation(ManyToMany.class) != null) {
			cascadeTypes = field.getAnnotation(ManyToMany.class).cascade();
		} else if(field.getAnnotation(OneToOne.class) != null) {
			cascadeTypes = field.getAnnotation(OneToOne.class).cascade();
		} else if(field.getAnnotation(ManyToOne.class) != null) {
			cascadeTypes = field.getAnnotation(ManyToOne.class).cascade();
		} else if(field.getAnnotation(OneToMany.class) != null) {
			cascadeTypes = field.getAnnotation(OneToMany.class).cascade();
		}
		
		boolean retVal = false;
		if(cascadeTypes != null) {
			for(final CascadeType type : cascadeTypes) {
				if(type.equals(CascadeType.ALL) || type.equals(CascadeType.REMOVE)) {
					retVal = true;
				}
			}
		}
		
		return retVal;
	}
	
	private Set<TargetInternationalizedField> getTargetFields(final BaseIdentity entity, final Set<VisitedField> visitedSet, final boolean isDelete) {
		final Set<TargetInternationalizedField> retVal = new HashSet<>();
		
		/* 
		 * you can't just get the class, because an incoming object can be a javassist/cglib object, modified by Hibernate.
		 * These objects won't have the expected annotations.  Therefore, it is necessary to un-proxy the Class Object.
		 * If the object is not of type HibernateProxy, the regular classname will be used.  
		 * See @HibernateProxyHelper.getClassWithoutInitializingProxy
		 */
		final Class<?> clazz = HibernateProxyHelper.getClassWithoutInitializingProxy(entity);
		
		if(isAnnotationPresent(clazz, Internationalized.class)) {
			final List<Field> resultList = getDeclaredFields(clazz);
			if(resultList != null) {
				for(final Field field : resultList) {
					final VisitedField visitedField = new VisitedField(clazz, field, entity);
					if(!visitedSet.contains(visitedField)) {
						visitedSet.add(visitedField);
						if(isAnnotationPresent(field, InternationalizedCollection.class)) {
							if(StringUtils.isNotBlank(entity.getId())) {
								retVal.add(new TargetInternationalizedField(field, entity));
							}
						} else if(isAnnotationPresent(field, Internationalized.class)) {
							
							//If this is a delete operation, but there is no delete cascade, ignore the field
							if(isDelete && !isCascadeDeletePresent(field)) {
								continue;
							}
							final Object fieldObject = getMethodCallResult(field, entity);
							if(fieldObject != null) {
								if(fieldObject instanceof Collection) {
									for(final Object o : (Collection)fieldObject) {
										if(o instanceof BaseIdentity) {
											retVal.addAll(getTargetFields((BaseIdentity)o, visitedSet, isDelete));
										}
									}
								} else if(fieldObject instanceof BaseIdentity) {
									retVal.addAll(getTargetFields((BaseIdentity)fieldObject, visitedSet, isDelete));
								} else if(fieldObject instanceof Map) {
									for(final Object key : ((Map)fieldObject).keySet()) {
										if(key != null) {
											if(key instanceof BaseIdentity) {
												retVal.addAll(getTargetFields((BaseIdentity)key, visitedSet, isDelete));
											}
											final Object value = ((Map)fieldObject).get(key);
											if(value != null) {
												retVal.addAll(getTargetFields((BaseIdentity)value, visitedSet, isDelete));
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

		//final InternationalizedCollection metadata = field.getAnnotation(InternationalizedCollection.class);
		if(StringUtils.isNotBlank(object.getId())) {
			Collection<LanguageMappingEntity> toDelete = new LinkedList<>();
			Collection<LanguageMappingEntity> toUpdate = new LinkedList<>();
			Collection<LanguageMappingEntity> toSave = new LinkedList<>();
			//final Object fieldObject = ReflectionUtils.getField(field, object);
			final Object fieldObject = getMethodCallResult(field, object);
			if(fieldObject == null) {
				return;
			}
			final Map<String, LanguageMappingEntity> transientMap = (Map<String, LanguageMappingEntity>)fieldObject;
			StringBuilder sb = new StringBuilder();
			sb.append("ThreadId=").append(Thread.currentThread().getId());
			sb.append("; REFERENCE_ID=").append(object.getId());
			sb.append("; REFERENCE_TYPE=").append(getReferenceType(object, field));
			LOG.debug(sb.toString());
			final List<LanguageMappingEntity> dbList = languageDAO.getByReferenceIdAndType(object.getId(), getReferenceType(object, field));
			if(MapUtils.isEmpty(transientMap)) {
				toDelete = dbList;
			} else {
				/* all new */
				if(CollectionUtils.isEmpty(dbList)) {
					for(final String languageId : transientMap.keySet()) {
						final LanguageMappingEntity entity = transientMap.get(languageId);
						setMetadata(entity, object, languageId, field);
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
							setMetadata(transientEntity, object, languageId, field);
							if(StringUtils.isNotBlank(transientEntity.getValue())) {
								toSave.add(transientEntity);
							}
						}
					}
				}
				
			}


			if(CollectionUtils.isNotEmpty(toSave)) {
				for(final LanguageMappingEntity entity : toSave) {
					sb = new StringBuilder();
					sb.append("ThreadId=").append(Thread.currentThread().getId());
					sb.append(" : START SAVE LANGUAGE_MAPPING : ");
					sb.append("; entity=").append((entity != null)?entity.toString():"null");
					LOG.debug(sb.toString());
					languageDAO.save(entity);
				}
			}
			if(CollectionUtils.isNotEmpty(toUpdate)) {
				for(final LanguageMappingEntity entity : toUpdate) {
					sb = new StringBuilder();
					sb.append("ThreadId=").append(Thread.currentThread().getId());
					sb.append(" : START UPDATE LANGUAGE_MAPPING : ");
					sb.append("; entity=").append((entity != null) ? entity.toString() : "null");
					LOG.debug(sb.toString());
					languageDAO.update(entity);
				}
			}
			if(CollectionUtils.isNotEmpty(toDelete)) {
				for(final LanguageMappingEntity entity : toDelete) {
					sb = new StringBuilder();
					sb.append("ThreadId=").append(Thread.currentThread().getId());
					sb.append(" : START DELETE LANGUAGE_MAPPING : ");
					sb.append("; entity=").append((entity != null)?entity.toString():"null");
					LOG.debug(sb.toString());
					languageDAO.delete(entity);
				}
			}
		}
	}
	
	private void setMetadata(final LanguageMappingEntity mappingEntity, final KeyEntity entity, final String languageId, final Field field) {
		mappingEntity.setLanguageId(languageId);
		mappingEntity.setReferenceId(entity.getId());
		mappingEntity.setReferenceType(getReferenceType(entity, field));
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
