package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class OpticalFilterTest extends StardardBase{
    
    public OpticalFilterTest() {
        super(new OpticalFilter());
    }

    
    @Test
    public void test1() {
        double[] input={1.06123e+09,1.14359e+09,1.31557e+09,7.66742e+08,1.05298e+08,1.20995e+09,1.54454e+09,1.20059e+09,1.25381e+09,5.43089e+08,6.41286e+08};
        double[] expected={ 0.99759755939884787,972446500.00189483};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.001);
    }

    
}
