package org.openiam.base.response;

import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.base.ws.Response;

/**
 * Created by alexander on 10/08/16.
 */
public class ContentProviderResponse extends Response {
    private ContentProvider provider;

    public ContentProvider getProvider() {
        return provider;
    }

    public void setProvider(ContentProvider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ContentProviderResponse{");
        sb.append(super.toString());
        sb.append(", provider=").append(provider);
        sb.append('}');
        return sb.toString();
    }
}
