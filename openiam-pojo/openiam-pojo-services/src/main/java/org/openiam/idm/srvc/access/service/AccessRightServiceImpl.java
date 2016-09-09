package org.openiam.idm.srvc.access.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.AccessRightDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessRightServiceImpl implements AccessRightService {
	
	@Autowired
	private AccessRightDAO dao;
	@Autowired
	private AccessRightDozerConverter converter;

	@Override
	@Transactional
	public String save(AccessRight dto) throws BasicDataServiceException {
		if(dto == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "AccessRight is not passed as argument");
		}
		final AccessRightEntity entity = converter.convertToEntity(dto, true);
		if(entity.getId() != null) {
			dao.merge(entity);
		} else {
			dao.save(entity);
		}
		return entity.getId();
	}

	@Override
	@Transactional
	public void delete(String id) throws BasicDataServiceException {
		if(StringUtils.isBlank(id)) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "AccessRight ID is not passed as argument");
		}
		final AccessRightEntity entity = dao.findById(id);
		if(entity != null) {
			dao.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public AccessRight get(String id) {
		return converter.convertToDTO(dao.findById(id), true);
	}

	@Override
	@Transactional(readOnly=true)
	@LocalizedServiceGet
	public List<AccessRight> findBeans(AccessRightSearchBean sb, int from, int size, final Language language) {
		return converter.convertToDTOList(dao.getByExample(sb, from, size), true);
	}

	@Override
	@Transactional(readOnly=true)
	public int count(final AccessRightSearchBean searchBean) {
		return dao.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccessRight> findByIds(Collection<String> ids) {
		return converter.convertToDTOList(dao.findByIds(ids), true);
	}
}
