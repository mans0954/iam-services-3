package org.openiam.imprt.constant;

/**
 * Class contains constant for implementation "order by" method for JDBC parser<br>
 * 
 * @author D. Zaporozhec
 * */
public enum OrderByType {

    ASC("ASC"), DESC("DESC");

    private String value;

    OrderByType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
