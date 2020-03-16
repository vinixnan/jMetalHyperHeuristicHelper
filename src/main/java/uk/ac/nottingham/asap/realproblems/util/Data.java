package uk.ac.nottingham.asap.realproblems.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author psavd Singleton
 */
public class Data {

// yacht hydrodynamics data set
    private static ArrayList<ArrayList<Double>> yacht_inputs = null;
    private static ArrayList<ArrayList<Double>> skin_inputs1 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs2 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs3 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs4 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs5 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs6 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs7 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs8 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs9 = null;
    private static ArrayList<ArrayList<Double>> skin_inputs10 = null;

    private static final double[] yacht_labels = {
        0.11, 0.27, 0.47, 0.78, 1.18, 1.82, 2.61, 3.76, 4.99, 7.16, 11.93, 20.11, 32.75, 49.49, 0.04, 0.17, 0.37, 0.66, 1.06, 1.59, 2.33, 3.29, 4.61,
        7.11, 11.99, 21.09, 35.01, 51.8, 0.09, 0.29, 0.56, 0.86, 1.31, 1.99, 2.94, 4.21, 5.54, 8.25, 13.08, 21.4, 33.14, 50.14, 0.2, 0.35, 0.65, 0.93,
        1.37, 1.97, 2.83, 3.99, 5.19, 8.03, 12.86, 21.51, 33.97, 50.36, 0.2, 0.35, 0.65, 0.93, 1.37, 1.97, 2.83, 3.99, 5.19, 8.03, 12.86, 21.51, 33.97,
        50.36, 0.12, 0.26, 0.43, 0.69, 1.09, 1.67, 2.46, 3.43, 4.62, 6.86, 11.56, 20.63, 34.5, 54.23, 0.28, 0.44, 0.7, 1.07, 1.57, 2.23, 3.09, 4.09, 5.82,
        8.28, 12.8, 20.41, 32.34, 47.29, 0.2, 0.38, 0.64, 0.97, 1.36, 1.98, 2.91, 4.35, 5.79, 8.04, 12.15, 19.18, 30.09, 44.38, 0.15, 0.32, 0.55, 0.86, 1.24,
        1.76, 2.49, 3.45, 4.83, 7.37, 12.76, 21.99, 35.64, 53.07, 0.11, 0.24, 0.49, 0.79, 1.28, 1.96, 2.88, 4.14, 5.96, 9.07, 14.93, 24.13, 38.12, 55.44, 0.07,
        0.18, 0.4, 0.7, 1.14, 1.83, 2.77, 4.12, 5.41, 7.87, 12.71, 21.02, 34.58, 51.77, 0.08, 0.26, 0.5, 0.83, 1.28, 1.9, 2.68, 3.76, 5.57, 8.76, 14.24, 23.05,
        35.46, 51.99, 0.08, 0.24, 0.45, 0.77, 1.19, 1.76, 2.59, 3.85, 5.27, 7.74, 12.4, 20.91, 33.23, 49.14, 0.08, 0.25, 0.46, 0.75, 1.11, 1.57, 2.17, 2.98, 4.42,
        7.84, 14.11, 24.14, 37.95, 55.17, 0.1, 0.23, 0.47, 0.76, 1.15, 1.65, 2.28, 3.09, 4.41, 7.51, 13.77, 23.96, 37.38, 56.46, 0.05, 0.17, 0.35, 0.63, 1.01, 1.43,
        2.05, 2.73, 3.87, 7.19, 13.96, 25.18, 41.34, 62.42, 0.03, 0.18, 0.4, 0.73, 1.3, 2.16, 3.35, 5.06, 7.14, 10.36, 15.25, 23.15, 34.62, 51.5, 0.06, 0.15, 0.34,
        0.63, 1.13, 1.85, 2.84, 4.34, 6.2, 8.62, 12.49, 20.41, 32.46, 50.94, 0.16, 0.32, 0.59, 0.92, 1.37, 1.94, 2.62, 3.7, 5.45, 9.45, 16.31, 27.34, 41.77, 60.85,
        0.09, 0.24, 0.47, 0.78, 1.21, 1.85, 2.62, 3.69, 5.07, 7.95, 13.73, 23.55, 37.14, 55.87, 0.01, 0.16, 0.39, 0.73, 1.24, 1.96, 3.04, 4.46, 6.31, 8.68, 12.39, 20.14,
        31.77, 47.13, 0.04, 0.17, 0.36, 0.64, 1.02, 1.62, 2.63, 4.15, 6, 8.47, 12.27, 19.59, 30.48, 46.66};
    
    
    private static double[] l1 = null;


