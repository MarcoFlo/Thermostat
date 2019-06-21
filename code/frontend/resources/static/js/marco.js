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

    //debug todo
    setMqttRoom("Kitchen");
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
    console.log(message.payloadString);

    var thermostatClientResource = JSON.parse(message.payloadString);
    console.log(thermostatClientResource);
    nest.target_temperature = thermostatClientResource.desiredTemperature;
    nest.ambient_temperature = thermostatClientResource.currentApparentTemperature;

}

function setMqttRoom(idRoom) {
    client.unsubscribe("/temperature/" + currentRoom);
    client.subscribe("/temperature/" + idRoom);
    currentRoom = idRoom;
}

// called when a message arrives
function onMessageArrived(message) {
    console.log("onMessageArrived:\n\tTopic -> " + message._getDestinationName() + "\n\tMessage -> " + message.payloadString);

    switch (message._getDestinationName()) {
        case "/temperature/" + currentRoom: {
            var thermostatClientResource = JSON.parse(message.payloadString);
            nest.target_temperature = thermostatClientResource.desiredTemperature;
            nest.ambient_temperature = thermostatClientResource.currentApparentTemperature;
            break;
        }


    }
}
