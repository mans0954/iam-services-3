package org.openiam.base.response.data;

import org.openiam.base.response.data.BaseDataResponse;

import java.io.File;

/**
 * Created by alexander on 01/12/16.
 */
public class FileResponse extends BaseDataResponse<File> {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
