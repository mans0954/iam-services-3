package org.openiam.idm.srvc.meta.comparator;

import java.util.Comparator;

import org.openiam.idm.srvc.meta.dto.PageElement;


public class PageElementComparator implements Comparator<PageElement> {
	
	public static final Comparator<PageElement> INSTANCE = new PageElementComparator();

	@Override
	public int compare(PageElement o1, PageElement o2) {
		int retVal = 1;
		if(o1.getOrder() != null && o2.getOrder() != null && !o1.getOrder().equals(o2.getOrder())) {
			retVal = o1.getOrder().compareTo(o2.getOrder());
		} else if(o1.getOrder() != null && o2.getOrder() == null) {
			retVal = 1;
		} else if(o1.getOrder() == null && o2.getOrder() != null) {
			retVal = -1;
		} else { /* both null, or orders equal */
			retVal = o1.getAttributeName().compareTo(o2.getAttributeName());
			if(retVal == 0) { /* db constraint should prevent this, but... */
				retVal = 1;
			}
		}
		return retVal;
	}

}
