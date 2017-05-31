/*
 *  Project 3 solution(Part 1) for simulating and filtering temperature values from a sensor.
 *  @author Poojith Jain(poojithj)
 *  Date : March 5, 2017 
 */

package edgent_part1;

import java.util.concurrent.TimeUnit;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

/*
 * A class to read and filter temperature levels out of the simulated stream of sensor readings.
 */
public class TempSensorApplication {
    public static void main(String[] args) throws Exception {
        // Create sensor object to read temperature readings.
        TempSensor sensor = new TempSensor();
        // Create object of DirectProvider that contains information on how and where the Edgent application will run.
        DirectProvider dp = new DirectProvider();
        // Topology is a container that describes the structure of the application.
        // Create an instance of topology using the object of DirectProvider.
        Topology topology = dp.newTopology("testTopology");
        
        // A supplier function is provided along with a time parameter to poll().
        // This specifies how frequently readings should be taken.   
        TStream<Double> tempReadings = topology.poll(sensor, 1, TimeUnit.MILLISECONDS);
        
        // Filter out expected readings which are above 50 degrees and below 80 degrees.
        TStream<Double> filteredReadings = tempReadings.filter(reading -> reading < 50 || reading > 80);
        
        
        // Prints each tuple that passes through the stream using .toString().
        filteredReadings.print();
        // Runs the application directly within the current virtual machine.
        dp.submit(topology);
    }   
}