    public static ArrayList<ArrayList<Double>> getYacht_inputs() {
        if (yacht_inputs == null) {
            yacht_inputs = readMatrix("dataMLProblems/yatch_inputs.csv");
        }
        return yacht_inputs;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs1() {
        if (skin_inputs1 == null) {
            skin_inputs1 = readMatrix("dataMLProblems/skin_inputs1.csv");
        }
        return skin_inputs1;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs2() {
        if (skin_inputs2 == null) {
            skin_inputs2 = readMatrix("dataMLProblems/skin_inputs2.csv");
        }
        return skin_inputs2;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs3() {
        if (skin_inputs3 == null) {
            skin_inputs3 = readMatrix("dataMLProblems/skin_inputs3.csv");
        }
        return skin_inputs3;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs4() {
        if (skin_inputs4 == null) {
            skin_inputs4 = readMatrix("dataMLProblems/skin_inputs4.csv");
        }
        return skin_inputs4;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs5() {
        if (skin_inputs5 == null) {
            skin_inputs5 = readMatrix("dataMLProblems/skin_inputs5.csv");
        }
        return skin_inputs5;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs6() {
        if (skin_inputs6 == null) {
            skin_inputs6 = readMatrix("dataMLProblems/skin_inputs6.csv");
        }
        return skin_inputs6;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs7() {
        if (skin_inputs7 == null) {
            skin_inputs7 = readMatrix("dataMLProblems/skin_inputs7.csv");
        }
        return skin_inputs7;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs8() {
        if (skin_inputs8 == null) {
            skin_inputs8 = readMatrix("dataMLProblems/skin_inputs8.csv");
        }
        return skin_inputs8;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs9() {
        if (skin_inputs9 == null) {
            skin_inputs9 = readMatrix("dataMLProblems/skin_inputs9.csv");
        }
        return skin_inputs9;
    }

    public static ArrayList<ArrayList<Double>> getSkin_inputs10() {
        if (skin_inputs10 == null) {
            skin_inputs10 = readMatrix("dataMLProblems/skin_inputs10.csv");
        }
        return skin_inputs10;
    }

    public static double[] getYacht_labels() {
        return yacht_labels;
    }

    public static double[] getL1() {
        if (l1 == null) {
            ArrayList<ArrayList<Double>>  aux = readMatrix("dataMLProblems/l1.csv");
            l1=aux.get(0).stream().mapToDouble(Double::doubleValue).toArray();
        }
        return l1;
    }
    
    public static ArrayList<ArrayList<Double>> readMatrix(String filename) {
        ArrayList<ArrayList<Double>> toReturn = new ArrayList<>();
        Reader in;
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream(filename);
            in = new InputStreamReader(is);
            CSVParser csvParser = new CSVParser(in, CSVFormat.DEFAULT
                    .withTrim());
            Iterable<CSVRecord> records = csvParser.getRecords();

            for (CSVRecord record : records) {
                ArrayList<Double> aux = new ArrayList<>();
                for (int j = 0; j < record.size(); j++) {
                    aux.add(Double.parseDouble(record.get(j)));
                }
                toReturn.add(aux);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;

    }

    public static void main(String[] args) {
        
        getYacht_inputs();
        getSkin_inputs1();
        getSkin_inputs2();
        getSkin_inputs3();
        getSkin_inputs4();
        getSkin_inputs5();
        getSkin_inputs6();
        getSkin_inputs7();
        getSkin_inputs8();
        getSkin_inputs9();
        getSkin_inputs10();
    }

}
