package org.openiam.idm.srvc.grp.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Data access object interface for GroupAttribute.
 *
 * @author Suneet Shah
 */
public interface GroupAttributeDAO extends BaseDao<GroupAttributeEntity, String> {
    public List<GroupAttributeEntity> findGroupAttributes(String groupId, final Set<String> metadataElementIds);
    //public void deleteByGroupId(final String groupId);

    public List<GroupAttributeEntity> findGroupAttributes(String groupId);

    public List<Map<String, String>> getAttributeByGroupIds(List<String> groupIds, String attrName);

    public String getAttributeByGroupId(String groupId, String attrName);

    public List<GroupAttributeEntity> getAttributeEntityByGroupIds(List<String> groupIds, String attrName);
}
