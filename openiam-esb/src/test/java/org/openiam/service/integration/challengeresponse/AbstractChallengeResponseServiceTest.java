package org.openiam.service.integration.challengeresponse;

import org.openiam.base.KeyDTO;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.srvc.user.ChallengeResponseWebService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractChallengeResponseServiceTest<T extends KeyDTO, S extends AbstractSearchBean<T, String>> extends AbstractKeyServiceTest<T, S> {

    @Autowired
    @Qualifier("challengeResponseServiceClient")
    protected ChallengeResponseWebService challengeResponseServiceClient;

    @Autowired
    @Qualifier("policyServiceClient")
    protected PolicyDataService policyServiceClient;

    protected IdentityQuestion getFirstQuestion() {
        return challengeResponseServiceClient.findQuestionBeans(new IdentityQuestionSearchBean(), 0, 1, getDefaultLanguage()).get(0);
    }

    protected Policy getDefaultPasswordPolicy() {
        return policyServiceClient.getPolicy("4000");
    }
}
