package org.openiam.idm.srvc.pswd.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.pswd.rule.PasswordRuleException;
import org.openiam.idm.srvc.pswd.rule.PasswordRuleViolation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PasswordValidationResponse", propOrder = {
        "rules",
        "violations"
})
public class PasswordValidationResponse extends Response {
	
	public PasswordValidationResponse() {}
	
	public PasswordValidationResponse(final ResponseStatus status) {
		super(status);
	}

	private List<PasswordRuleViolation> violations;
	private List<PasswordRule> rules;
	
	public List<PasswordRule> getRules() {
		return rules;
	}

	public void setRules(List<PasswordRule> rules) {
		this.rules = rules;
	}

	public List<PasswordRuleViolation> getViolations() {
		return violations;
	}

	public void setViolations(List<PasswordRuleViolation> violations) {
		this.violations = violations;
	}
	
	public void addViolation(final PasswordRuleViolation violation) {
		if(violation != null) {
			if(this.violations == null) {
				this.violations = new LinkedList<>();
			}
			this.violations.add(violation);
		}
	}
}
