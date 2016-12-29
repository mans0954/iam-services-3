package org.openiam.am.cert.groovy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCACertCheck {

	protected static final Log LOG = LogFactory.getLog(DefaultCACertCheck.class);

	protected X509Certificate clientCert;

	protected X509Certificate caCert;

	@Autowired
	protected LoginDataService loginDataWebService;

	@Autowired
	@Qualifier("userManager")
	protected UserDataService userManager;

	@Autowired
	protected SysConfiguration sysConfiguration;

	public DefaultCACertCheck() {
		
	}

	public final void setCertficiate(final X509Certificate cert) {
		this.clientCert = cert;
	}

	public final void init() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}

	public void setCACert(final X509Certificate caCert) {
		this.caCert = caCert;
	}
	
	/**
	 * Returns the Login associated with the X509Certificate
	 * @return
	 */
	public Boolean resolve() throws BasicDataServiceException {
/*		if (caCert != null) {
			try {
				caCert.checkValidity();
			} catch (Exception ex) {
				throw new BasicDataServiceException(ResponseCode.CERT_CA_INVALID, ex.getMessage());
			}
			if (clientCert != null) {
				try {
					clientCert.checkValidity();
				} catch (Exception ex) {
					throw new BasicDataServiceException(ResponseCode.CERT_CLIENT_INVALID, ex.getMessage());
				}
				try {
					caCert.verify(clientCert.getPublicKey());
				} catch (Exception ex) {
					throw new BasicDataServiceException(ResponseCode.CERT_INVALID_VERIFY_WITH_CA, ex.getMessage());
				}
			}
		}*/
		return true;
	}
}
