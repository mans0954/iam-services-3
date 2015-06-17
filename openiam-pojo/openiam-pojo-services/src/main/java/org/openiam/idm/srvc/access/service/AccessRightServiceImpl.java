package org.openiam.idm.srvc.access.service;

import java.util.Collection;
import java.util.List;

import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessRightServiceImpl implements AccessRightService {
	
	@Autowired
	private AccessRightDAO dao;

	@Override
	@Transactional
	public void save(AccessRightEntity entity) {
		if(entity.getId() != null) {
			dao.merge(entity);
		} else {
			dao.save(entity);
		}
	}

	@Override
	@Transactional
	public void delete(String id) {
		final AccessRightEntity entity = get(id);
		if(entity != null) {
			dao.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public AccessRightEntity get(String id) {
		return dao.findById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccessRightEntity> findBeans(AccessRightSearchBean sb,
			int from, int size) {
		return dao.getByExample(sb, from, size);
	}

	@Override
	@Transactional(readOnly=true)
	public int count(final AccessRightSearchBean searchBean) {
		return dao.count(searchBean);
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccessRightEntity> findByIds(Collection<String> ids) {
		return dao.findByIds(ids);
	}

}
