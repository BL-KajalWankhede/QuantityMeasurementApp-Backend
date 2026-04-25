package com.quantitymeasurement;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        Feet value1 = new Feet(1.0);
        Feet value2 = new Feet(1.0);
        boolean result = value1.equals(value2);
        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + result + ")");



    }
}
