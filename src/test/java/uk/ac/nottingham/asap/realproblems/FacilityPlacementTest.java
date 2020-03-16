package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class FacilityPlacementTest extends StardardBase{
    
    public FacilityPlacementTest() {
        super(new FacilityPlacement());
    }

    
    @Test
    public void test1() {
        double[] input={ 0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5,0.5};
        double[] expected={ 0.887445,0.985179};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.000001);
    }

    
}
