package org.openiam.am.cert.groovy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cert.X509CRLHolder;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.cert.service.CertDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


public class DefaultCACertCheck {

	protected static final Log LOG = LogFactory.getLog(DefaultCACertCheck.class);

	protected X509Certificate clientCert;

	protected X509Certificate caCert;

	protected String crlPath;

	@Autowired
	@Qualifier("certManager")
	protected CertDataService certManager;

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

	public void setCrlPath(String crlPath) {
		this.crlPath = crlPath;
	}

	/**
	 * Returns the Login associated with the X509Certificate
	 * @return
	 */
	public Boolean resolve() throws BasicDataServiceException {
		/*
		if (caCert != null) {
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
					clientCert.verify(caCert.getPublicKey());
				} catch (Exception ex) {
					throw new BasicDataServiceException(ResponseCode.CERT_INVALID_VERIFY_WITH_CA, ex.getMessage());
				}

				try {
					certManager.verifyCertificateNotRevoked(caCert, clientCert);
				} catch (Exception ex) {
					if (StringUtils.isBlank(crlPath)) {
						List<X509CRLHolder> crlList = new ArrayList<X509CRLHolder>();
						// "file://crl.der"    "https://lnx1.openiamdemo.com/crl"
						crlList.add(certManager.downloadCRL(crlPath));
						certManager.verifyCertificateNotRevoked(crlList, caCert, clientCert);
					}
				}

			}
		}
		*/
			return true;
	}
}
