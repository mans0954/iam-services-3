package org.openiam.idm.srvc.report.service;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.report.domain.ReportParamMetaTypeEntity;
import org.springframework.stereotype.Repository;


@Repository
public class ReportParamMetaTypeDaoImpl extends BaseDaoImpl<ReportParamMetaTypeEntity, String> implements ReportParamMetaTypeDao {

    @Override
    protected String getPKfieldName() {
        return "id";
    }
}
