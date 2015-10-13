package org.openiam.idm.srvc.audit.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.audit.domain.WorkflowStatusRptViewEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by anton on 11.10.15.
 */
@Repository("workflowStatusRptViewDAOImpl")
public class WorkflowStatusRptViewDAOImpl extends BaseDaoImpl<WorkflowStatusRptViewEntity, String> implements WorkflowStatusRptViewDAO {

    @Override
    public List<WorkflowStatusRptViewEntity> getResultForReport(Date from, Date to, String actionId, String resourceId){
        Criteria criteria = super.getCriteria();

        if(from != null && to != null) {
            criteria.add(Restrictions.between("approvalDate", from, to));
        } else if(from != null) {
            criteria.add(Restrictions.gt("approvalDate", from));
        } else if(to != null) {
            criteria.add(Restrictions.lt("approvalDate", to));
        }

        if(StringUtils.isNotBlank(actionId)) {
            criteria.add(Restrictions.eq("logAction", actionId));
        }

        if(StringUtils.isNotBlank(resourceId)) {
            criteria.add(Restrictions.eq("associationId", resourceId));
        }

        criteria.add(Restrictions.isNotNull("employeeFirstName"));
        criteria.add(Restrictions.isNotNull("employeeLastName"));

        List<WorkflowStatusRptViewEntity> workflowStatusRptViewEntities = (List<WorkflowStatusRptViewEntity>)criteria.list();

        return workflowStatusRptViewEntities;
    }


    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
