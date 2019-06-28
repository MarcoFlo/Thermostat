var currentRoom;
var client;
var room_list;

function mqttLoad() {
    client = new Paho.MQTT.Client('localhost', 9001, '/ws', 'frontend');

    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

    client.connect({onSuccess: onConnect}, {reconnect: true}, {keepAliveInterval: 100000});
    return client;
}

function onConnect() {
    console.log("MQTT Client connected");
    requestRoom();
}

function onConnectionLost(responseObject) {
    if (responseObject.errorCode !== 0) {
        console.log("onConnectionLost:" + responseObject.errorMessage);
    }
}

function setMqttRoom(idRoom) {
    client.unsubscribe("/temperature/" + room_list[currentRoom]);
    client.subscribe("/temperature/" + room_list[idRoom]);
    currentRoom = idRoom;
    document.getElementById("room_name").innerText = room_list[currentRoom];
}

function onMessageArrived(message) {
    switch (message._getDestinationName()) {
        case "/temperature/" + room_list[currentRoom]: {
            var thermostatClientResource = JSON.parse(message.payloadString);
            if (thermostatClientResource.desiredTemperature !== -1)
                nest.target_temperature = thermostatClientResource.desiredTemperature;
            nest.ambient_temperature = thermostatClientResource.currentApparentTemperature;
            break;
        }
    }
}

/**
 *  Initial request for the list of available room
 *  TODO Retrive and set the current setting from GET localhost:8080/temperature/current_room_state_resource
 */
function requestRoom() {
    var xhttp_room = new XMLHttpRequest();
    xhttp_room.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            room_list = JSON.parse(xhttp_room.responseText);
            client.subscribe("/temperature/MainRoom");
            currentRoom = room_list.indexOf("MainRoom");
            document.getElementById("room_name").innerText = "MainRoom";
        }
    };
    xhttp_room.open("GET", "http://localhost:8080/setting/room", true);
    xhttp_room.send();
    xhttp_room.close
}

/**
 * Function that handle the right/left click
 *  TODO Retrive and set the current setting from GET localhost:8080/temperature/current_room_state_resource
 * @param ev
 */
function changeRoom(ev) {
    if (currentRoom !== undefined) {
        var desired_room;
        switch (ev.target.id) {
            case "right_arrow" : {
                console.log("right arrow");
                desired_room = (currentRoom + 1) % room_list.length;
            }
                break;
            case "left_arrow" : {
                if (currentRoom === 0)
                    desired_room = room_list.length - 1;
                else
                    desired_room = currentRoom - 1;
                desired_room = desired_room % room_list.length;
            }
                break;
        }
        setMqttRoom(desired_room);
    }
}
