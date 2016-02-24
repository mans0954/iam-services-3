package org.openiam.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by alexander on 04/01/16.
 */
@Service("groupEntityValidator")
public class GroupEntityValidator extends AbstractEntityValidator {
    @Autowired
    private GroupDAO groupDao;


    @Override
    public <T> boolean isValid(T entity) throws BasicDataServiceException {
        if (entity == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        GroupEntity group = (GroupEntity)entity;

        if (StringUtils.isBlank(group.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME);
        }

        //final GroupEntity found = groupManager.getGroupByName(group.getName(), null);
        if(log.isDebugEnabled()) {
        	log.debug("Validating group " + group.getName() + " of managed system " + group.getManagedSystem().getId());
        }
        //final GroupEntity found = groupManager.getGroupByNameAndManagedSys(group.getName(), group.getManagedSysId(), null);
        GroupSearchBean groupSearchBean = new GroupSearchBean();
        groupSearchBean.setName(group.getName());
        groupSearchBean.setManagedSysId(group.getManagedSystem().getId());
        final List<GroupEntity> foundList = groupDao.getByExample(groupSearchBean, 0, 1);
        final GroupEntity found = (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;

        if (found != null) {
            if ( ( !found.getId().equals(group.getId()))) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN, "Group name is already in use");
            }
        }

        return super.isValid(entity);
    }
}
