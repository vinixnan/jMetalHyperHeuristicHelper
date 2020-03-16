package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class HydroDynamicsTest extends StardardBase{
    
    public HydroDynamicsTest() {
        super(new HydroDynamics());
    }

    
    @Test
    public void test1() {
        double[] input={ 0.080264299999999997,0.024364799999999999,0.071117399999999997,0.17825099999999999,0.16230600000000001,0.20408200000000001};
        double[] expected={ 0.68486144129679438,0.61267732847660106};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.0000001);
    }

    
}
