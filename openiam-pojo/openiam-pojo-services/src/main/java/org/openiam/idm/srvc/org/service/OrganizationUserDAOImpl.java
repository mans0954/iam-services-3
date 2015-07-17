package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.openiam.base.Tuple;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.org.domain.*;
import org.openiam.idm.srvc.searchbean.converter.OrganizationSearchBeanConverter;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Data access object implementation for OrganizationEntity.
 */
@Repository("organizationUserDAO")
public class OrganizationUserDAOImpl extends
        BaseDaoImpl<OrganizationUserEntity, OrganizationUserIdEntity> implements OrganizationUserDAO {


    @Override
    protected String getPKfieldName() {
        return "primaryKey";
    }

    @Override
    public List<OrganizationUserEntity> findByOrganizationId(String organizationId) {
        return this.getCriteria().add(
                Restrictions.eq("primaryKey.organization.id", organizationId)).list();
    }

    @Override
    public List<OrganizationUserEntity> findByUserId(String userId) {
        return this.getCriteria().add(
                Restrictions.eq("primaryKey.user.id", userId)).list();
    }

    @Override
    public OrganizationUserEntity find(String userId, String orgId) {
        List<OrganizationUserEntity> entities = this.getCriteria().add(
                Restrictions.and(Restrictions.eq("primaryKey.user.id", userId),
                        Restrictions.eq("primaryKey.organization.id", orgId))).list();
        if (entities != null) {
            return entities.get(0);
        }
        return null;
    }
}
