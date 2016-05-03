package org.openiam.imprt.query.expression;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.DataHolder;

public class Column {
    private ImportPropertiesKey columnKey;
    private ColumnAttribute attribute;
    private String column;

    public ImportPropertiesKey getColumnKey() {
        return columnKey;
    }

    @Override
    public String toString() {
        String result = "";
        if (attribute == null) {
            result = column;
        } else {
            switch (attribute) {
            case MAX:
                result = "MAX(" + column + ")";
                break;
            case MIN:
                result = "MIN(" + column + ")";
            default:
                break;
            }
        }
        return result;
    };

    public String getColumn() {
        return column;
    }

    public ColumnAttribute getAttribute() {
        return attribute;
    }

    public Column(ImportPropertiesKey columnkey, ColumnAttribute attribute) {
        super();
        this.columnKey = columnkey;
        this.column = DataHolder.getInstance().getProperty(columnkey);
        this.attribute = attribute;
    }

    public Column(ImportPropertiesKey columnkey) {
        super();
        this.columnKey = columnkey;
        this.column = DataHolder.getInstance().getProperty(columnkey);
        this.attribute = null;
    }


}
