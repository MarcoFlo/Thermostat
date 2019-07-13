var wifiMap = new Map();


function requestWifiList() {
    document.getElementById("successful").style.display = "none";
    document.getElementById("fail").style.display = "none";
    var xhttp_wifi = new XMLHttpRequest();
    xhttp_wifi.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            obj = JSON.parse(xhttp_wifi.responseText);
            wifiMap = createMapFromWifiList(obj);
            console.log(wifiMap);
            var capa = document.getElementById("options-wifi");
            while (capa.firstChild) {
                capa.removeChild(capa.firstChild);
            }
            for (var i = 0; i < obj.length; i++) {
                var div = document.createElement("div");
                div.setAttribute("class", "d-flex flex-row p-1");

                var button = document.createElement("button");
                button.setAttribute("type", "button");
                button.setAttribute("class", "btn btn-secondary btn-block");
                button.innerHTML = obj[i].essid;
                button.setAttribute("id", obj[i].essid);
                button.addEventListener("click", wifiSelection);

                div.appendChild(button);
                capa.appendChild(div);
            }

        }
    };
    xhttp_wifi.open("GET", "http://localhost:8080/setting/wifi/list", true);
    xhttp_wifi.send();
}


function connectWifi() {
    document.getElementById("successful").style.display = "none";
    document.getElementById("fail").style.display = "none";
    var wifi_selected;
    var children = document.getElementById("options-wifi").children;
    for (var i = 0; i < children.length; i++) {
        if (children[i].children[0].classList.contains("btn-primary")) {
            wifi_selected = wifiMap.get(children[i].children[0].id);
        }
    }

    wifi_selected.netPassword = null;
    if (!wifi_selected.isKnown)
        wifi_selected.netPassword = document.getElementById("keyboardInput").value;

    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var res = xhr.responseText;
            if(res === "true"){
                document.getElementById("successful").style.display = "block";
            }else{
                document.getElementById("fail").style.display = "block";
            }
        }
    };
    xhr.open("POST", 'http://localhost:8080/setting/wifi/credentials', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(wifi_selected));
}

function wifiSelection() {
    var children = document.getElementById("options-wifi").children;
    for (var i = 0; i < children.length; i++) {
        if (this.id === children[i].children[0].id)
            this.classList.contains("btn-secondary") ? this.className = "btn btn-primary btn-block" : this.className = "btn btn-secondary btn-block";
        else {
            children[i].children[0].className = "btn btn-secondary btn-block";
            $('#collapseKeyboard').collapse('hide');

        }
    }

    if (wifiMap.get(this.id).isKnown) {
        console.log("hide");
        $('#collapseKeyboard').collapse('hide');
    } else {
        console.log("show");

        $('#collapseKeyboard').collapse('show');

    }
}

function createMapFromWifiList(obj) {
    var wifiMap = new Map();
    for (var i = 0; i < obj.length; i++) {
        wifiMap.set(obj[i].essid, obj[i]);
    }
    return wifiMap;
}

function change_color() {
    nest.away = false;
    document.getElementById("main-container").style.pointerEvents = "auto";
    var name = this.id;
    var value = this.value;
    var array = ["Summer", "Winter"];
    for (var x = 0; x < array.length; x++) {
        if (value == 0) {
            if (array[x] != name) {
                document.getElementById(name).className = "btn btn-primary";
                document.getElementById(name).value = 1;
                document.getElementById(array[x]).className = "btn btn-secondary";
                document.getElementById(array[x]).value = 0;
                // var xhr = new XMLHttpRequest();

                if (name === "Summer") {
                    antif_state = document.getElementById("AntiFreeze").value;
                    if ( antif_state == 0) {
                        nest.hvac_state = 'cooling';
                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                        xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.send("summer");
                    }else {
                        document.getElementById(name).className = "btn btn-secondary";
                        document.getElementById(name).value = 0;
                        document.getElementById(array[x]).className = "btn btn-primary";
                        document.getElementById(array[x]).value = 1;
                    }
                } else if (name === "Winter") {
                    antif_state = document.getElementById("AntiFreeze").value;
                    if ( antif_state == 0) {
                        nest.hvac_state = 'heating';
                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                        xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.send("winter");
                    } /*else {
                        document.getElementById(name).className = "btn btn-secondary m-1";
                        document.getElementById(name).value = 0;
                        document.getElementById(array[x]).className = "btn btn-primary m-1";
                        document.getElementById(array[x]).value = 1;
                    }*/
                }
            }
        } else if (value == 1) {
            if (array[x] != name) {
                document.getElementById(name).className = "btn btn-secondary";
                document.getElementById(name).value = 0;
                document.getElementById(array[x]).className = "btn btn-primary";
                document.getElementById(array[x]).value = 1;
                //nest.hvac_state = 'off';
                if (array[x] === "Summer") {
                    antif_state = document.getElementById("AntiFreeze").value;
                    if ( antif_state == 0) {
                        nest.hvac_state = 'cooling';
                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                        xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.send("summer");
                    } else {
                        document.getElementById(name).className = "btn btn-primary";
                        document.getElementById(name).value = 0;
                        document.getElementById(array[x]).className = "btn btn-secondary";
                        document.getElementById(array[x]).value = 1;
                    }
                } else if (array[x] === "Winter") {
                    nest.hvac_state = 'heating';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("winter");
                }
            }
        }
    }
}


