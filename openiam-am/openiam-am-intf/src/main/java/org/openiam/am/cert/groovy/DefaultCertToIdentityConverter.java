package org.openiam.am.cert.groovy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.base.response.LoginResponse;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.SpringContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultCertToIdentityConverter {
	
	protected static final Log LOG = LogFactory.getLog(DefaultCertToIdentityConverter.class);
	
	protected X509Certificate clientCert;
	
	protected String regex;
	
	@Autowired
	protected LoginDataService loginDataWebService;

	@Autowired
	@Qualifier("userManager")
	protected UserDataService userManager;
	
	@Autowired
	protected SysConfiguration sysConfiguration;
	
	public DefaultCertToIdentityConverter() {
		
	}

	public final void setCertficiate(final X509Certificate cert) {
		this.clientCert = cert;
	}

	public final void init() {
		SpringContextProvider.autowire(this);
		SpringContextProvider.resolveProperties(this);
	}
	
	public void setClientDNRegex(final String regex) {
		this.regex = regex;
	}
	
	/**
	 * Returns the Login associated with the X509Certificate
	 * @return
	 */
	public Login resolve() throws BasicDataServiceException {
		String subjectDN = clientCert.getSubjectDN().getName();

		if(LOG.isDebugEnabled()) {
			LOG.debug("Subject DN is '" + subjectDN + "'");
		}

		Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(subjectDN);

		if (!matcher.find()) {
			throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN, String.format("Subject '%s' did not match regex '%s'", subjectDN, regex));
		}

		if (matcher.groupCount() != 1) {
			throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN, String.format("Regular expression must contain a single group"));
		}

		final String username = matcher.group(1);

		if(LOG.isDebugEnabled()) {
			LOG.debug("Extracted Principal name is '" + username + "'");
		}
		
		if(StringUtils.isBlank(username)) {
			throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN, String.format("Regular expression '%s' did not resolve to any matches", regex));
		}
		
		final Login login= loginDataWebService.getLoginDtoByManagedSys(username, sysConfiguration.getDefaultManagedSysId());
		if(login == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN, String.format("Regular expression '%s' did not resolve to any login for principal '%s'", regex, username));
		}
		
		return login;
	}
}
