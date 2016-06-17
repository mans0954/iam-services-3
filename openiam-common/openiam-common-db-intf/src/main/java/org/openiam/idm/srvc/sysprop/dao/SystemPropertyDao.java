package org.openiam.idm.srvc.sysprop.dao;

import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.sysprop.domain.SystemPropertyEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.SearchAttribute;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface SystemPropertyDao extends BaseDao<SystemPropertyEntity, String> {

    public List<SystemPropertyEntity> getByMetadataType(String mdTypeId);

    public List<SystemPropertyEntity> getByName(String name);

}
