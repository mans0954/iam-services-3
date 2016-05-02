
package org.openiam.idm.srvc.key.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openiam.idm.srvc.key.service package. 
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

    private final static QName _InitKeyManagement_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "initKeyManagement");
    private final static QName _GenerateKeysForUserList_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateKeysForUserList");
    private final static QName _GenerateKeysForUserListResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateKeysForUserListResponse");
    private final static QName _EncryptUserDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "encryptUserDataResponse");
    private final static QName _InitKeyManagementResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "initKeyManagementResponse");
    private final static QName _DecryptDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "decryptDataResponse");
    private final static QName _GetCookieKey_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "getCookieKey");
    private final static QName _GenerateKeysForUser_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateKeysForUser");
    private final static QName _MigrateDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "migrateDataResponse");
    private final static QName _MigrateData_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "migrateData");
    private final static QName _GenerateCookieKey_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateCookieKey");
    private final static QName _Exception_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "Exception");
    private final static QName _EncryptData_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "encryptData");
    private final static QName _EncryptDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "encryptDataResponse");
    private final static QName _GenerateCookieKeyResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateCookieKeyResponse");
    private final static QName _GetCookieKeyResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "getCookieKeyResponse");
    private final static QName _DecryptData_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "decryptData");
    private final static QName _DecryptUserDataResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "decryptUserDataResponse");
    private final static QName _DecryptUserData_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "decryptUserData");
    private final static QName _EncryptUserData_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "encryptUserData");
    private final static QName _GenerateMasterKeyResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateMasterKeyResponse");
    private final static QName _GenerateKeysForUserResponse_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateKeysForUserResponse");
    private final static QName _GenerateMasterKey_QNAME = new QName("urn:idm.openiam.org/srvc/key/service", "generateMasterKey");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openiam.idm.srvc.key.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EncryptData }
     * 
     */
    public EncryptData createEncryptData() {
        return new EncryptData();
    }

    /**
     * Create an instance of {@link EncryptDataResponse }
     * 
     */
    public EncryptDataResponse createEncryptDataResponse() {
        return new EncryptDataResponse();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link GenerateCookieKey }
     * 
     */
    public GenerateCookieKey createGenerateCookieKey() {
        return new GenerateCookieKey();
    }

    /**
     * Create an instance of {@link MigrateDataResponse }
     * 
     */
    public MigrateDataResponse createMigrateDataResponse() {
        return new MigrateDataResponse();
    }

    /**
     * Create an instance of {@link MigrateData }
     * 
     */
    public MigrateData createMigrateData() {
        return new MigrateData();
    }

    /**
     * Create an instance of {@link GenerateCookieKeyResponse }
     * 
     */
    public GenerateCookieKeyResponse createGenerateCookieKeyResponse() {
        return new GenerateCookieKeyResponse();
    }

    /**
     * Create an instance of {@link GenerateKeysForUserListResponse }
     * 
     */
    public GenerateKeysForUserListResponse createGenerateKeysForUserListResponse() {
        return new GenerateKeysForUserListResponse();
    }

    /**
     * Create an instance of {@link InitKeyManagement }
     * 
     */
    public InitKeyManagement createInitKeyManagement() {
        return new InitKeyManagement();
    }

    /**
     * Create an instance of {@link GenerateKeysForUserList }
     * 
     */
    public GenerateKeysForUserList createGenerateKeysForUserList() {
        return new GenerateKeysForUserList();
    }

    /**
     * Create an instance of {@link GenerateKeysForUser }
     * 
     */
    public GenerateKeysForUser createGenerateKeysForUser() {
        return new GenerateKeysForUser();
    }

    /**
     * Create an instance of {@link GetCookieKey }
     * 
     */
    public GetCookieKey createGetCookieKey() {
        return new GetCookieKey();
    }

    /**
     * Create an instance of {@link DecryptDataResponse }
     * 
     */
    public DecryptDataResponse createDecryptDataResponse() {
        return new DecryptDataResponse();
    }

    /**
     * Create an instance of {@link InitKeyManagementResponse }
     * 
     */
    public InitKeyManagementResponse createInitKeyManagementResponse() {
        return new InitKeyManagementResponse();
    }

    /**
     * Create an instance of {@link EncryptUserDataResponse }
     * 
     */
    public EncryptUserDataResponse createEncryptUserDataResponse() {
        return new EncryptUserDataResponse();
    }

    /**
     * Create an instance of {@link GenerateMasterKey }
     * 
     */
    public GenerateMasterKey createGenerateMasterKey() {
        return new GenerateMasterKey();
    }

    /**
     * Create an instance of {@link GenerateKeysForUserResponse }
     * 
     */
    public GenerateKeysForUserResponse createGenerateKeysForUserResponse() {
        return new GenerateKeysForUserResponse();
    }

    /**
     * Create an instance of {@link GenerateMasterKeyResponse }
     * 
     */
    public GenerateMasterKeyResponse createGenerateMasterKeyResponse() {
        return new GenerateMasterKeyResponse();
    }

    /**
     * Create an instance of {@link EncryptUserData }
     * 
     */
    public EncryptUserData createEncryptUserData() {
        return new EncryptUserData();
    }

    /**
     * Create an instance of {@link DecryptUserData }
     * 
     */
    public DecryptUserData createDecryptUserData() {
        return new DecryptUserData();
    }

    /**
     * Create an instance of {@link DecryptUserDataResponse }
     * 
     */
    public DecryptUserDataResponse createDecryptUserDataResponse() {
        return new DecryptUserDataResponse();
    }

    /**
     * Create an instance of {@link DecryptData }
     * 
     */
    public DecryptData createDecryptData() {
        return new DecryptData();
    }

    /**
     * Create an instance of {@link GetCookieKeyResponse }
     * 
     */
    public GetCookieKeyResponse createGetCookieKeyResponse() {
        return new GetCookieKeyResponse();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link EsbErrorToken }
     * 
     */
    public EsbErrorToken createEsbErrorToken() {
        return new EsbErrorToken();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitKeyManagement }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "initKeyManagement")
    public JAXBElement<InitKeyManagement> createInitKeyManagement(InitKeyManagement value) {
        return new JAXBElement<InitKeyManagement>(_InitKeyManagement_QNAME, InitKeyManagement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateKeysForUserList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateKeysForUserList")
    public JAXBElement<GenerateKeysForUserList> createGenerateKeysForUserList(GenerateKeysForUserList value) {
        return new JAXBElement<GenerateKeysForUserList>(_GenerateKeysForUserList_QNAME, GenerateKeysForUserList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateKeysForUserListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateKeysForUserListResponse")
    public JAXBElement<GenerateKeysForUserListResponse> createGenerateKeysForUserListResponse(GenerateKeysForUserListResponse value) {
        return new JAXBElement<GenerateKeysForUserListResponse>(_GenerateKeysForUserListResponse_QNAME, GenerateKeysForUserListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptUserDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "encryptUserDataResponse")
    public JAXBElement<EncryptUserDataResponse> createEncryptUserDataResponse(EncryptUserDataResponse value) {
        return new JAXBElement<EncryptUserDataResponse>(_EncryptUserDataResponse_QNAME, EncryptUserDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitKeyManagementResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "initKeyManagementResponse")
    public JAXBElement<InitKeyManagementResponse> createInitKeyManagementResponse(InitKeyManagementResponse value) {
        return new JAXBElement<InitKeyManagementResponse>(_InitKeyManagementResponse_QNAME, InitKeyManagementResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "decryptDataResponse")
    public JAXBElement<DecryptDataResponse> createDecryptDataResponse(DecryptDataResponse value) {
        return new JAXBElement<DecryptDataResponse>(_DecryptDataResponse_QNAME, DecryptDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCookieKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "getCookieKey")
    public JAXBElement<GetCookieKey> createGetCookieKey(GetCookieKey value) {
        return new JAXBElement<GetCookieKey>(_GetCookieKey_QNAME, GetCookieKey.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateKeysForUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateKeysForUser")
    public JAXBElement<GenerateKeysForUser> createGenerateKeysForUser(GenerateKeysForUser value) {
        return new JAXBElement<GenerateKeysForUser>(_GenerateKeysForUser_QNAME, GenerateKeysForUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MigrateDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "migrateDataResponse")
    public JAXBElement<MigrateDataResponse> createMigrateDataResponse(MigrateDataResponse value) {
        return new JAXBElement<MigrateDataResponse>(_MigrateDataResponse_QNAME, MigrateDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MigrateData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "migrateData")
    public JAXBElement<MigrateData> createMigrateData(MigrateData value) {
        return new JAXBElement<MigrateData>(_MigrateData_QNAME, MigrateData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateCookieKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateCookieKey")
    public JAXBElement<GenerateCookieKey> createGenerateCookieKey(GenerateCookieKey value) {
        return new JAXBElement<GenerateCookieKey>(_GenerateCookieKey_QNAME, GenerateCookieKey.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "encryptData")
    public JAXBElement<EncryptData> createEncryptData(EncryptData value) {
        return new JAXBElement<EncryptData>(_EncryptData_QNAME, EncryptData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "encryptDataResponse")
    public JAXBElement<EncryptDataResponse> createEncryptDataResponse(EncryptDataResponse value) {
        return new JAXBElement<EncryptDataResponse>(_EncryptDataResponse_QNAME, EncryptDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateCookieKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateCookieKeyResponse")
    public JAXBElement<GenerateCookieKeyResponse> createGenerateCookieKeyResponse(GenerateCookieKeyResponse value) {
        return new JAXBElement<GenerateCookieKeyResponse>(_GenerateCookieKeyResponse_QNAME, GenerateCookieKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCookieKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "getCookieKeyResponse")
    public JAXBElement<GetCookieKeyResponse> createGetCookieKeyResponse(GetCookieKeyResponse value) {
        return new JAXBElement<GetCookieKeyResponse>(_GetCookieKeyResponse_QNAME, GetCookieKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "decryptData")
    public JAXBElement<DecryptData> createDecryptData(DecryptData value) {
        return new JAXBElement<DecryptData>(_DecryptData_QNAME, DecryptData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptUserDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "decryptUserDataResponse")
    public JAXBElement<DecryptUserDataResponse> createDecryptUserDataResponse(DecryptUserDataResponse value) {
        return new JAXBElement<DecryptUserDataResponse>(_DecryptUserDataResponse_QNAME, DecryptUserDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DecryptUserData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "decryptUserData")
    public JAXBElement<DecryptUserData> createDecryptUserData(DecryptUserData value) {
        return new JAXBElement<DecryptUserData>(_DecryptUserData_QNAME, DecryptUserData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EncryptUserData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "encryptUserData")
    public JAXBElement<EncryptUserData> createEncryptUserData(EncryptUserData value) {
        return new JAXBElement<EncryptUserData>(_EncryptUserData_QNAME, EncryptUserData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateMasterKeyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateMasterKeyResponse")
    public JAXBElement<GenerateMasterKeyResponse> createGenerateMasterKeyResponse(GenerateMasterKeyResponse value) {
        return new JAXBElement<GenerateMasterKeyResponse>(_GenerateMasterKeyResponse_QNAME, GenerateMasterKeyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateKeysForUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateKeysForUserResponse")
    public JAXBElement<GenerateKeysForUserResponse> createGenerateKeysForUserResponse(GenerateKeysForUserResponse value) {
        return new JAXBElement<GenerateKeysForUserResponse>(_GenerateKeysForUserResponse_QNAME, GenerateKeysForUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GenerateMasterKey }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:idm.openiam.org/srvc/key/service", name = "generateMasterKey")
    public JAXBElement<GenerateMasterKey> createGenerateMasterKey(GenerateMasterKey value) {
        return new JAXBElement<GenerateMasterKey>(_GenerateMasterKey_QNAME, GenerateMasterKey.class, null, value);
    }

}
