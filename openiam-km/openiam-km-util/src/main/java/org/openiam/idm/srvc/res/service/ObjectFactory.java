
package org.openiam.idm.srvc.res.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openiam.idm.srvc.res.service package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _RefreshUserKeys_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "refreshUserKeys");
    private final static QName _RefreshUserKeysResponse_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "refreshUserKeysResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openiam.idm.srvc.res.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RefreshUserKeys }
     * 
     */
    public RefreshUserKeys createRefreshUserKeys() {
        return new RefreshUserKeys();
    }

    /**
     * Create an instance of {@link RefreshUserKeysResponse }
     * 
     */
    public RefreshUserKeysResponse createRefreshUserKeysResponse() {
        return new RefreshUserKeysResponse();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RefreshUserKeys }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "refreshUserKeys")
    public JAXBElement<RefreshUserKeys> createRefreshUserKeys(RefreshUserKeys value) {
        return new JAXBElement<RefreshUserKeys>(_RefreshUserKeys_QNAME, RefreshUserKeys.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RefreshUserKeysResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "refreshUserKeysResponse")
    public JAXBElement<RefreshUserKeysResponse> createRefreshUserKeysResponse(RefreshUserKeysResponse value) {
        return new JAXBElement<RefreshUserKeysResponse>(_RefreshUserKeysResponse_QNAME, RefreshUserKeysResponse.class, null, value);
    }

}
