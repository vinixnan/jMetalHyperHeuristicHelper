package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class NeuralNetDoublePoleBalancingTest extends StardardBase{
    
    public NeuralNetDoublePoleBalancingTest() {
        super(new NeuralNetDoublePoleBalancing());
    }

    
    @Test
    public void test1() {
        double[] input={ 0.0802643,0.0243648,0.0711174,0.178251,0.162306,0.204082,0.0785033,0.0786408,0.203832,0.0309569,0.0432485,0.0596544,0.0762946,0.0321338,0.189782,0.13053,0.114202,0.14381,0.182274,0.149411,0.080169,0.118849,0.166016,0.0738283};
        double[] expected={0.9989,1};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.0000001);
    }
    
}
