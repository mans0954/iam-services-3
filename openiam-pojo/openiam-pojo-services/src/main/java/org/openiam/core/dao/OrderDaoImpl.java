package org.openiam.core.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.BaseIdentity;
import org.openiam.base.ws.SortParam;
import org.openiam.idm.searchbeans.AbstractLanguageSearchBean;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.internationalization.InternationalizationProvider;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class OrderDaoImpl<T extends Serializable, PrimaryKey extends Serializable> extends BaseDaoImpl<T, PrimaryKey> {

    protected void setOderByCriteria(Criteria criteria, AbstractSearchBean sb) {
        List<SortParam> sortParamList = sb.getSortBy();
        for (SortParam sort : sortParamList) {
            if ("displayName".equals(sort.getSortBy())) {
                if (sb instanceof AbstractLanguageSearchBean) {
                    AbstractLanguageSearchBean lsb = (AbstractLanguageSearchBean) sb;
                    criteria.createAlias("languageMappings", "lm", Criteria.LEFT_JOIN);

                    if (StringUtils.isNotBlank(lsb.getLanguageId()))
                        criteria.add(Restrictions.eq("lm.languageId", lsb.getLanguageId()));
                    else
                        //by default lang english
                        criteria.add(Restrictions.eq("lm.languageId", "1"));

                    if (StringUtils.isNotBlank(lsb.getReferenceType()))
                        criteria.add(Restrictions.eq("lm.referenceType", lsb.getReferenceType()));
                    else
                        criteria.add(Restrictions.eq("lm.referenceType", getReferenceType()));

                    criteria.addOrder(createOrder("lm.value", sort.getOrderBy()));
                }
            } else if ("description".equals(sort.getSortBy()) || "name".equals(sort.getSortBy())) {
            	/*
            	 * Lev Bornovalov - no field 'description' on metadata type - removing
            	 * 
                if (sb instanceof MetadataElementSearchBean) {
                    criteria.createAlias("metadataType", "mt", Criteria.LEFT_JOIN);
                    criteria.addOrder(createOrder("mt.description", sort.getOrderBy()));
                }
                */
            } else {
                criteria.addOrder(createOrder(sort.getSortBy(), sort.getOrderBy()));
            }
        }
    }

    protected String getReferenceType() {
        return null;
    }
}
