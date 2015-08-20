package org.openiam.model;

import com.hazelcast.nio.Address;

public class Member {

	private Address address;
	
	public Member() {}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
}
