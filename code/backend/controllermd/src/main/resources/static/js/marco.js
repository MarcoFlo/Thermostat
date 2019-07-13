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
    //InitialState();

    //if(idRoom!=undefined){
    //alert(room_list[currentRoom]);
    //   get_backend(currentRoom);
    //}
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
    get_backend("MainRoom");
    var xhttp_room = new XMLHttpRequest();
    xhttp_room.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            room_list = JSON.parse(xhttp_room.responseText);
            client.subscribe("/temperature/MainRoom");
            currentRoom = room_list.indexOf("MainRoom");


        }
    };
    xhttp_room.open("GET", "http://localhost:8080/setting/room/list", true);
    xhttp_room.send();
}

function requestRoomList() {
    var xhttp_room = new XMLHttpRequest();
    xhttp_room.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            room_list = JSON.parse(xhttp_room.responseText);
        }
    };
    xhttp_room.open("GET", "http://localhost:8080/setting/room/list", true);
    xhttp_room.send();
}

/**
 * Function that handle the right/left click
 *  TODO Retrive and set the current setting from GET localhost:8080/temperature/current_room_state_resource
 * @param ev
 */
function changeRoom() {
    if (currentRoom !== undefined) {
        var desired_room = rotateRoom(currentRoom, this.id);

        initialState();
        setMqttRoom(desired_room);
        get_backend(room_list[desired_room]);

    }
}



function get_backend(desired_room) {
    var xhttp_backend = new XMLHttpRequest();
    xhttp_backend.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            console.log("Entry backend");
            var obj = JSON.parse(xhttp_backend.responseText);

            //obj.isWinter = true;
            if (obj.isManual) {
                retrieve_values("Manual", "heating");
            }
            if (obj.isWinter) {
                //alert("entra");
                retrieve_values("Winter","heating");
            }
            if (obj.isSummer) {
                retrieve_values("Summer","cooling");
            }
            if (obj.isAntiFreeze) {
                retrieve_values("Winter","heating");
                retrieve_values("AntiFreeze","heating");
            }
            /*document.getElementById("demo").innerHTML = obj;*//*String separado por comas*/

        }
    };
    xhttp_backend.open("GET", "http://localhost:8080/temperature/current_room_state_resource/" + desired_room, true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp_backend.send();
}

function initialState() {
    document.getElementById("Summer").value = 0;
    document.getElementById("Summer").className = "btn btn-secondary";
    document.getElementById("Winter").value = 0;
    document.getElementById("Winter").className = "btn btn-secondary";
    document.getElementById("Manual").value = 0;
    document.getElementById("Manual").className = "btn btn-secondary m-1";
    document.getElementById("AntiFreeze").value = 0;
    document.getElementById("AntiFreeze").className = "btn btn-secondary m-1";
    nest.hvac_state = 'off';

}
function retrieve_values(stanza, state){
    if(stanza === "Winter" || stanza === "Summer"){
        document.getElementById(stanza).className = "btn btn-primary";
    }else{
        document.getElementById(stanza).className = "btn btn-primary m-1";
    }
    document.getElementById(stanza).value = 1;
    nest.hvac_state = state; 
}

