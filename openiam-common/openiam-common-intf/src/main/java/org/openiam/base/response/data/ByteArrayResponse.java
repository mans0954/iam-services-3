package org.openiam.base.response.data;

/**
 * Created by aduckardt on 2016-12-12.
 */
public class ByteArrayResponse extends BaseDataResponse<byte[]>{
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ByteArrayResponse{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
}
