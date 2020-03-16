package uk.ac.nottingham.asap.realproblems.util;

import com.google.common.primitives.Doubles;
import java.util.List;

/**
 *
 * @author vinicius
 */
public class MatrixHelper {

    public static double[][] matrixReshape(double[][] nums, int r, int c) {
        int totalElements = nums.length * nums[0].length;
        if (totalElements != r * c || totalElements % r != 0) {
            return nums;
        }
        final double[][] result = new double[r][c];
        int newR = 0;
        int newC = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums[i].length; j++) {
                result[newR][newC] = nums[i][j];
                newC++;
                if (newC == c) {
                    newC = 0;
                    newR++;
                }
            }
        }
        return result;
    }

    public static double[][] matrixCut(double[][] nums, int from, int size) {
        //only works with array
        List<Double> arr = Doubles.asList(nums[0]);
        arr = arr.subList(from, from + size);
        double[] arrx = arr.stream().mapToDouble(i -> i).toArray();
        double[][] toReturn = new double[nums.length][nums[0].length];
        toReturn[0] = arrx;
        return toReturn;
    }

    public static double[][] vectorReshape(double[][] nums, int from, int size, int r, int c) {
        double[][] cut = MatrixHelper.matrixCut(nums, from, size);
        double[][] reshape = MatrixHelper.matrixReshape(cut, r, c);
        return reshape;
    }

}
