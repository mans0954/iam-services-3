package org.openiam.idm.srvc.audit.service;

import org.hibernate.Criteria;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.audit.domain.WorkflowStatusRptViewEntity;

import java.util.Date;
import java.util.List;

/**
 * Created by anton on 11.10.15.
 */
public interface WorkflowStatusRptViewDAO extends BaseDao<WorkflowStatusRptViewEntity, String> {
    List<WorkflowStatusRptViewEntity> getResultForReport(Date from, Date to, String actionId, String resourceId);
}
