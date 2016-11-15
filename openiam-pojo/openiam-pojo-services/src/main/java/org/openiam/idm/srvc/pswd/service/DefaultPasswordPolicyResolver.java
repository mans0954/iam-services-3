package org.openiam.idm.srvc.pswd.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;

public class DefaultPasswordPolicyResolver {
	

	/**
	 * Can be overridden in a groovy script, using the org.openiam.password.policy.resolver.script property
	 * This is called within a Transacation
	 * Picks a PolicyEntity from the given policies, which will be used as the primary password policy for this request
	 * @param policies - the list of policies that can theoretically apply to this request
	 * @param searchBean - searchBean that was used for this request
	 * @return a PolicyEntity for this request.  Can be null.  In case of null, the Global PasswordPolicy will be used
	 */
	public PolicyEntity resolve(final PasswordPolicyAssocSearchBean searchBean, final List<PolicyEntity> policies) {
		if(CollectionUtils.isEmpty(policies)) {
			return null;
		} else {
			Collections.sort(policies, COMPARATOR);
			return policies.get(0);
		}
	}
	
	private static final Comparator<PolicyEntity> COMPARATOR = new PolicyComparator();
	
	private static final class PolicyComparator implements Comparator<PolicyEntity> {

		@Override
		public int compare(final PolicyEntity o1, final PolicyEntity o2) {
			if(o1 == null && o2 == null) {
				return 0;
			} else if(o1 != null && o2 == null) {
				return -1;
			} else if(o1 == null && o2 != null) {
				return 1;
			} else {
				final Integer priority1 = o1.getPriority();
				final Integer priority2 = o2.getPriority();
				if(priority1 == null && priority2 == null) {
					return 0;
				} else if(priority1 != null && priority2 == null) {
					return -1;
				} else if(priority1 == null && priority2 != null) {
					return 1;
				} else {
					return Integer.valueOf(priority1).compareTo(Integer.valueOf(priority2)) * -1;
				}
			}
		}
		
	}
}
