package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class WeldedBeamDesignTest extends StardardBase{
    
    public WeldedBeamDesignTest() {
        super(new WeldedBeamDesign());
    }

    @Test
    public void test1() {
        double[] input={0.157155,0.00511576,0.135593,0.0321643};
        double[] expected={ 0.999853,1};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.000001);
    }
    
}
