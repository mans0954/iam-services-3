package org.openiam.core.key.util;

import java.util.Scanner;

/**
 * Created by: Alexander Duckardt
 * Date: 30.10.12
 */
public class KmUtil {
    public static final String DEFAULT_WSDL_LOCATION  = "http://localhost:8080/openiam-esb/idmsrvc/KeyManagementWS?wsdl";
    public static String promtParameter(Scanner scanner, String promtString) {
        return promtParameter(scanner,promtString,null);
    }
    public static String promtParameter(Scanner scanner, String promtString, String defaultValue) {
        String value = null;
        do{
            System.out.println(promtString);
            value =scanner.nextLine();

            if(defaultValue!=null && !defaultValue.isEmpty() && (value==null || value.trim().isEmpty())){
                System.out.println("You did not set value. It will be set by default.");
                value=defaultValue;
            }
            if(value==null || value.trim().isEmpty()){
                System.out.println("You did not set value. Please enter not empty value.");
                value=null;
            }
        } while(value == null);
        return value;
    }
}
