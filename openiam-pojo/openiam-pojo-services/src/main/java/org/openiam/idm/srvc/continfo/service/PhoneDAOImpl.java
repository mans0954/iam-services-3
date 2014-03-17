package org.openiam.idm.srvc.continfo.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;


@Repository("phoneDAO")
public class PhoneDAOImpl extends BaseDaoImpl<PhoneEntity, String> implements PhoneDAO {

	private static final Log log = LogFactory.getLog(PhoneDAOImpl.class);
	
	private String DELETE_BY_USER_ID = "DELETE FROM %s e WHERE e.parent.id = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
	}

    @Override
    protected Criteria getExampleCriteria(PhoneEntity phone){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(phone.getPhoneId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), phone.getPhoneId()));
        } else {

            if (phone.getParent() != null) {
                if (StringUtils.isNotBlank(phone.getParent().getId())) {
                    criteria.add(Restrictions.eq("parent.id", phone.getParent().getId()));
                }
            }

            if (phone.getMetadataType() != null) {
                if (StringUtils.isNotBlank(phone.getMetadataType().getId())) {
                    criteria.add(Restrictions.eq("metadataType.id", phone.getMetadataType().getId()));
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
		return "phoneId";
	}
	
}
