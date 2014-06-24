package org.openiam.connector.gapps;

public class GoogleUtils {
	public static String makeGoogleId(String id, String domain) {
		String result = "";
		if (id.contains("@")) {
			result = id.toLowerCase();
		} else {
			result = id.toLowerCase() + "@" + domain;
		}
		return result;
	}
}
