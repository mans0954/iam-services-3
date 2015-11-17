package org.openiam.idm.srvc.auth.login;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.openiam.base.Tuple;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.searchbean.converter.IdentitySearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("identityDAO")
public class IdentityDAOImpl extends BaseDaoImpl<IdentityEntity, String> implements IdentityDAO {

    @Autowired
    private IdentitySearchBeanConverter identitySearchBeanConverter;

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<IdentityEntity> findByReferredId(String referredId) {
        Criteria criteria = getCriteria().add(Restrictions.eq("referredObjectId",referredId));
        return criteria.list();
    }

    @Override
    public List<IdentityEntity> findByType(IdentityTypeEnum type) {
        Criteria criteria = getCriteria().add(Restrictions.eq("type",type));
        return criteria.list();
    }

    @Override
    public IdentityEntity findByManagedSysId(String referredId, String managedSysId) {
        Criteria criteria = getCriteria().add(Restrictions.and(Restrictions.eq("managedSysId",managedSysId),Restrictions.eq("referredObjectId",referredId)));
        return (IdentityEntity)criteria.uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean, boolean isCount) {
        Criteria criteria = getCriteria();
        if(searchBean != null && searchBean instanceof IdentitySearchBean) {
            final IdentitySearchBean identitySearchBean = (IdentitySearchBean)searchBean;

            final IdentityEntity exampleEnity = identitySearchBeanConverter.convert(identitySearchBean);
            criteria = this.getExampleCriteria(exampleEnity);
        }

        return criteria;
    }

    @Override
    public IdentityEntity getByIdentityManagedSys(String principal, String managedSysId) {
        return (IdentityEntity) getCriteria()
                .add(Restrictions.eq("identity", principal))
                .add(Restrictions.eq("managedSysId", managedSysId)).uniqueResult();
    }
}
