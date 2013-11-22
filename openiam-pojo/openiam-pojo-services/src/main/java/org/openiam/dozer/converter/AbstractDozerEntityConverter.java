package org.openiam.dozer.converter;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractDozerEntityConverter<DTO, Entity> {

	@Autowired
	@Qualifier("dto2entityDeepDozerMapper")
	protected Mapper dto2entityDeepDozerMapper;
	
	@Autowired
	@Qualifier("dto2entityShallowDozerMapper")
	protected Mapper dto2entityShallowDozerMapper;
	
	@Autowired
	@Qualifier("deepDozerMapper")
	protected Mapper deepDozerMapper;
	
	@Autowired
	@Qualifier("shallowDozerMapper")
	protected Mapper shallowDozerMapper;
	
	public abstract Entity convertEntity (final Entity entity, final boolean isDeep);
	public abstract DTO convertDTO(final DTO entity, final boolean isDeep);
	public abstract Entity convertToEntity(final DTO entity, final boolean isDeep);
	public abstract DTO convertToDTO(final Entity entity, final boolean isDeep);
	public abstract List<Entity> convertToEntityList(final List<DTO> list, final boolean isDeep);
	public abstract List<DTO> convertToDTOList(final List<Entity> list, final boolean isDeep);
    public abstract Set<Entity> convertToEntitySet(final Set<DTO> set, final boolean isDeep);
    public abstract Set<DTO> convertToDTOSet(final Set<Entity> set, final boolean isDeep);
	
	public <T> T convert(final Object entity, final boolean isDeep, final Class<T> clazz) {
		final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
		return (entity != null) ? mapper.map(entity, clazz) : null;
	}
	
	public <T> T convertToCrossEntity(final Object object, final boolean isDeep, final Class<T> clazz) {
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		return (object != null) ? mapper.map(object, clazz) : null;
	}
	
	public <T> List<T> convertListToCrossEntity(final List fromList, final boolean isDeep, final Class<T> clazz) {
		final List<T> retVal = new LinkedList<T>();
		final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
		if(CollectionUtils.isNotEmpty(fromList)) {
			for(final Object from : fromList) {
				retVal.add(mapper.map(from, clazz));
			}
		}
		return retVal;
	}

    public <T> Set<T> convertSetToCrossEntity(final Set fromSet, final boolean isDeep, final Class<T> clazz) {
        final Set<T> retVal = new HashSet<T>();
        final Mapper mapper = (isDeep) ? dto2entityDeepDozerMapper : dto2entityShallowDozerMapper;
        if(CollectionUtils.isNotEmpty(fromSet)) {
            for(final Object from : fromSet) {
                retVal.add(mapper.map(from, clazz));
            }
        }
        return retVal;
    }
}
