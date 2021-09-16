package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/16/21 at  6:45 AM
 */
public class FormattedStem extends FormattedStems<String> {
    @Override
    protected String formatEntry(Object x) {
        return x.toString();
    }

    public String put(int rowNumber, int colNumber, Object object) {
        String entry = super.put(rowNumber, colNumber, object);
        maxLength = Math.max(maxLength, entry.length());
        if (minLength == -1) {
            minLength = maxLength;
        } else {
            minLength = Math.min(minLength, entry.length());
        }
        return entry;
    }

    public String get(int row, int column) {
        return null;
    }

    int maxLength = 0;

    public int maxLength() {
        return maxLength;
    }

    int minLength = -1;

    public int minLength() {
        return minLength;
    }


    /**
     * Takes a row and creates the individual elements in it. These then can
     * be just glommed together to make the whole row with whatever spacing or
     * indenting that is needed.
     * @param rowNumber
     * @return
     */
    public List<String> formatRow(int rowNumber, int maxColWidth) {
        List<String> formattedRow = new ArrayList<>();
        if (!hasRow(rowNumber)) {
            return formattedRow;
        }
        List<String> rows = getRow(rowNumber);
        int colWidth = maxColWidth;
        if(colWidth <= 0){
                colWidth = maxLength() + 1;
            }

        for (int j = 0; j < rows.size(); j++) {
            String r = rows.get(j);
            if(colWidth < r.length()){
                r = r.substring(0, colWidth);
            }
            formattedRow.add(StringUtils.justify(r, colWidth, true));
        }
        return formattedRow;
    }
     String colSeparator = " | ";
    public void print(PrintStream printStream, int offset) {
        int colWidth = maxLength() + 1;
        for (int i = 0; i < entries.size(); i++) {
            List<String> row = formatRow(i, -1);
            String rowString = colSeparator;
            for (int j = 0; j < row.size(); j++) {
                rowString = rowString +  row.get(j) + colSeparator;
            }

            printStream.println(StringUtils.getBlanks(offset) + rowString);
        }
    }

    public static void main(String[] args) {
        int rowCount = 5;
        int colCount = 6;
        FormattedStem formattedStem = populateTest(rowCount, colCount);
        formattedStem.print(System.out, 5);
        System.out.println("max length = " + formattedStem.maxLength + ", min length = " + formattedStem.minLength);
    }

    static Random random = new Random();

    public static FormattedStem populateTest(int rowCount, int colCount) {
        int stringLength = 8;
        FormattedStem formattedStem = new FormattedStem();

        byte[] rString = new byte[stringLength];
        int lastInt = Math.abs(random.nextInt());
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {

                switch ((lastInt) % 4) {
                    case 0:
                        formattedStem.put(row, col, random.nextInt(100000));
                        break;
                    case 1:
                        formattedStem.put(row, col, random.nextDouble());
                        break;
                    case 2:
                        formattedStem.put(row, col, 0 == random.nextInt() % 2);
                        break;
                    case 3:
                        random.nextBytes(rString);
                        formattedStem.put(row, col, Base64.getEncoder().encodeToString(rString));
                        break;
                }
                lastInt = Math.abs(random.nextInt());
            }
        }
        return formattedStem;
    }
}
