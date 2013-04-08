package org.openiam.am.srvc.dozer.converter;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.LinkedList;
import java.util.List;

// TODO: need to be refactored - Create only one abstarct class for all projects
public abstract class AbstractDozerEntityConverter <DTO, Entity> {

    @Autowired
    @Qualifier("amDto2entityDeepDozerMapper")
    protected Mapper dto2entityDeepDozerMapper;

    @Autowired
    @Qualifier("amDto2entityShallowDozerMapper")
    protected Mapper dto2entityShallowDozerMapper;

    @Autowired
    @Qualifier("amDeepDozerMapper")
    protected Mapper deepDozerMapper;

    @Autowired
    @Qualifier("amShallowDozerMapper")
    protected Mapper shallowDozerMapper;

    public abstract Entity convertEntity (final Entity entity, final boolean isDeep);
    public abstract DTO convertDTO(final DTO entity, final boolean isDeep);
    public abstract Entity convertToEntity(final DTO entity, final boolean isDeep);
    public abstract DTO convertToDTO(final Entity entity, final boolean isDeep);
    public abstract List<Entity> convertToEntityList(final List<DTO> list, final boolean isDeep);
    public abstract List<DTO> convertToDTOList(final List<Entity> list, final boolean isDeep);

    public <T> T convert(final Object entity, final boolean isDeep, final Class<T> clazz) {
        final Mapper mapper = (isDeep) ? deepDozerMapper : shallowDozerMapper;
        return mapper.map(entity, clazz);
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
}
