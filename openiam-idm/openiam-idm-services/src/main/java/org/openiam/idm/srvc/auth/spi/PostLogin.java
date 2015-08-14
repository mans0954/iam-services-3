package org.openiam.idm.srvc.auth.spi;

/**
 * The PostLogin interface needs to be implemented by login modules or services 
 *  that want to do post authentication processing.<br>
 *  The interface will be invoked by the Authentication service on the following events:
 *  <ul>
 *  	<li> Successful authentication 
 *  	<li> Failed authentication 
 *  	<li> During logout.
 *  </ul>
 * @author Suneet Shah
 * @version 2
 */
public interface PostLogin {

	/**
	 * Post processing on successful authentication
	 */
	void onSuccess();
	/**
	 * Post processing on failed authentication
	 */
	void onFailure();
	/**
	 *  Post processing on Logout.
	 */
	void onLogout();
}
