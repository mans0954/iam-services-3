package org.openiam.authmanager.common;

import java.util.HashMap;
import java.util.Set;

public class HashMapResponse {

    private HashMap<String, SetStringResponse> hashMapResp;

    public HashMapResponse(HashMap<String, Set<String>> param) {
        HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();

    }

    public HashMap<String, SetStringResponse> getHashMapResp(){
        return this.hashMapResp;
    }

    public void setHashMapResp(HashMap<String, SetStringResponse> hashMapResp) {
        this.hashMapResp = hashMapResp;
    }
}
