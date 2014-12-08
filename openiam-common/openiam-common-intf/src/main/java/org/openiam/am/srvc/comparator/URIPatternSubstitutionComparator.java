package org.openiam.am.srvc.comparator;

import java.util.Comparator;

import org.openiam.am.srvc.dto.URIPatternSubstitution;

public class URIPatternSubstitutionComparator implements Comparator<URIPatternSubstitution> {

	@Override
	public int compare(URIPatternSubstitution o1, URIPatternSubstitution o2) {
		if(o1 == null && o2 == null) {
			return 0;
		} else if(o1 != null && o2 == null) {
			return 1;
		} else if(o1 == null && o2 != null) {
			return -1;
		} else {
			final Integer o1Order = o1.getOrder();
			final Integer o2Order = o2.getOrder();
			
			if(o1Order == null && o2Order == null) {
				return 0;
			} else if(o1Order != null && o2Order == null) {
				return 1;
			} else if(o1Order == null && o2Order != null) {
				return -1;
			} else {
				if(o1Order.equals(o2Order)) {
					return 0;
					//throw new IllegalArgumentException(String.format("%s and %s have the same order", o1, o2));
				} else {
					return o1Order.compareTo(o2Order);
				}
			}
		}
	}

}
