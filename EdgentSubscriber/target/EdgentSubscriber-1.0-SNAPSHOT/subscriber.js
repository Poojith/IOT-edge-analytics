/*
 * Project 3 solution(Part 4) for subscribing real-time temperature information from the edge device.
 * @author Poojith Jain(poojithj) 
 * Date : March 5, 2017        
*/
        
// Create the temperature variable to which the retrieved temperature value from JSON is assigned.
var temperature;
// Create a new Client object with the broker's hostname, port and the client ID.
// The andrewID is concatenated with a random number for the client ID in order to avoid repetition.
var client =  new Paho.MQTT.Client("broker.hivemq.com", 8000, "poojith" + parseInt(Math.random() * 100, 10));

 //Gets called in the event if the WebSocket/MQTT connection is disconnected for any reason.
 client.onConnectionLost = function (responseObject) {
     // Alert the user about the lost connection with an error message.
     alert("Connection lost: " + responseObject.errorMessage); 
 };

  //Gets called whenever a new message is received for a particular subscription.
 client.onMessageArrived = function (message) {
     
    console.log(message.payloadString); // Log the payload for the purpose of debugging.
    jsonString = JSON.parse(message.payloadString); // Parse the JSON String.
    var context = jsonString['@context']; // Retrieve the context for the JSON-LD formatted data.
    var type = jsonString['@type']; // Retrieve the type of the entity whose data is embedded in the JSON-LD representation.
    var unit = jsonString.unit; // Retrieve the unit of the temperature information that is sent as part of the JSON- LD format.
    var url = jsonString.url;   // Retrieve the URL associated for the entity embedded in the JSON- LD format.
    var value = jsonString.temperature; // Retrieve the value of the generated temperature.
    temperature = parseFloat(Math.round(value * 100) / 100).toFixed(2); // Format the temperature value to 2 decimal places.
    
    // Output the parsed JSON information.
    var parsedJSON = "Context: " + context + "<br> Type: " + type + "<br> Unit: " + unit + 
            "<br> URL: " + url + "<br> Temperature value: " + temperature;
    
    // Get the HTML5 element and update the parsed JSON information. 
    document.getElementById('jsonContent').innerHTML = parsedJSON;
    
    displayTemperature(); // Call displayTemperature() for displaying the results of thetemperature levels.
};
 

 //Options associated with connect. 
 var options = {
     timeout: 3,
     //Gets called when the connection is successfully established
     onSuccess: function () {
         //Alert the user about the successful connection.
       alert("You are connected. You will now be able to subscribe and receive messages from the edge devices.");
       // Subscribe to the topic of the temperature levels generated from the edge device with a QoS value of 0.
       client.subscribe('edgent/sensorTemperature', {qos: 0});  
     },
     
     //Gets called if the connection does not get established for some reason
     onFailure: function (message) {
         // Alert the user about the failure to connect.
         alert("Sorry, connection failed: " + message.errorMessage);
         // Log the failed result with appropriate message in order to know what went wrong.
         console.log("Connection failed: " + message.errorMessage);
     }
 };
 
 // When the window loads, the anonymous JavaScript function executes, which connects the client to the broker.
window.onload = function () {
    client.connect(options); // Connect the client by calling connect(), and call the function expression, 'options'
};

// When the window is about to unload(close), exitFunction is called, which disconnects the client from the broker.
window.onbeforeunload = exitFunction;
function exitFunction() {
    client.disconnect(); // Disconnect the client by calling disconnect()
}

