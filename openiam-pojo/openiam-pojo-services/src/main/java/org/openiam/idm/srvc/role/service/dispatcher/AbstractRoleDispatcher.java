package org.openiam.idm.srvc.role.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.mq.constants.RoleAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zaporozhec on 8/29/16.
 */
public abstract class AbstractRoleDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response> extends AbstractAPIDispatcher<RequestBody, ResponseBody, RoleAPI> {

    @Autowired
    protected RoleDataService roleDataService;

    @Autowired
    protected RoleDozerConverter roleDozerConverter;

    public AbstractRoleDispatcher(Class<ResponseBody> responseBodyClass) {
        super(responseBodyClass);
    }

}
