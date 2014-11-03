package org.openiam.idm.srvc.synch.srcadapter;

public class LastRecordTime {
    private Double mostRecentRecord;
    private String generalizedTime;

    public Double getMostRecentRecord() {
        return mostRecentRecord;
    }

    public void setMostRecentRecord(Double mostRecentRecord) {
        this.mostRecentRecord = mostRecentRecord;
    }

    public String getGeneralizedTime() {
        return generalizedTime;
    }

    public void setGeneralizedTime(String generalizedTime) {
        this.generalizedTime = generalizedTime;
    }
}