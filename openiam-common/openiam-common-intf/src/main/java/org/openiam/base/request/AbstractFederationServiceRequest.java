package org.openiam.base.request;

import org.springframework.http.HttpMethod;

/**
 * Created by alexander on 10/08/16.
 */
public abstract class AbstractFederationServiceRequest extends BaseServiceRequest {
    private String proxyURI;
    private HttpMethod method;

    public String getProxyURI() {
        return proxyURI;
    }


    public void setProxyURI(String proxyURI) {
        this.proxyURI = proxyURI;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AbstractFederationServiceRequest{");
        sb.append(super.toString());
        sb.append(", proxyURI='").append(proxyURI).append('\'');
        sb.append(", method=").append(method);
        sb.append('}');
        return sb.toString();
    }
}
