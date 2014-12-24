package org.openiam.idm.srvc.loc.service;

// Generated May 9, 2009 1:42:34 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.loc.domain.LocationEntity;
import org.springframework.stereotype.Repository;


import static org.hibernate.criterion.Example.create;

/**
 * DAO to manage the list of Locations
 * @see org.openiam.idm.srvc.loc.dto.Location
 * @author Suneet shah
 */
@Repository("locationDAO")
public class LocationDAOImpl extends BaseDaoImpl<LocationEntity, String> implements LocationDAO {

    private static final Log log = LogFactory.getLog(LocationDAOImpl.class);

    private String DELETE_BY_ORGANIZATION_ID = "DELETE FROM %s e WHERE e.organizationId = :organizationId";


    @PostConstruct
    public void initSQL() {
        DELETE_BY_ORGANIZATION_ID = String.format(DELETE_BY_ORGANIZATION_ID, domainClass.getSimpleName());
    }

    @Override
    protected Criteria getExampleCriteria(LocationEntity location){
        final Criteria criteria = getCriteria();
        if (StringUtils.isNotBlank(location.getLocationId())) {
            criteria.add(Restrictions.eq(getPKfieldName(), location.getLocationId()));
        } else {

            if (location.getOrganizationId() != null) {
                if (StringUtils.isNotBlank(location.getOrganizationId())) {
                    criteria.add(Restrictions.eq("organizationId", location.getOrganizationId()));
                }
            }

            if (location.getCountry() != null) {
                if (StringUtils.isNotBlank(location.getCountry())) {
                    criteria.add(Restrictions.eq("country", location.getCountry()));
                }
            }

            if (location.getCity() != null) {
                if (StringUtils.isNotBlank(location.getCity())) {
                    criteria.add(Restrictions.eq("city", location.getCity()));
                }
            }
        }
        return criteria;
    }

    @Override
    public void removeByOrganizationId(final String userId) {
        final Query qry = getSession().createQuery(DELETE_BY_ORGANIZATION_ID);
        qry.setString("locationId", userId);
        qry.executeUpdate();
    }

    @Override
    protected String getPKfieldName() {
        return "locationId";
    }

}
