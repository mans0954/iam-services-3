package org.openiam.sso.saml;

import org.openiam.sso.constant.SSOPropertiesKey;
import org.openiam.sso.constant.SSOType;
import org.openiam.sso.utils.SSOProperties;
import org.openiam.sso.utils.SSOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Created by: Alexander Duckardt
 * Date: 26.09.12
 */
public class SamlGenerator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static SamlGenerator instance = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    private SamlGenerator(){
        this.sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
    };

    public static SamlGenerator getInstance(){
          if(SamlGenerator.instance==null){
              SamlGenerator.instance = new SamlGenerator();
          }
          return SamlGenerator.instance;
    }

    public String generateSAML(SSOType type,  SSOProperties properties) throws Exception {
        String samlData = "";
        switch (type){
            case SAMLAuthRequest:
                samlData = getSamlAuthnRequest(properties);
                break;
            case SAMLLogoutRequest:
                samlData = getSamlLogutRequest(properties);
                break;
            case SAMLLogoutResponse:
                samlData = getSAMLLogoutResponse(properties);
                break;
            default:
                throw new IllegalArgumentException("Unknown or unimplemented SAML Generator for type: " + type.name());
        }
        log.debug("Original SAML request: \n" + samlData);
        byte[] deflatedSaml = SSOUtils.deflate(samlData);
        String deflatedRequest =   new String(deflatedSaml, 0, deflatedSaml.length, "UTF-8") ;
        log.debug("deflated SAML request: \n" + deflatedRequest);

        String codedRequest =   SSOUtils.encodeBase64(deflatedSaml);
        log.debug("Base64 encoded SAML request: \n" + codedRequest);

        String urlEncoded = URLEncoder.encode(codedRequest, "UTF-8");
        log.debug("Url Encoded  SAML request: \n" + urlEncoded);
        return urlEncoded;
    }


    private String getSAMLLogoutResponse(SSOProperties properties) throws Exception {
        String requestId = (String)properties.getAttribute(SSOPropertiesKey.requestId);
        if(requestId==null || requestId.isEmpty())
            throw new IllegalArgumentException("Request Id is required parameter");
        String inResponseTo = (String)properties.getAttribute(SSOPropertiesKey.inResponseTo);
        if(inResponseTo==null || inResponseTo.isEmpty())
            throw new IllegalArgumentException("InResponseTo is required parameter");
        String idpLogoutUrl = (String)properties.getAttribute(SSOPropertiesKey.idpLogoutUrl);
        if(idpLogoutUrl==null || idpLogoutUrl.isEmpty())
            throw new IllegalArgumentException("IdP Logout Url is required parameter");
        String spEntityId = (String)properties.getAttribute(SSOPropertiesKey.spEntityId);
        if(spEntityId==null || spEntityId.isEmpty())
            throw new IllegalArgumentException("SP Entity Id is required parameter");
        String statusCode = (String)properties.getAttribute(SSOPropertiesKey.statusCode);
        if(statusCode==null || statusCode.isEmpty())
            throw new IllegalArgumentException("Status code is required parameter");

        StringBuilder samlResponse = new StringBuilder();
        samlResponse.append("<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"");
        samlResponse.append(" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"");
        samlResponse.append(" ID=\"").append(requestId).append("\"");
        samlResponse.append(" Version=\"2.0\"");
        samlResponse.append(" IssueInstant=\"").append(sdf.format(new Date())).append("\"");
        samlResponse.append(" Destination=\"").append(idpLogoutUrl).append("\"");
        samlResponse.append(" InResponseTo=\"").append(requestId).append("\">");
        samlResponse.append(" <saml:Issuer>").append(spEntityId).append("</saml:Issuer>");
        samlResponse.append("<samlp:Status>");
        samlResponse.append(" <samlp:StatusCode Value=\"").append(statusCode).append("\"/>");
        samlResponse.append("</samlp:Status>");
        samlResponse.append("</samlp:LogoutResponse>");
        return samlResponse.toString();
    }

    private  String getSamlLogutRequest(SSOProperties properties) throws Exception{
        String requestId = (String)properties.getAttribute(SSOPropertiesKey.requestId);
        if(requestId==null || requestId.isEmpty())
            throw new IllegalArgumentException("Request Id is required parameter");
        String ssoToken = (String)properties.getAttribute(SSOPropertiesKey.ssoToken);
        if(ssoToken==null || ssoToken.isEmpty())
            throw new IllegalArgumentException("SSO Token is required parameter");
        String spEntityId = (String)properties.getAttribute(SSOPropertiesKey.spEntityId);
        if(spEntityId==null || spEntityId.isEmpty())
            throw new IllegalArgumentException("SP Entity Id is required parameter");
        String idpLogoutUrl = (String)properties.getAttribute(SSOPropertiesKey.idpLogoutUrl);
        if(idpLogoutUrl==null || idpLogoutUrl.isEmpty())
            throw new IllegalArgumentException("IdP Logout Url is required parameter");

        StringBuilder samlRequest = new StringBuilder();
        samlRequest.append("<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"");
        samlRequest.append(" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"");
        samlRequest.append(" ID=\"").append(requestId).append("\"");
        samlRequest.append(" Version=\"2.0\"");
        samlRequest.append(" IssueInstant=\"").append(sdf.format(new Date())).append("\"");
        samlRequest.append(" Destination=\"").append(idpLogoutUrl).append("\">");
        samlRequest.append(" <saml:Issuer>").append(spEntityId).append("</saml:Issuer>");
        samlRequest.append("<saml:NameID SPNameQualifier=\"").append(spEntityId).append("\"");
        samlRequest.append(" Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:transient\">").append(requestId).append("</saml:NameID>");
        samlRequest.append("<samlp:SessionIndex>").append(ssoToken).append("</samlp:SessionIndex>");
        samlRequest.append("</samlp:LogoutRequest>");
        return samlRequest.toString();
    }

    private String getSamlAuthnRequest(SSOProperties properties) throws Exception{
        String requestId = (String)properties.getAttribute(SSOPropertiesKey.requestId);
        if(requestId==null || requestId.isEmpty())
            throw new IllegalArgumentException("Request Id is required parameter");
        String spEntityId = (String)properties.getAttribute(SSOPropertiesKey.spEntityId);
        if(spEntityId==null || spEntityId.isEmpty())
            throw new IllegalArgumentException("SP Entity Id is required parameter");

        StringBuilder samlRequest = new StringBuilder();
        samlRequest.append("<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"");
        samlRequest.append(" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
        samlRequest.append(" ID=\"").append(requestId).append("\"");
        samlRequest.append(" Version=\"2.0\"");
        samlRequest.append(" IssueInstant=\"").append(sdf.format(new Date())).append("\">");
        samlRequest.append(" <saml:Issuer>").append(spEntityId).append("</saml:Issuer>");
        samlRequest.append(" <samlp:NameIDPolicy AllowCreate=\"false\" Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:principal\"/>");
        samlRequest.append(" </samlp:AuthnRequest>");
        return samlRequest.toString();
    }
}
