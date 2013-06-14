package org.openiam.spml2;

import org.openiam.idm.srvc.synch.service.generic.GenericObjectSynchService;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.msg.password.ExpirePasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordRequestType;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordRequestType;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.constants.CommandType;

public class RequestFactory {
    public static <ProvisionObject extends GenericProvisionObject> RequestType<ProvisionObject> getBean(CommandType type, Class<ProvisionObject> clazz) throws Exception {
        switch (type) {
            case ADD:
                return new AddRequestType<ProvisionObject>();
            case DELETE:
                return new DeleteRequestType<ProvisionObject>();
            case MODIFY:
                return new ModifyRequestType<ProvisionObject>();
            case LOOKUP_ATTRIBUTE_NAME:
                return new LookupAttributeRequestType<ProvisionObject>();
            case LOOKUP:
                return new LookupRequestType<ProvisionObject>();
            case SET_PASSWORD:
                return (RequestType<ProvisionObject>)(new SetPasswordRequestType());
            case EXPIRE_PASSWORD:
                return (RequestType<ProvisionObject>)(new ExpirePasswordRequestType());
            case RESET_PASSWORD:
                return (RequestType<ProvisionObject>)(new ResetPasswordRequestType());
            case VALIDATE_PASSWORD:
                return (RequestType<ProvisionObject>)(new ValidatePasswordRequestType());
            case SUSPEND:
                return (RequestType<ProvisionObject>)(new SuspendRequestType());
            case RESUME:
                return (RequestType<ProvisionObject>)(new ResumeRequestType());
            default:
                throw new Exception("sdas");
        }
    }
}
