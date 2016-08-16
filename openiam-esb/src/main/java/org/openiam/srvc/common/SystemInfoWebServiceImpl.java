package org.openiam.srvc.common;

import org.openiam.base.SysConfiguration;
import org.openiam.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.jws.WebService;
import javax.servlet.ServletContext;

@Service("systemInfoWS")
@WebService(endpointInterface = "org.openiam.srvc.common.SystemInfoWebService",
        targetNamespace = "urn:idm.openiam.org/util/service", portName = "SystemInfoWebServicePort",
        serviceName = "SystemInfoWebService")
public class SystemInfoWebServiceImpl implements SystemInfoWebService, ServletContextAware {

    @Autowired
    private SysConfiguration sysConfiguration;

    private ServletContext servletContext;

    @Override
    public Boolean isDevelopmentMode() {
        return sysConfiguration.isDevelopmentMode();
    }

    @Override
    public String getWarManifestInfo(String attrName) {
        return SystemUtils.getWarManifestInfo(servletContext, attrName);
    }

    @Override
    public String getJarManifestInfo(String resName, String attrName) {
        return SystemUtils.getJarManifestInfo(resName, attrName);
    }

    @Override
    public String getOsInfo(String param) {
        return SystemUtils.getOsInfo(param);
    }

    @Override
    public String getJavaInfo(String param) {
        return SystemUtils.getJavaInfo(param);
    }

    @Override
    public String getMemInfo(String param) {
        return SystemUtils.getMemInfo(param);
    }

    @Override
    public Boolean isWindows() {
        return SystemUtils.isWindows();
    }

    @Override
    public Boolean isLinux() {
        return SystemUtils.isLinux();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

	@Override
	public String getProjectVersion() {
		return getWarManifestInfo("Openiam-Version");
	}


}
