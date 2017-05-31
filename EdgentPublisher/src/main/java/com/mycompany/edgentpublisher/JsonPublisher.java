/*
 *  Project 3 solution(Part 3) for publishing real-time temperature information from the edge device.
 *  @author Poojith Jain(poojithj) 
 *  Date : March 5, 2017 
 */

package com.mycompany.edgentpublisher;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;
import org.json.simple.JSONObject;
import org.apache.edgent.connectors.mqtt.MqttStreams;

/*
 * A class to publish temperature levels from a simulated edge device to a remote MQTT broker.
 */
public class JsonPublisher {

    public static void main(String[] args) {

        // Initialize the topic of the publication.
        String topic = "edgent/sensorTemperature";
        // Initialize the level of QoS to zero.
        int qos = 0;
        // Initialize the URL of the remote MQTT broker(HiveMQ) with the port number.
        String url = "tcp://broker.hivemq.com:1883";
        // Generate a random number to concatenate with the client ID in order to avoid connection problems with repeated IDs.
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 9 + 1);
        // Initialize the client ID by concatenating andrewID with the randomly generated number.
        String clientId = "poojithj" + randomNumber;

        // Create sensor object to read temperature readings.
        TempSensor sensor = new TempSensor();
        // Create object of DirectProvider that contains information on how and where the Edgent application will run.
        DirectProvider dp = new DirectProvider();
       
        // Topology is a container that describes the structure of the application.
        // Create an instance of topology using the object of DirectProvider.
        Topology topology = dp.newTopology("publisherTopology");

        // Instantiate the MQTTStreams object with topology, URL, and the client ID.
        // MqttStreams is a connector to thw MQTT broker for publishing and subscribing to topics.
        MqttStreams mqtt = new MqttStreams(topology, url, clientId);

        // A supplier function is provided along with a time parameter to poll().
        // This specifies how frequently readings should be taken.
        TStream<Double> tempReadings = topology.poll(sensor, 500, TimeUnit.MILLISECONDS);
        // Filter out expected readings which are above 60 degrees and below 70 degrees.
        TStream<Double> filteredReadings = tempReadings.filter(reading -> reading < 60 || reading > 70);

        // Create JSON string in JSON-LD format for the filtered temperature readings.
        TStream<String> json = filteredReadings.map(tuple -> {
            // Create JSON object for the filtered readings.
            JSONObject jsonObject = new JSONObject();
            // Add the JSON key-value pair for 'context' to define the short-hand names in the JSON-LD representation.
            // Context facilitates the mapping of terms, i.e., properties with associated values in the JSON document.
            jsonObject.put("@context", "http://schema.org/");
            // Add the JSON key-value pair for 'type' in order to specify the type of the entity whose data is embedded in the JSON-LD representation.
            jsonObject.put("@type", "Sensor");
            // Add the JSON key-value pair for 'unit' in order to define the unit of the data (for temperature) that is being sent.
            jsonObject.put("unit", "fahrenheit");

            // Add the JSON key-value pair to use Schema.org's url property.
            // Associate the sensor entity with a particular web identifier, URL.
            jsonObject.put("url", "http://www.sensorInfo.com/1234");

            // Add the JSON key-value pair for 'temperature' in order to send the unexpected temperature levels from the sensor.
            jsonObject.put("temperature", tuple);
            // JSON.simple library, by default, escapes every slash to void issues with HTML. 
            // Therefore, replace the escaped characters for the encoded slashes.
            String jsonString = jsonObject.toString().replace("\\", "");
            // Return the updated string representation of the JSON object.
            return jsonString;
        });

        // Publish to the HiveMQ broker by passing the previously populated JSON string, topic of the publication and the level of QoS.
        // The fourth boolean parameter of false is to specify that values need not be retained in the broker.
        mqtt.publish(json, topic, qos, false);
        
        // Print the JSON results for the purpose of debugging.
        json.print();
        
        // Run the application directly within the current virtual machine.
        dp.submit(topology);
    }
}
