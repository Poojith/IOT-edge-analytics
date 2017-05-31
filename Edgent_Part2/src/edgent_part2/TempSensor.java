/*
 *  Project 3 solution(Part 2) with modified temperature values in appropriate JSON-LD format.
 *  @author Poojith Jain(poojithj)
 *  Date : March 5, 2017 
 */

package edgent_part2;

import java.util.Random;
import org.apache.edgent.function.Supplier;

/*
 * A class that simulates the generation of temperature levels from a sensor.
 * Every time get() is called, TempSensor generates a temperature reading.
 */
public class TempSensor implements Supplier<Double> {

    double currentTemp = 65.0; // Initialize current temperature to a set value of 65.0.
    Random rand; // Declare object for generating random values.

    TempSensor() {
        // Initiliaze the random object when the TempSensor class is instantiated via the default constructor.
        rand = new Random();
    }

    // A method that simulates temperature levels by generating random double values.
    @Override
    public Double get() {
        // Change the current temperature some random amount.
        double newTemp = rand.nextGaussian() + currentTemp;
        // Assign the newly created value to the current temperature.
        currentTemp = newTemp;
        // Return the new randomized temperature.
        return currentTemp;
    }
}
