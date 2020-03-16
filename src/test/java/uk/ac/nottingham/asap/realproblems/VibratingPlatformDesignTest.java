package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class VibratingPlatformDesignTest extends StardardBase{
    
    public VibratingPlatformDesignTest() {
        super(new VibratingPlatformDesign());
    }

    @Test
    public void test1() {
        double[] input={0.161495,0.0116988,0.163608,0.0863057,0.0361168};
        double[] expected={0.763515,0.0875506};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.000001);
    }
    
}
