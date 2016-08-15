package org.openiam.jaas.util;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.openiam.srvc.user.LoginDataWebService;
import org.openiam.srvc.am.GroupDataWebService;
import org.openiam.idm.srvc.key.ws.KeyManagementWS;
import org.openiam.idm.srvc.org.service.OrganizationDataService;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.srvc.user.UserDataWebService;
import org.openiam.jaas.config.JaasConfiguration;
import org.openiam.srvc.am.AuthenticationService;

public class ServiceLookupHelper {

    private static JaasConfiguration jaasConfiguration = JaasConfiguration.getInstance();


    public static AuthenticationService getAuthenticationService() {
        return getServceInstance("AuthenticationService","AuthenticationServicePort", AuthenticationService.class);
    }
    
    public static KeyManagementWS getKeyManagementService() {
    	return getServceInstance("KeyManagementWS","KeyManagementWSPort", KeyManagementWS.class);
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
