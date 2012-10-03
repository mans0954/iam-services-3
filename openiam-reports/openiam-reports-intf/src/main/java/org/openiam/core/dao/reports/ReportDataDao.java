package org.openiam.core.dao.reports;

import org.openiam.core.dao.BaseDao;
import org.openiam.core.domain.reports.ReportInfo;

public interface ReportDataDao extends BaseDao<ReportInfo> {
    ReportInfo findByName(String name);
}
