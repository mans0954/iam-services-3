package org.openiam.idm.srvc.meta.comparator;

import java.util.Comparator;

import org.openiam.idm.srvc.meta.dto.PageElementValidValue;

public class PageElementValidValueComparator implements Comparator<PageElementValidValue> {
	
	public static final Comparator<PageElementValidValue> INSTANCE = new PageElementValidValueComparator();

	@Override
	public int compare(PageElementValidValue o1, PageElementValidValue o2) {
		int retVal = 1;
		if(o1.getDisplayOrder() != null && o2.getDisplayOrder() != null && !o1.getDisplayOrder().equals(o2.getDisplayOrder())) {
			retVal = o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
		} else if(o1.getDisplayOrder() != null && o2.getDisplayOrder() == null) {
			retVal = 1;
		} else if(o1.getDisplayOrder() == null && o2.getDisplayOrder() != null) {
			retVal = -1;
		} else { /* both null, or orders equal */
			retVal = o1.getDisplayName().compareTo(o2.getDisplayName());
			if(retVal == 0) { /* db constraint should prevent this, but... */
				retVal = 1;
			}
		}
		return retVal;
	}

}
