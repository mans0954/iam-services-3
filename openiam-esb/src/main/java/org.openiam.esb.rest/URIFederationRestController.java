package org.openiam.esb.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openiam.am.cert.groovy.DefaultCACertCheck;
import org.openiam.am.cert.groovy.DefaultCertToIdentityConverter;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.am.srvc.service.URIFederationService;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.cert.service.CertDataService;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value="/proxy")
public class URIFederationRestController {
	protected final Logger log = Logger.getLogger(this.getClass());

	@Value("${org.openiam.auth.level.cert.id}")
	private String certAuthLevelId;

	@Value("${org.openiam.metadata.type.cert.auth.regex}")
	private String authLevelRegex;

	@Value("${org.openiam.metadata.type.cert.auth.regex.script}")
	private String authLevelRegexScript;

	private static final Log LOG = LogFactory.getLog(URIFederationRestController.class);

	@Autowired
	private URIFederationService uriFederationService;

	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	private ScriptIntegration scriptIntegration;

	@Autowired
	private AuthProviderService authProviderService;

	@Autowired
	@Qualifier("certManager")
	protected CertDataService certManager;

	private Map<String, HttpMethod> httpMethodMap = new HashMap<String, HttpMethod>();


	@PostConstruct
	public void init() {
		for(final HttpMethod method : HttpMethod.values()) {
			httpMethodMap.put(method.name().toLowerCase(), method);
		}
	}

	private HttpMethod getMethod(final String method) {
		return StringUtils.isNotBlank(method) ? httpMethodMap.get(method.toLowerCase()) : null;
	}

	@RequestMapping(value="/cert/identity", method=RequestMethod.POST)
	public @ResponseBody LoginResponse getIdentityFromCert(final @RequestParam(value="proxyURI", required=true) String proxyURI,
														   final @RequestParam(required=true, value="method") String method,
														   final @RequestParam(value="cert", required=true) MultipartFile certContents) {
		final LoginResponse wsResponse = new LoginResponse();
		try {
			final URIFederationResponse metadata = uriFederationService.getMetadata(proxyURI);
			if(metadata.isFailure()) {
				throw new BasicDataServiceException(ResponseCode.METADATA_INVALID);
			}

			final AuthProvider provider = authProviderService.getCachedAuthProvider(metadata.getAuthProviderId());

			if(provider == null) {
				throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID);
			}

			if(!provider.isSupportsCertAuth()) {
				throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID);
			}

			final String regex = StringUtils.trimToNull(provider.getCertRegex());
			final String regexScript = StringUtils.trimToNull(provider.getCertGroovyScript());
			final String caValidScript = StringUtils.trimToNull(provider.getCaValidateGroovyScript());
			if(StringUtils.isBlank(regex) && StringUtils.isBlank(regexScript)) {
				throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID);
			}

			X509Certificate caCert = null;
			if ((provider.getCaCert() != null) && (provider.getCaCert().length > 0)) {
				try {
					caCert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(provider.getCaCert()));
				} catch (Exception ex) {
					throw new BasicDataServiceException(ResponseCode.CERT_CA_INVALID, "Can not parse CA Cert");
				}
			}

			X509Certificate clientCert;
			try {
				clientCert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certContents.getBytes()));
			} catch (Exception ex) {
				throw new BasicDataServiceException(ResponseCode.CERT_CLIENT_INVALID, "Could not parse certificate");
			}

			DefaultCertToIdentityConverter certToIdentityConverter;
			if(regex != null) {
				certToIdentityConverter = new DefaultCertToIdentityConverter();
				certToIdentityConverter.setClientDNRegex(regex);
			} else {
				if(!scriptIntegration.scriptExists(regexScript)) {
					throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, "regexScript - not exist ");
				}
				certToIdentityConverter = (DefaultCertToIdentityConverter)scriptIntegration.instantiateClass(null, regexScript);
				if(certToIdentityConverter == null) {
					throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID);
				}
			}

			DefaultCACertCheck caCertCheck = null;
			if(StringUtils.isNotBlank(caValidScript)) {
				if(!scriptIntegration.scriptExists(caValidScript)) {
					throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, "caValidScript - not exist ");
				}
				caCertCheck = (DefaultCACertCheck)scriptIntegration.instantiateClass(null, caValidScript);
				if(caCertCheck == null) {
					throw new BasicDataServiceException(ResponseCode.CERT_CONFIG_INVALID, "Error on execute caValidScript");
				}
			}
			if (caCertCheck != null) {
				if (caCert != null) {
					caCertCheck.setCACert(caCert);
				}
				caCertCheck.setCertficiate(clientCert);
				caCertCheck.setCrlPath(provider.getDerPath());
				caCertCheck.init();
				if (!caCertCheck.resolve()) {
					throw new BasicDataServiceException(ResponseCode.CERT_INVALID_VERIFY_WITH_CA);
				}
			}

			certToIdentityConverter.setCertficiate(clientCert);
			certToIdentityConverter.init();
			final Login login = certToIdentityConverter.resolve();
			if(login == null) {
				throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN);
			}
			login.setPassword("**********");
			wsResponse.setPrincipal(login);
			wsResponse.succeed();
		} catch(BasicDataServiceException e) {
			wsResponse.fail();
			wsResponse.setErrorText(e.getResponseValue() + " : " + e.getMessage());
			wsResponse.setErrorCode(e.getCode());
			LOG.info("Cannot cert identity", e);
		} catch(Throwable e) {
			wsResponse.fail();
			wsResponse.setErrorText(e.getMessage());
			LOG.warn("Cannot cert identity", e);
		}

		return wsResponse;
	}
}
