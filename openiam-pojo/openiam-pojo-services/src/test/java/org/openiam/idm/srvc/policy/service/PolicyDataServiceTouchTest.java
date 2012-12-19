/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.policy.service;

import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

/**
 * PolicyDataService is used create and manage policies. 
 * Enforcement of these policies is handled through policy specific services and policy enforcement points. 
 * @author suneet
 *
 */

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml",
        "classpath:test-application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class PolicyDataServiceTouchTest extends
        AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private PolicyDataService policyDataService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#getPolicyTypes()
     */
    @Test
    public void getPolicyTypes() {
        policyDataService.getPolicyTypes();
    }

    @Test
    public void addPolicyDefinition() {
        policyDataService.addPolicyDefinition(new PolicyDef());

    }

    @Test
    public void getPolicyDefinition() {
        policyDataService.getPolicyDefinition("");
    }

    @Test
    public void removePolicyDefinition() {
        policyDataService.removePolicyDefinition("");

    }

    /**
     * Returns an array of all policy definitions
     * @return
     */
    @Test
    public void getAllPolicyDef() {
        policyDataService.getAllPolicyDef();
    }

    @Test
    public void addPolicy() {
        policyDataService.addPolicy(new Policy());
    }

    @Test
    public void getAllPolicies() {
        policyDataService.getAllPolicies("");
    }

    @Test
    public void getPolicy() {
        policyDataService.getPolicy("");
    }

    /**
     * Policy definitions parameters can be further categorized by parameter groups.
     * @param paramGroup
     * @return
     */
    @Test
    public void getPolicyDefParamByGroup() {
        policyDataService.getPolicyDefParamByGroup("", "");
    }

    @Test
    public void removePolicy() {
        policyDataService.removePolicy("");
    }

    @Test
    public void isPolicyExist() {
        policyDataService.isPolicyExist("", "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#associatePolicyToObject
     * (org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
     */
    @Test
    public void associatePolicyToObject() {
        policyDataService.associatePolicyToObject(new PolicyObjectAssoc());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.policy.service.PolicyDataService#
     * getAssociationsForPolicy(java.lang.String)
     */
    @Test
    public void getAssociationsForPolicy() {
        policyDataService.getAssociationsForPolicy("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.policy.service.PolicyDataService#updatePolicyAssociation
     * (org.openiam.idm.srvc.policy.dto.PolicyObjectAssoc)
     */

    @Test
    public void findAssociationByLevel() {
        policyDataService.findAssociationByLevel("", "");
    }
}
