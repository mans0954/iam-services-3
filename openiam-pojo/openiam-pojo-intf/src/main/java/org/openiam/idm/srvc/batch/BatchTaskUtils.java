package org.openiam.idm.srvc.batch;


public class BatchTaskUtils {

    public static final String EVERY_1_MIN_EXPRESSION = "0 0/1 * * * ?";
    public static final String EVERY_5_MIN_EXPRESSION = "0 0/5 * * * ?";
    public static final String EVERY_15_MIN_EXPRESSION = "0 0/15 * * * ?";
    public static final String EVERY_60_MIN_EXPRESSION = "0 0/60 * * * ?";
    public static final String EVERY_NIGHTLY_EXPRESSION = "0 0 12 * * ?";

}
