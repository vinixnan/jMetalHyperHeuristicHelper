/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.nottingham.asap.realproblems;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vinicius
 */
public class AucMaximizationTest extends StardardBase{
    
    public AucMaximizationTest() {
        super(new AucMaximization());
    }

    
    @Test
    public void test1() {
        double[] input={ 0.0802643,0.0243648,0.0711174,0.178251,0.162306,0.204082,0.0785033,0.0786408,0.203832,0.0309569};
        double[] expected={ 1,0};
        double[] returned=this.runTest(input);
        assertArrayEquals(expected, returned, 0.0000001);
    }

    
}
