package org.openiam.spml2.spi.salesforce.exception;

import com.sforce.soap.partner.SaveResult;

public class SalesForcePersistException extends Exception {

	private SaveResult saveResult = null;
	
	public SalesForcePersistException(final String msg) {
		super(msg);
	}
	
	public SalesForcePersistException(final SaveResult saveResult) {
		this.saveResult = saveResult;
	}
	
	@Override
	public String toString() {
		if(saveResult != null) {
			return String.format("Save Failed: %s", saveResult);
		} else {
			return super.toString();
		}
	}
}
