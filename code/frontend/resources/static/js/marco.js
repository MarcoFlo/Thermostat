var currentRoom;
var client;


function mqttLoad() {
    let sensorTopic = "/id-esp/sensor";
// Create a client instance
    client = new Paho.MQTT.Client('localhost', 9001, '/ws', 'frontend');

// set callback handlers
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

// connect the client
    client.connect({onSuccess: onConnect}, {reconnect: true}, {keepAliveInterval: 100000});

    return client;
}


// called when the client connects
function onConnect() {
    // Once a connection has been made, make a subscription and send a message.
    console.log("Client connecte");
    // client.subscribe(sensorTopic);
    // let message = new Paho.MQTT.Message("prova");
    // message._setDestinationName(sensorTopic);
    // client.send(message);
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
        console.log("onConnectionLost:" + responseObject.errorMessage);
    }
}



function handleNewThermostatClientResource(message) {
    var thermostatClientResource = JSON.parse(message.payloadString);
    console.log(thermostatClientResource);
    nest.target_temperature = thermostatClientResource.desiredTemperature
    nest.ambient_temperature = thermostatClientResource.currentApparentTemperature;

}

function setMqttRoom(idRoom: String)
{
    client.unsubscribe({topic: "/temperature/"+currentRoom});
    client.subscribe("/temperature/"+idRoom, {onSuccess: handleNewThermostatClientResource});
    currentRoom = idRoom;
}

// called when a message arrives
function onMessageArrived(message) {
    console.log("onMessageArrived:" + message.payloadString);
}
