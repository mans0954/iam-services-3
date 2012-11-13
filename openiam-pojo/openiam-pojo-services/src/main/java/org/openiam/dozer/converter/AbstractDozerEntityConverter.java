package org.openiam.dozer.converter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
}
