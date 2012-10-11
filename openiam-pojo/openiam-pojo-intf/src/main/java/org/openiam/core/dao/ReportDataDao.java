package org.openiam.core.dao;

import org.openiam.core.domain.ReportInfo;

public interface ReportDataDao extends BaseDao<ReportInfo, String> {
    ReportInfo findByName(String name);
}
