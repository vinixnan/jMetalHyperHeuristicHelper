package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class DiskBrakeDesignTest extends StardardBase{

    public DiskBrakeDesignTest() {
        super(new DiskBrakeDesign());
    }
    
    
    @Test
    public void test1() {
        double[] input={ 8.0001e+08, 2.77648e+08, 6.57613e+08, 1.99531e+08};
        double[] expected={1,-3.74367e-35};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.0000001);
    }
    
    @Test
    public void test2() {
        double[] input={1.57155e+09,5.11576e+07,1.35593e+09,3.21643e+08};
        double[] expected={ 1,-3.11185e-35};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.0000001);
    }
    
}
