package org.openiam.base;

public class Tuple<K,V> {

	private K key;
	private V value;
	
	public Tuple(){}
	
	public Tuple(final K key, final V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	
}
