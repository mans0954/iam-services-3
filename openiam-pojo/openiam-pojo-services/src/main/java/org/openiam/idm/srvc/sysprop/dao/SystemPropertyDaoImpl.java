package org.openiam.idm.srvc.sysprop.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.openiam.base.OrderConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.SearchParam;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.sysprop.domain.SystemPropertyEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.SearchAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.hibernate.criterion.Projections.rowCount;

@Repository("systemPropertyDao")
public class SystemPropertyDaoImpl extends BaseDaoImpl<SystemPropertyEntity, String> implements SystemPropertyDao {

    @Override
    protected String getPKfieldName() {
        return "name";
    }

    protected boolean cachable() {
        return false;
    }

    @Override
    public List<SystemPropertyEntity> getByMetadataType(String mdTypeId) {
        if (mdTypeId == null) {
            throw new NullPointerException("Metadata type id is null");
        }
        final Criteria criteria = getCriteria();
        return (List<SystemPropertyEntity>) criteria.add(Restrictions.eq("type.id", mdTypeId)).list();
    }

    @Override
    public List<SystemPropertyEntity> getByName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        final Criteria criteria = getCriteria();
        criteria.add(Restrictions.ilike("name", name, MatchMode.START));
        criteria.setFirstResult(0);
        criteria.setMaxResults(Integer.MAX_VALUE);
        criteria.setCacheable(this.cachable());
        return (List<SystemPropertyEntity>) criteria.list();
    }
}
