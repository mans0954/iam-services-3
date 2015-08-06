package org.openiam.jaas.util;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.openiam.idm.srvc.auth.service.AuthenticationWebService;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.grp.ws.GroupDataWebService;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.jaas.config.JaasConfiguration;

public class ServiceLookupHelper {

    private static JaasConfiguration jaasConfiguration = JaasConfiguration.getInstance();


    public static AuthenticationWebService getAuthenticationService() {
        return getServceInstance("AuthenticationService","AuthenticationServicePort", AuthenticationWebService.class);
    }

    public static LoginDataWebService getLoginService() {
        return getServceInstance("LoginDataWebService","LoginDataWebServiceImplPort", LoginDataWebService.class);
    }

    public static UserDataWebService getUserService() {
        return getServceInstance("UserDataService","UserDataWebServiceImplPort", UserDataWebService.class);
    }
    public static RoleDataWebService getRoleService() {
        return getServceInstance("RoleDataWebService","RoleDataWebServicePort", RoleDataWebService.class);
    }

    public static GroupDataWebService getGroupService() {
        return getServceInstance("GroupDataWebService","GroupDataWebServicePort", GroupDataWebService.class);
    }

    public static OrganizationDataService getOrgService() {
        return getServceInstance("OrganizationDataService","OrganizationDataServicePort", OrganizationDataService.class);
    }

    private static <T> T getServceInstance(String address, String qName, Class<T> clazz){
        ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(clazz);

        if(!address.startsWith("/"))
            address = jaasConfiguration.getServiceBaseUrl()+"/"+address;
        else
            address = jaasConfiguration.getServiceBaseUrl()+address;

        System.out.println("SERVICE BASE URL:" + jaasConfiguration.getServiceBaseUrl());
        System.out.println("AUTHENTICATION SERVICE URL= " + address);

        factory.setAddress(address);
        javax.xml.namespace.QName qname = javax.xml.namespace.QName.valueOf(qName);
        factory.setEndpointName(qname);
        return (T)factory.create();
    }

}
