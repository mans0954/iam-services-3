package org.openiam.util;



/**
 * Provides a logging utility using Log4J.
 * @author suneet
 *
 *This needs to be deleted from teh codebase
 */
@Deprecated
public class LoggerUtil {
	
	/*
	 * Log INFO level message. Checks if info is enabled.
	 */
	static public void info(org.apache.log4j.Logger log, String message) {
		if (log.isInfoEnabled())
			log.info(message);
	}
	/*
	 * Log DEBUG level message. Checks if debug is enabled.
	 */
	static public void debug(org.apache.log4j.Logger log, String message) {
		if (log.isDebugEnabled())
			log.debug(message);
	}
	/*
	 * Logs erors
	 */
	static public void error(org.apache.log4j.Logger log, Object message) {
		log.error(message);
	}
	static public void error(org.apache.log4j.Logger log,  String msg, Throwable t) {
		log.error(msg, t);
	}
}

