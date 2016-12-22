package org.openiam.mq.constants.api.user;

import org.openiam.mq.constants.api.OpenIAMAPI;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
public enum ChallengeResponseAPI implements OpenIAMAPI {
    GetNumOfCorrectAnswers, Count, GetQuestion, FindQuestionBeans, FindAnswerBeans, SaveQuestion, DeleteQuestion, SaveAnswer, DeleteAnswer, ValidateAnswers, SaveAnswers, isResponseValid, IsUserAnsweredSecurityQuestions, ResetQuestionsForUser, GetNumOfRequiredQuestions
}
