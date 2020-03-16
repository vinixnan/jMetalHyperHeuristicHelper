package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class HeatExchangerTest extends StardardBase{
    
    public HeatExchangerTest() {
        super(new HeatExchanger());
    }

    @Test
    public void test1() {
        double[] input={0.157155,0.00511576,0.135593,0.0321643,0.0401144,0.143466,0.155932,0.0339019,0.0538483,0.0490077,0.151864,0.200746,0.054917,0.149764,0.0793055,0.1216};
        double[] expected={0.0941242,0.955967};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.000001);
    }
    
}
