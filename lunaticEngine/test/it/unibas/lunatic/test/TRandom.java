package it.unibas.lunatic.test;

import java.util.Random;
import junit.framework.TestCase;

public class TRandom extends TestCase {
    
    public TRandom(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void test() {
        Random random = new Random();
        int less = 0;
        int more = 0;
        for (int i = 0; i < 100000; i++) {
            double number = random.nextDouble();
            if (number < 0.5) 
                less++; 
            else 
                more++;
        }
        System.out.println("Less: " + less);
        System.out.println("More: " + more);
    }
}
