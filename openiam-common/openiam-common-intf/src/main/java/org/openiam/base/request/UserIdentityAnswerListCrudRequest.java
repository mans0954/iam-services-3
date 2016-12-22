package org.openiam.base.request;

import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

import java.util.List;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public class UserIdentityAnswerListCrudRequest extends BaseCrudServiceRequest<UserIdentityAnswer> {
    private String userId;
    private List<UserIdentityAnswer>  answerList;
    public UserIdentityAnswerListCrudRequest(){
        super(null);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserIdentityAnswer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<UserIdentityAnswer> answerList) {
        this.answerList = answerList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserIdentityAnswerListCrudRequest{");
        sb.append(super.toString());
        sb.append(",                 userId='").append(userId).append('\'');
        sb.append(",                 answerList=").append(answerList);
        sb.append('}');
        return sb.toString();
    }
}
