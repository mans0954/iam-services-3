package org.openiam.am.srvc.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;

/**
 * 
 * @author Lev Bornovalov
 *
 */
public class URIPatternMethodComparator implements Comparator<URIPatternMethod>, Serializable {
	
	private static final Log LOG = LogFactory.getLog(URIPatternMethodComparator.class);

	/* 
	 * This method makes the assumption that the HTTP Method represented by o1 and o2 is equal (i.e. o1.method==o2.method)
	 */
	@Override
	public int compare(final URIPatternMethod o1, final URIPatternMethod o2) {
		if(o1 == null && o2 == null) {
			return 0;
		} else if(o1 != null && o2 == null) {
			return 1;
		} else if(o1 == null && o2 != null) {
			return -1;
		} else {
			//if they are the same ID, they are equal.
			if(o1.getId().equals(o2.getId())) {
				return 0;
			}
			
			final PatternMatchMode mode1 = o1.getMatchMode();
			final PatternMatchMode mode2 = o2.getMatchMode();
			if(mode1 == null && mode2 == null) {
				return 0;
			} else if(mode1 != null && mode2 == null) {
				return 1;
			} else if(mode1 == null && mode2 != null) {
				return -1;
			} else {
				if(mode1.getLevel() > mode2.getLevel()) {
					return 1;
				} else if(mode1.getLevel() < mode2.getLevel()) {
					return -1;
				} else { 
					//the levels should be equal ONLY for SPECIFIC_PARAMS at this point.  Confirm this.
					if(!mode1.equals(PatternMatchMode.SPECIFIC_PARAMS)) {
						throw new IllegalArgumentException(String.format("%s should have SPECIFIC_PARAMS mode", o1));
					}
					if(!mode2.equals(PatternMatchMode.SPECIFIC_PARAMS)) {
						throw new IllegalArgumentException(String.format("%s should have SPECIFIC_PARAMS mode", o2));
					}
					
					/* both should have parameters */
					if(CollectionUtils.isEmpty(o1.getParams())) {
						throw new IllegalArgumentException(String.format("%s should have parameters", o1));
					}
					
					if(CollectionUtils.isEmpty(o2.getParams())) {
						throw new IllegalArgumentException(String.format("%s should have parameters", o2));
					}
					
					
					/* now, sort by parameters */
					
					//step 1 - sort by number of parameters first */
					if(o1.getParams().size() > o2.getParams().size()) {
						return 1;
					} else if(o1.getParams().size() < o2.getParams().size()) {
						return -1;
					} else {
						//step 2 - if the number of parameters are equal, sort by values.
						// parameters with values get extra points
						int numOfValues1 = 0;
						int numOfValues2 = 0;
						
						//final Map<String, List<String>> o1ValueSet = new HashMap<String, List<String>>();
						for(final URIPatternMethodParameter param : o1.getParams()) {
							//if(!o1ValueSet.containsKey(param.getName().toLowerCase())) {
							//	o1ValueSet.put(param.getName(), new LinkedList<String>());
							//}
							if(CollectionUtils.isNotEmpty(param.getValues())) {
								//o1ValueSet.get(param.getName()).addAll(new LinkedList<String>(param.getValues()));
								numOfValues1 += param.getValues().size();
							}
						}
						
						//final Map<String, List<String>> o2ValueSet = new HashMap<String, List<String>>();
						for(final URIPatternMethodParameter param : o2.getParams()) {
							//if(!o2ValueSet.containsKey(param.getName().toLowerCase())) {
							//	o2ValueSet.put(param.getName(), new LinkedList<String>());
							//}
							if(CollectionUtils.isNotEmpty(param.getValues())) {
								//o2ValueSet.get(param.getName()).addAll(new LinkedList<String>(param.getValues()));
								numOfValues2 += param.getValues().size();
							}
						}
						
						/*
						o1ValueSet.forEach((key, value) ->  {
							Collections.sort(value);
						});
						
						o2ValueSet.forEach((key, value) ->  {
							Collections.sort(value);
						});
						*/
						
						if(numOfValues1 > numOfValues2) {
							return 1;
						} else if(numOfValues1 < numOfValues2) {
							return -1;
						} else {
							//at this point, everything is the same except the parameter values.  A lookahead will be required
							//when doing the match
							o1.setHasSimiliarMethodInParentURI(true);
							o2.setHasSimiliarMethodInParentURI(true);
							return 1;
						}
					}
				}
			}
		}
	}

	
}
