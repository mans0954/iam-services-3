package org.openiam.imprt;

import org.openiam.idm.srvc.key.service.Exception;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.DataHolder;

import java.io.InputStream;

public class Import{
    private static final String CONF_PATH_ARG_NAME="confPath";
    private static final String SYNC_CONF_ID_ARG_NAME="syncConfigId";
    private static final String WEB_SERVER_URL="webServerURL";

    private static final String CONF_PATH_ARG_NAME_PREFIX;
    private static final String SYNC_CONF_ID_ARG_NAME_PREFIX;
    private static final String WEB_SERVER_URL_PREFIX;

    static {
        CONF_PATH_ARG_NAME_PREFIX = String.format("--%s=", CONF_PATH_ARG_NAME);
        SYNC_CONF_ID_ARG_NAME_PREFIX = String.format("--%s=", SYNC_CONF_ID_ARG_NAME);
        WEB_SERVER_URL_PREFIX = String.format("--%s=", WEB_SERVER_URL);
    }


    public static void main(String[] args){
        // load props for database mapping
        if(args==null || args.length<=0){
            throw new IllegalArgumentException("Invalid or empty arguments");
        }
        for(String param:args){

            if (param.contains(CONF_PATH_ARG_NAME_PREFIX)){
                parseArg(param, CONF_PATH_ARG_NAME_PREFIX, CONF_PATH_ARG_NAME, ImportPropertiesKey.CONF_PATH);
            } else if (param.contains(SYNC_CONF_ID_ARG_NAME_PREFIX)){
                parseArg(param, SYNC_CONF_ID_ARG_NAME_PREFIX, SYNC_CONF_ID_ARG_NAME, ImportPropertiesKey.SYNC_CONFIG);
            } else if (param.contains(WEB_SERVER_URL_PREFIX)){
                parseArg(param, WEB_SERVER_URL_PREFIX, WEB_SERVER_URL, ImportPropertiesKey.WEB_SERVER_URL);
            }
        }

        ImportProcessor processor = new ImportProcessor();
        try {
            processor.start();
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseArg(String arg, String namePrefix, String name, ImportPropertiesKey key) {
        String value =arg.substring(namePrefix.length());
        if(value==null || value.trim().isEmpty()){
            throw new IllegalArgumentException(String.format("%s must be specified", name));
        }
        DataHolder.getInstance().setProperty(key, value);
    }


}