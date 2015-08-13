package org.openiam.idm.parser.csv;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.lang.StringUtils;

import java.io.*;

/*
 * The class is used to add support of Mac csv-files to org.apache.commons.csv.CSVParser
 */
public class CSVHelper {

    private InputStream input;
    private String charsetName;
    private CSVStrategy strategy;

    public CSVHelper(InputStream input) {
        this(input, null, null);
    }

    public CSVHelper(InputStream input, String charsetName)  {
        this(input, charsetName, null);
    }

    public CSVHelper(InputStream input, CSVStrategy strategy)  {
        this(input, null, strategy);
    }

    public CSVHelper(InputStream input, String charsetName, CSVStrategy strategy) {
        this.input = input;
        this.charsetName = charsetName;
        this.strategy = strategy;
    }

    public String[][] getAllValues() throws java.io.IOException {

        Reader reader = null;

        try {
            reader = (charsetName != null)
                    ? new CSVStreamReader(input, charsetName)
                    : new CSVStreamReader(input);

            CSVParser parser = (strategy != null)
                    ? new CSVParser(reader, strategy)
                    : new CSVParser(reader);

            String[][] values = parser.getAllValues();

            // ret rid of all empty rows
            int cntEmpty = countEmptyRows(values);
            if (cntEmpty == 0) {
                return values;
            } else {
                String[][] result = new String[values.length - cntEmpty][];
                int ctr = 0;
                for(String[] row : values) {
                    if (row.length == 0 || (row.length == 1 && StringUtils.isEmpty(row[0]))) {
                        result[ctr++] = row;
                    }
                }
                return result;
            }

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int countEmptyRows(String[][] values) {
        int ctr = 0;
        for(String[] row : values) {
            if (row.length == 0 || (row.length == 1 && StringUtils.isEmpty(row[0]))) {
                ++ctr;
            }
        }
        return ctr;
    }

    private static class CSVStreamReader extends InputStreamReader {

        public CSVStreamReader(InputStream in) {
            super(in);
        }

        public CSVStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
            super(in, charsetName);
        }

        private boolean isCR = false;

        @Override
        public int read(char cbuf[], int off, int len) throws IOException {
            int result = super.read(cbuf, off, len);
            if (result >= 0) {
                int last = off + result - 1;
                for(int i = off; i < last; ++i) {
                    if (cbuf[i] == '\r' && cbuf[i+1] != '\n') {
                        cbuf[i] = '\n';
                        isCR = true;
                    }
                }
                if (cbuf[last] == '\r' && isCR) {
                    cbuf[last] = '\n';
                }
            }
            return result;
        }

    }

}
