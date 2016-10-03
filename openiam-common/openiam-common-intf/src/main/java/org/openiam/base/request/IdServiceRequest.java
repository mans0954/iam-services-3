package org.openiam.base.request;

/**
 * Created by alexander on 08/08/16.
 */
public class IdServiceRequest extends BaseServiceRequest {

    public IdServiceRequest(){}
    public IdServiceRequest(String id){
        this.id=id;
    }
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IdServiceRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
