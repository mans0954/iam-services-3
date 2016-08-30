package org.openiam.base.request;

import java.util.Date;
import java.util.Set;

/**
 * Created by zaporozhec on 8/30/16.
 */
public class GetParentsRequest extends IdServiceRequest {
    private int from;
    private int size;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
