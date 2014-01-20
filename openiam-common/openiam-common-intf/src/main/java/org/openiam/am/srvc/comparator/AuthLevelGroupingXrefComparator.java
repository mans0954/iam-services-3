package org.openiam.am.srvc.comparator;

import java.util.Comparator;

import org.openiam.am.srvc.dto.AbstractAuthLevelGroupingXref;

public class AuthLevelGroupingXrefComparator implements Comparator<AbstractAuthLevelGroupingXref> {

	@Override
	public int compare(AbstractAuthLevelGroupingXref o1,
			AbstractAuthLevelGroupingXref o2) {
		return Integer.valueOf(o1.getOrder()).compareTo(Integer.valueOf(o2.getOrder()));
	}

}
