package org.openiam.base.request;

/**
 * Created by alexander on 01/12/16.
 */
public class TweetMessageRequest extends BaseServiceRequest {
    private String userid;
    private String msg;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TweetMessageRequest{");
        sb.append(super.toString());
        sb.append(", userid='").append(userid).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