function manual() {
    nest.away = false;
    var name = this.id;
    var value = this.value;
    var xhr = new XMLHttpRequest();
    var idRoom = $($('h1').contents()[0]).text();
    var desiredTemperature = nest.target_temperature;
    var obj = {idRoom: idRoom, desiredTemperature: desiredTemperature};

    if (value == 0) {
        document.getElementById(name).className = "btn btn-primary m-1";
        document.getElementById(name).value = 1;
        document.getElementById("AntiFreeze").className = "btn btn-secondary m-1";
        document.getElementById("AntiFreeze").value = 0;
        var jsonSend = JSON.stringify(obj);
        xhr.open("POST", 'http://localhost:8080/temperature/manual', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(jsonSend);
        document.getElementById("main-container").style.pointerEvents = "auto";
    } else if (value == 1) {
        document.getElementById(name).className = "btn btn-secondary m-1";
        document.getElementById(name).value = 0;
        xhr.open("POST", 'http://localhost:8080/temperature/programmed', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(idRoom);
        document.getElementById("main-container").style.pointerEvents = "none";
        // document.getElementById("container-buttons").style.pointerEvents = "auto";
        // document.getElementById("left_button").style.pointerEvents = "auto";
        // document.getElementById("right_button").style.pointerEvents = "auto";

        nest.target_temperature = nest.ambient_temperature;
    }
}

function antifreeze() {
    nest.away = false;
    document.getElementById("main-container").style.pointerEvents = "auto";


    var name = this.id;
    var value = this.value;
    if (value == 0) {
        var idRoom = $($('h1').contents()[0]).text();
        document.getElementById(name).className = "btn btn-primary m-1";
        document.getElementById(name).value = 1;
        document.getElementById("Summer").className = "btn btn-secondary";
        document.getElementById("Summer").value = 0;
        document.getElementById("Manual").className = "btn btn-secondary m-1";
        document.getElementById("Manual").value = 0;
        document.getElementById("Winter").className = "btn btn-primary";
        document.getElementById("Winter").value = 1;
        nest.hvac_state = 'heating';
        var xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send("antifreeze");
    } else if (value == 1) {
        document.getElementById(name).className = "btn btn-secondary m-1";
        document.getElementById(name).value = 0;
        var xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send("antifreeze");
    }
}

function activate_Leave_Resource(){
    if(this.value == 0){
        var hours = document.getElementById("time-hours-select").value.split(" ")[0];
        var days = document.getElementById("time-days-select").value.split(" ")[0];
        var leaveTemperature = document.getElementById("temperature_leave").value;
        var hourAmount = days*24 + parseInt(hours);
        document.getElementById("activate-leave").className = "btn btn-primary btn-lg btn-block";
        document.getElementById("activate-leave").value = 1;
        var LeaveResource = {leaveTemperature: leaveTemperature, hourAmount: hourAmount};
        var xhr = new XMLHttpRequest();
        var jsonSend = JSON.stringify(LeaveResource);
        xhr.open("POST", 'http://localhost:8080/temperature/leave', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(jsonSend);
    }else{
        document.getElementById("activate-leave").className = "btn btn-secondary btn-lg btn-block";
        document.getElementById("activate-leave").value = 0;
    }
}

