
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

    private final static QName _GenerateMasterKeyResponse_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "generateMasterKeyResponse");
    private final static QName _MigrateData_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "migrateData");
    private final static QName _MigrateDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "migrateDataResponse");
    private final static QName _GenerateMasterKey_QNAME = new QName("urn:idm.openiam.org/srvc/res/service", "generateMasterKey");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openiam.idm.srvc.res.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MigrateDataResponse }
     * 
     */
    public MigrateDataResponse createMigrateDataResponse() {
        return new MigrateDataResponse();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link GenerateMasterKey }
     * 
     */
    public GenerateMasterKey createGenerateMasterKey() {
        return new GenerateMasterKey();
    }

    /**
     * Create an instance of {@link GenerateMasterKeyResponse }
     * 
     */
    public GenerateMasterKeyResponse createGenerateMasterKeyResponse() {
        return new GenerateMasterKeyResponse();
    }

    /**
     * Create an instance of {@link MigrateData }
     * 
     */
    public MigrateData createMigrateData() {
        return new MigrateData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateMasterKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "generateMasterKeyResponse")
    public JAXBElement<GenerateMasterKeyResponse> createGenerateMasterKeyResponse(GenerateMasterKeyResponse value) {
        return new JAXBElement<GenerateMasterKeyResponse>(_GenerateMasterKeyResponse_QNAME, GenerateMasterKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MigrateData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "migrateData")
    public JAXBElement<MigrateData> createMigrateData(MigrateData value) {
        return new JAXBElement<MigrateData>(_MigrateData_QNAME, MigrateData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MigrateDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "migrateDataResponse")
    public JAXBElement<MigrateDataResponse> createMigrateDataResponse(MigrateDataResponse value) {
        return new JAXBElement<MigrateDataResponse>(_MigrateDataResponse_QNAME, MigrateDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateMasterKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/res/service", name = "generateMasterKey")
    public JAXBElement<GenerateMasterKey> createGenerateMasterKey(GenerateMasterKey value) {
        return new JAXBElement<GenerateMasterKey>(_GenerateMasterKey_QNAME, GenerateMasterKey.class, null, value);
    }

}
