/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */
package org.openiam.idm.srvc.pswd.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.BaseObject;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;


/**
 * Domain object representing an answer by the user for a challenge response question.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserIdentityAnswer", propOrder = {
        "id",
        "questionId",
        "userId",
        "questionAnswer"
})
@DozerDTOCorrespondence(UserIdentityAnswerEntity.class)
public class UserIdentityAnswer extends BaseObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8841064146448209034L;
    protected String id;
    protected String questionId;
    protected String userId;
    protected String questionAnswer;

    public UserIdentityAnswer() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionAnswer() {
        return this.questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

}
