package org.openiam.service.integration.challengeresponse;

import java.util.List;

import org.openiam.base.KeyDTO;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractChallengeResponseServiceTest<T extends KeyDTO, S extends AbstractSearchBean<T, String>> extends AbstractKeyServiceTest<T, S> {

	@Autowired
	@Qualifier("challengeResponseServiceClient")
	protected ChallengeResponseWebService challengeResponseServiceClient;
	
	protected IdentityQuestion getFirstQuestion() {
		return challengeResponseServiceClient.findQuestionBeans(new IdentityQuestionSearchBean(), 0, 1, getDefaultLanguage()).get(0);
	}
}
