function mqttLoad() {
    let sensorTopic = "/id-esp/sensor";
// Create a client instance
    var client = new Paho.MQTT.Client('localhost', 9001, '/ws', 'frontend');

// set callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

// connect the client
    client.connect({onSuccess: onConnect}, {reconnect: true}, {keepAliveInterval: 100000});


// called when the client connects
    function onConnect() {
        // Once a connection has been made, make a subscription and send a message.
        console.log("onConnect");
        client.subscribe(sensorTopic);
        let message = new Paho.MQTT.Message("prova");
        message._setDestinationName(sensorTopic);
        client.send(message);
    }

// called when the client loses its connection
    function onConnectionLost(responseObject) {
        if (responseObject.errorCode !== 0) {
            console.log("onConnectionLost:" + responseObject.errorMessage);
        }
    }

// called when a message arrives
    function onMessageArrived(message) {
        console.log("onMessageArrived:" + message.payloadString);
    }

    return client;
}
