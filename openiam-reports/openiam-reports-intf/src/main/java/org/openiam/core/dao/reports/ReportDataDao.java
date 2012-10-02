package org.openiam.core.dao.reports;

import org.openiam.core.dao.BaseDao;
import org.openiam.core.domain.reports.ReportQuery;

public interface ReportDataDao extends BaseDao<ReportQuery> {
    ReportQuery findByName(String name);
}
