package org.openiam.idm.srvc.continfo.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;

@Repository("addressDAO")
public class AddressDAOImpl extends BaseDaoImpl<AddressEntity, String> implements AddressDAO {


	private static final Log log = LogFactory.getLog(AddressDAOImpl.class);
	
	private String DELETE_BY_USER_ID = "DELETE FROM %s e WHERE e.parent.id = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
	}

    @Override
    protected Criteria getExampleCriteria(AddressEntity address){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(address.getId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), address.getId()));
        } else {

            if (address.getParent() != null) {
                if (StringUtils.isNotBlank(address.getParent().getId())) {
                    criteria.add(Restrictions.eq("parent.id", address.getParent().getId()));
                }
            }

            if (address.getType() != null) {
                if (StringUtils.isNotBlank(address.getType().getId())) {
                    criteria.add(Restrictions.eq("metadataType.id", address.getType().getId()));
                }
            }
        }
        return criteria;
    }
	
    @Override
    public void removeByUserId(final String userId) {
    	final Query qry = getSession().createQuery(DELETE_BY_USER_ID);
		qry.setString("userId", userId);
		qry.executeUpdate();
	}

	@Override
	protected String getPKfieldName() {
		return "addressId";
	}
	
}
