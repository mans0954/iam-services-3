package org.openiam.connector.gapps.command.base;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gdata.data.codesearch.Match;

public class GoogleGamCommand implements Comparable<GoogleGamCommand> {

	protected final Log log = LogFactory.getLog(this.getClass());
	private final static Pattern displayOrderPattern = Pattern
			.compile("(do=)([0-9]*)");
	private final static Pattern methodsPattern = Pattern
			.compile("(methods=)([0-9a-zA-Z,]*)");
	private final static Pattern commandPattern = Pattern
			.compile("(command=)([0-9a-zA-Z, @._]*)");
	private int displayOrder;
	private String command;
	private List<String> methods;

	public GoogleGamCommand(String value) {
		if (StringUtils.isNotBlank(value)) {
			Matcher disp = displayOrderPattern.matcher(value);
			Matcher com = commandPattern.matcher(value);
			Matcher meth = methodsPattern.matcher(value);
			while (disp.find()) {
				try {
					displayOrder = Integer.valueOf(disp.group(2));
				} catch (NumberFormatException ex) {
					displayOrder = 1;
				}
				log.debug("GAM command: display order=" + displayOrder);
			}
			while (com.find()) {
				command = com.group(2);
				log.debug("GAM command: command=" + command);
			}
			while (meth.find()) {
				String meths = meth.group(2);
				if (StringUtils.isNotBlank(meths)) {
					try {
						methods = Arrays.asList(meths.split(","));
					} catch (Exception ex) {
						log.error("GAM command! Methods bad format or empty "
								+ ex);
					}
				}
				log.debug("GAM command: methods=" + methods);
			}
		} else {
			log.warn("GAM Value is empty!");
		}
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getMethods() {
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}

	@Override
	public int compareTo(GoogleGamCommand o) {
		if (o == null) {
			return 1;
		}
		return Integer.compare(this.displayOrder, o.getDisplayOrder());
	}
}
