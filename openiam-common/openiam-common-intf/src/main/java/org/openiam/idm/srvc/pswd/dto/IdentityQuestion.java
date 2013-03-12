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
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;

/**
 * Domain object that represents a question for use in the challenge response functionality
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentityQuestion", propOrder = {
        "id",
        "identityQuestGrpId",
        "questionText",
        "required",
        "active",
        "userId"
})
@DozerDTOCorrespondence(IdentityQuestionEntity.class)
public class IdentityQuestion extends BaseObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1802758764731284709L;
    protected String id;
    protected String identityQuestGrpId;
    protected String questionText;
    protected boolean required = false;
    protected boolean active = true;
    protected String userId;

    public IdentityQuestion() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getIdentityQuestGrpId() {
		return identityQuestGrpId;
	}

	public void setIdentityQuestGrpId(String identityQuestGrpId) {
		this.identityQuestGrpId = identityQuestGrpId;
	}

    
}
