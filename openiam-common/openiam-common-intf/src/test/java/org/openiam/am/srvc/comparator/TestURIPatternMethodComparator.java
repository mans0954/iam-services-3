package org.openiam.am.srvc.comparator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestURIPatternMethodComparator {
	
	private String getRandomName() {
		return RandomStringUtils.randomAlphabetic(4);
	}
	
	private URIPatternMethod createMethodWithMode(final PatternMatchMode mode) {
		final URIPatternMethod method = new URIPatternMethod();
		method.setId(getRandomName());
		method.setMatchMode(mode);
		return method;
	}
	
	private URIPatternMethodParameter createMethodParam(final String name, final List<String> values) {
		final URIPatternMethodParameter param = new URIPatternMethodParameter();
		param.setName(name);
		param.setValues(values);
		return param;
	}
	
	private List<String> createValueList(final int num) {
		final List<String> list = new LinkedList<String>();
		for(int i = 0; i < num; i++) {
			list.add(getRandomName());
		}
		return list;
	}

	@Test
	public void testExceptions() {
		final Set<URIPatternMethod> methodSet = new TreeSet<URIPatternMethod>(new URIPatternMethodComparator());
		methodSet.add(createMethodWithMode(PatternMatchMode.NO_PARAMS));
		try {
			methodSet.add(createMethodWithMode(PatternMatchMode.NO_PARAMS));
			Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		methodSet.add(createMethodWithMode(PatternMatchMode.ANY_PARAMS));
		try {
			methodSet.add(createMethodWithMode(PatternMatchMode.ANY_PARAMS));
			Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		methodSet.add(createMethodWithMode(PatternMatchMode.IGNORE));
		try {
			methodSet.add(createMethodWithMode(PatternMatchMode.IGNORE));
			Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		methodSet.add(createMethodWithMode(PatternMatchMode.SPECIFIC_PARAMS));
		try {
			methodSet.add(createMethodWithMode(PatternMatchMode.SPECIFIC_PARAMS));
			Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		final URIPatternMethod method1 = createMethodWithMode(PatternMatchMode.SPECIFIC_PARAMS);
		final URIPatternMethod method2 = createMethodWithMode(PatternMatchMode.SPECIFIC_PARAMS);
		final String paramName = getRandomName();
		
		method1.addParam(createMethodParam(paramName, null));
		method2.addParam(createMethodParam(paramName, null));
		methodSet.add(method1);
		try {
			methodSet.add(method2);
			//Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		final List<String> values = createValueList(2);
		method1.addParam(createMethodParam(paramName, values));
		method2.addParam(createMethodParam(paramName, values));
		methodSet.add(method1);
		try {
			methodSet.add(method2);
			//Assert.assertFalse(true, "Expecting IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		methodSet.clear();
		method1.addParam(createMethodParam(paramName, createValueList(2)));
		methodSet.add(method1);
		methodSet.add(method2);
	}
	
	@Test
	public void testSpecificParams() {
		final Set<URIPatternMethod> methodSet = new TreeSet<URIPatternMethod>(new URIPatternMethodComparator());
		
		final List<URIPatternMethod> methods = new LinkedList<URIPatternMethod>();
		for(int i = 3; i <= 7; i++) {
			final URIPatternMethod method = createMethodWithMode(PatternMatchMode.SPECIFIC_PARAMS);
			method.setId(i + "");
			for(int j = 7 - i; j < 7; j++) {
				method.addParam(createMethodParam(getRandomName(), createValueList(3)));
			}
			methods.add(method);
		}
		methods.add(createMethodWithMode(PatternMatchMode.IGNORE));
		methods.add(createMethodWithMode(PatternMatchMode.ANY_PARAMS));
		methods.add(createMethodWithMode(PatternMatchMode.NO_PARAMS));
		Collections.shuffle(methods);
		
		methodSet.addAll(methods);
		
		int i = 0;
		for(final URIPatternMethod method : methodSet) {
			if(i == 0) {
				Assert.assertTrue(PatternMatchMode.IGNORE.equals(method.getMatchMode()), "First position should have IGNORE");
				i++;
			} else if(i == 1) {
				Assert.assertTrue(PatternMatchMode.NO_PARAMS.equals(method.getMatchMode()), "Second position should have NO_PARAMS");
				i++;
			} else if(i == 2) {
				Assert.assertTrue(PatternMatchMode.ANY_PARAMS.equals(method.getMatchMode()), "Second position should have ANY_PARAMS");
				i++;
			} else {
				Assert.assertEquals(method.getMatchMode(), PatternMatchMode.SPECIFIC_PARAMS);
				Assert.assertTrue(method.getParams().size() == i);
				i++;
			}
		}
	}
}
