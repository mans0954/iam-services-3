package org.openiam.idm.srvc.mngsys.ws;


import org.openiam.idm.srvc.meta.dto.MetadataType;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorDto;
import org.openiam.idm.srvc.mngsys.dto.ProvisionConnectorSearchBean;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.openiam.base.ws.Response;


import java.util.List;

/**
 * Service layer to manage the provisioning connectors in OpenIAM. <br>
 * Connectors are associated with a MetadataType. The MetadataType allows us to customize
 * the attributes that are captured for a connector. Additionally, the type is often used to indicate
 *  end system such as 'Active Directory', 'LDAP', etc.
 * @author suneet
 * @author vitaly.yakunin
 * @version 2
 */
@WebService
public interface ProvisionConnectorWebService {

    /**
     *
     * @param searchBean
     * @return
     */
    @WebMethod
    public Integer getProvisionConnectorsCount(@WebParam(name = "searchBean", targetNamespace = "")ProvisionConnectorSearchBean searchBean);

    @WebMethod
    public List<MetadataType> getProvisionConnectorsTypes();

    /**
     * Return an list of the connectors in the system by SearchBean
     * @return
     */
    @WebMethod
    List<ProvisionConnectorDto> getProvisionConnectors(@WebParam(name = "searchBean", targetNamespace = "")ProvisionConnectorSearchBean searchBean,
                                                      @WebParam(name = "size", targetNamespace = "")Integer size,
                                                      @WebParam(name = "from", targetNamespace = "")Integer from);
    /**
     * Adds a new connector to the system
     * @param con
     */
    @WebMethod
    void addProvisionConnector(
            @WebParam(name = "con", targetNamespace = "")
            ProvisionConnectorDto con);
    /**
     * Updates an existing connector in the system
     * @param con
     */
    @WebMethod
    void updateProvisionConnector(
            @WebParam(name = "con", targetNamespace = "")
            ProvisionConnectorDto con);
    /**
     * Removes a connector from the system
     * @param conId
     */
    @WebMethod
    Response removeProvisionConnector(
            @WebParam(name = "conId", targetNamespace = "")
            String conId);
    /**
     * Returns a connector based on the connector id that has been provided.
     * @param conId
     * @return
     */
    @WebMethod
    ProvisionConnectorDto getProvisionConnector(
            @WebParam(name = "conId", targetNamespace = "")
            String conId);

}
