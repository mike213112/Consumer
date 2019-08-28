/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.so1.test;

/**
 *
 * @author mmendez
 */
public class ConsumerMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestConsumerThread thread01 = new TestConsumerThread("01", 500);
        TestConsumerThread thread02 = new TestConsumerThread("02", 500);
        TestConsumerThread thread03 = new TestConsumerThread("03", 500);
        TestConsumerThread thread04 = new TestConsumerThread("04", 500);
        TestConsumerThread thread05 = new TestConsumerThread("05", 500);
        TestConsumerThread thread06 = new TestConsumerThread("06", 500);
        TestConsumerThread thread07 = new TestConsumerThread("07", 500);
        TestConsumerThread thread08 = new TestConsumerThread("08", 500);
        TestConsumerThread thread09 = new TestConsumerThread("09", 500);
        TestConsumerThread thread10 = new TestConsumerThread("10", 500);

        thread01.start();
        thread02.start();
        thread03.start();
        thread04.start();
        thread05.start();
        thread06.start();
        thread07.start();
        thread08.start();
        thread09.start();
        thread10.start();
    }

}
