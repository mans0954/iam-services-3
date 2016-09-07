package org.openiam.idm.srvc.mngsys.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MngSysPolicySearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.domain.MngSysPolicyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

@Repository("mngSysPolicyDAO")
public class MngSysPolicyDAOImpl extends BaseDaoImpl<MngSysPolicyEntity, String> implements MngSysPolicyDAO {

    @Override
    protected String getPKfieldName() {
        return "id";
    }

    @Override
    public List<MngSysPolicyEntity> findByMngSysId(String mngSysId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .addOrder(Order.asc(getPKfieldName()));
        return criteria.list();
    }

    @Override
    public List<MngSysPolicyEntity> findByMngSysIdAndType(String mngSysId, String metadataTypeId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .add(Restrictions.eq("type.id",metadataTypeId))
                .addOrder(Order.asc(getPKfieldName()));
        return criteria.list();
    }

    @Override
    public MngSysPolicyEntity findPrimaryByMngSysIdAndType(String mngSysId, String metadataTypeId) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("managedSystem.id", mngSysId))
                .add(Restrictions.eq("type.id",metadataTypeId))
                .add(Restrictions.eq("primary",true))
                .addOrder(Order.asc(getPKfieldName()));
        return (MngSysPolicyEntity)criteria.uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(SearchBean sb) {
        final Criteria criteria = super.getCriteria();
        if(sb != null) {
            if(sb instanceof MngSysPolicySearchBean) {
                final MngSysPolicySearchBean searchBean = (MngSysPolicySearchBean)sb;
                if(StringUtils.isNotBlank(searchBean.getKey())) {
                    criteria.add(Restrictions.idEq(searchBean.getKey()));
                }
                final Criterion nameCriterion = getStringCriterion("name", searchBean.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
                if(nameCriterion != null) {
                	criteria.add(nameCriterion);
                }
                if(StringUtils.isNotBlank(searchBean.getManagedSysId())) {
                    criteria.add(Restrictions.eq("managedSystem.id", searchBean.getManagedSysId()));
                }
                if(StringUtils.isNotBlank(searchBean.getMetadataTypeId())) {
                    criteria.add(Restrictions.eq("type.id", searchBean.getMetadataTypeId()));
                }
            }
        }
        return criteria;
    }
}
