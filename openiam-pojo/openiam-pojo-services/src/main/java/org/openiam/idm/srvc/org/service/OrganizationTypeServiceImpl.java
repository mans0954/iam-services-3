package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class OrganizationTypeServiceImpl implements OrganizationTypeService {

	@Autowired
	private OrganizationTypeDAO organizationTypeDAO;

    @Autowired
    private UserDataService userDataService;

    @Value("${org.openiam.delegation.filter.organization}")
    private String organizationTypeId;

	@Override
	public OrganizationTypeEntity findById(String id) {
		return organizationTypeDAO.findById(id);
	}
	
	@Override
	public OrganizationTypeEntity findByName(String name) {
		final OrganizationTypeEntity entity = new OrganizationTypeEntity();
		entity.setName(name);
		final List<OrganizationTypeEntity> entityList = organizationTypeDAO.getByExample(entity);
		return (CollectionUtils.isNotEmpty(entityList)) ? entityList.get(0) : null;
	}

	@Override
	public List<OrganizationTypeEntity> findBeans(final OrganizationTypeSearchBean searchBean, String requesterId, int from, int size) {
        setExcludeOrgType(searchBean, requesterId);

        return organizationTypeDAO.getByExample(searchBean, from, size);
	}

    @Override
	public int count(final OrganizationTypeSearchBean searchBean, String requesterId) {
        setExcludeOrgType(searchBean, requesterId);
		return organizationTypeDAO.count(searchBean);
	}

	@Override
	public void save(OrganizationTypeEntity type) {
		if(type != null) {
			if(StringUtils.isNotBlank(type.getId())) {
				final OrganizationTypeEntity entity = organizationTypeDAO.findById(type.getId());
				if(entity != null) {
					type.setChildTypes(entity.getChildTypes());
					type.setParentTypes(entity.getParentTypes());
					type.setOrganizations(entity.getOrganizations());
					organizationTypeDAO.merge(type);
				}
			} else {
				type.setChildTypes(null);
				type.setParentTypes(null);
				type.setOrganizations(null);
				organizationTypeDAO.save(type);
			}
		}
	}

	@Override
	public void delete(String id) {
		if(StringUtils.isNotBlank(id)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				organizationTypeDAO.delete(entity);
			}
		}
	}

	@Override
	public void addChild(String id, String childId) {
		if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(childId)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				final OrganizationTypeEntity child = organizationTypeDAO.findById(childId);
				if(child != null) {
					entity.addChildType(child);
					organizationTypeDAO.update(entity);
				}
			}
		}
	}

	@Override
	public void removeChild(String id, String childId) {
		if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(childId)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				entity.removeChildType(childId);
				organizationTypeDAO.update(entity);
			}
		}
	}

	@Override
	public void validateOrgType2OrgTypeAddition(String parentId, String memberId)
			throws BasicDataServiceException {
		final OrganizationTypeEntity parent = organizationTypeDAO.findById(parentId);
		final OrganizationTypeEntity child = organizationTypeDAO.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<OrganizationTypeEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(parent.hasChildType(child.getId())) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		
		/*
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
		*/
	}
	
	private boolean causesCircularDependency(final OrganizationTypeEntity parent, final OrganizationTypeEntity child, final Set<OrganizationTypeEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentTypes())) {
					for(final OrganizationTypeEntity entity : parent.getParentTypes()) {
						retval = entity.getId().equals(child.getId());
						if(retval) {
							break;
						}
						causesCircularDependency(parent, entity, visitedSet);
					}
				}
			}
		}
		return retval;
	}

    private void setExcludeOrgType(OrganizationTypeSearchBean searchBean, String requesterId) {
        if(StringUtils.isNotBlank(requesterId)){
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            if(DelegationFilterHelper.isOrgFilterSet(requesterAttributes)){
                searchBean.addExcludeId(organizationTypeId);
            }
        }
    }
}
