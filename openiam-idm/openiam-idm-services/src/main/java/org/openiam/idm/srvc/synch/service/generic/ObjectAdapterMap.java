package org.openiam.idm.srvc.synch.service.generic;

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * Provides a map of Adapters for each type of Object.
 * User: suneetshah
 * Date: 3/27/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("adapterMap")
public class ObjectAdapterMap {
	
	@PostConstruct
	public void init() {
		adapterMap = new HashMap<String, String>();
		adapterMap.put("GROUP", "/sync/generic/GroupAdapter.groovy");
	}
	
    private Map<String, String> adapterMap;

    public String getHandlerName(String key) {
        return adapterMap.get(key);

    }
}
