package org.openiam.idm.srvc.prov.request.service;

// Generated Jan 9, 2009 5:33:58 PM by Hibernate Tools 3.2.2.GA

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;
import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.prov.request.domain.ProvisionRequestEntity;
import org.openiam.idm.srvc.prov.request.domain.RequestApproverEntity;
import org.openiam.idm.srvc.prov.request.dto.ProvisionRequest;
import org.openiam.idm.srvc.prov.request.dto.RequestApprover;
import org.openiam.idm.srvc.prov.request.dto.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository("requestDAO")
public class ProvisionRequestDAOImpl extends BaseDaoImpl<ProvisionRequestEntity, String> implements ProvisionRequestDAO {

	private static final Log log = LogFactory.getLog(ProvisionRequestDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(ProvisionRequestEntity entity) {
		final Criteria criteria = getCriteria();
		if(CollectionUtils.isNotEmpty(entity.getRequestApprovers())) {
			final Set<String> approverIds = new HashSet<String>();
			for(final RequestApproverEntity approver : entity.getRequestApprovers()) {
				approverIds.add(approver.getApproverId());
			}
			
			criteria.createAlias("requestApprovers", "approver").add(
					Restrictions.in("approver.approverId", approverIds));
		}
		
		if(StringUtils.isNotBlank(entity.getStatus())) {
			criteria.add(Restrictions.eq("status", entity.getStatus()));
		}
		return criteria;
	}



	public List<ProvisionRequestEntity> findRequestByApprover(String approverId, String status) {
		final ProvisionRequestEntity example = new ProvisionRequestEntity();
		final RequestApproverEntity approver = new RequestApproverEntity();
		approver.setApproverId(approverId);
		example.addRequestApprover(approver);
		example.setStatus(status);
		return getByExample(example);	
	}

	/*
	public List<ProvisionRequestEntity> search(SearchRequest search) {
		Session session = getSession();
		Criteria crit = session.createCriteria(org.openiam.idm.srvc.prov.request.dto.ProvisionRequest.class);
		crit.createAlias("requestApprovers", "requestApprovers");
		crit.setMaxResults(maxResultSetSize);

        List<String> roleIdList = search.getRoleIdList();
        if (roleIdList == null) {
            roleIdList = new ArrayList<String>();
        }


		if (search.getEndDate() != null  ) {

			crit.add(Restrictions.le("statusDate",search.getEndDate()));
		}
		if (search.getStartDate() != null  ) {

			crit.add(Restrictions.ge("statusDate",search.getStartDate()));
		}
		if (search.getRequestId() != null && search.getRequestId().length() > 0  ) {

			crit.add(Restrictions.eq("requestId",search.getRequestId()));
		}
		if (search.getRequestorId() != null && search.getRequestorId().length() > 0  ) {

			crit.add(Restrictions.eq("requestorId",search.getRequestorId()));
		}
		if (search.getStatus() != null && search.getStatus().length() > 0  ) {
			log.debug("search: status=" + search.getStatus() );
			crit.add(Restrictions.eq("status",search.getStatus()));
		}

       if (search.getApproverId() != null && search.getApproverId().length() > 0) {

           log.debug("search: approverId=" + search.getApproverId());

           // in our database we have the role Id and the individual approvers in the same field
           roleIdList.add(search.getApproverId());
		}

        if (!roleIdList.isEmpty() ) {
            crit.add(Restrictions.in("requestApprovers.approverId", roleIdList));
        }

        if (search.getRequestForOrgList() != null && !search.getRequestForOrgList().isEmpty()) {

            log.debug("Filtering by OrgList=" + search.getRequestForOrgList());

            crit.add(Restrictions.in("requestForOrgId", search.getRequestForOrgList()));
        }




		crit.addOrder(Order.desc("requestDate"));
		
		List<ProvisionRequestEntity> results = (List<ProvisionRequestEntity>)crit.list();
		return results;		
	}
	*/

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
