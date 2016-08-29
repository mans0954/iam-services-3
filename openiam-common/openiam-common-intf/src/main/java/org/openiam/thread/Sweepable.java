package org.openiam.thread;

public interface Sweepable {

	/**
	 * Placeholder for Spring to call via Quartz.  Required for @Transactional sweeper methods.
	 */
	void sweep();
}
