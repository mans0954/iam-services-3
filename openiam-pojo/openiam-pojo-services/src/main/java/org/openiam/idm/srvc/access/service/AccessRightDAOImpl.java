package org.openiam.idm.srvc.access.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AccessRightDAOImpl extends BaseDaoImpl<AccessRightEntity, String> implements AccessRightDAO {

    private static final Log log = LogFactory.getLog(AccessRightDAOImpl.class);

    @Override
    protected Criteria getExampleCriteria(SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof AccessRightSearchBean) {
            final AccessRightSearchBean sb = (AccessRightSearchBean) searchBean;
            if (StringUtils.isNotBlank(sb.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
            } else {
                String name = sb.getName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                    name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("name", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("name", name));
                    }
                }
                if (StringUtils.isBlank(sb.getMetadataTypeId1()) && StringUtils.isNotBlank(sb.getMetadataTypeId2())) {
                    criteria.add(Restrictions.or(Restrictions.eq("metadataTypeEntity2.id", sb.getMetadataTypeId2()), Restrictions.eq("metadataTypeEntity1.id", sb.getMetadataTypeId2())));
                } else if (StringUtils.isBlank(sb.getMetadataTypeId2()) && StringUtils.isNotBlank(sb.getMetadataTypeId1())) {
                    criteria.add(Restrictions.or(Restrictions.eq("metadataTypeEntity2.id", sb.getMetadataTypeId1()), Restrictions.eq("metadataTypeEntity1.id", sb.getMetadataTypeId1())));
                } else if (StringUtils.isNotBlank(sb.getMetadataTypeId1()) && StringUtils.isNotBlank(sb.getMetadataTypeId2())) {
                    criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("metadataTypeEntity2.id", sb.getMetadataTypeId2()), Restrictions.eq("metadataTypeEntity1.id", sb.getMetadataTypeId1())), Restrictions.and(Restrictions.eq("metadataTypeEntity2.id", sb.getMetadataTypeId1()), Restrictions.eq("metadataTypeEntity1.id", sb.getMetadataTypeId2()))));
                }
            }
        }
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
