var wifiMap = new Map();


function requestWifiList() {
    var xhttp_wifi = new XMLHttpRequest();
    xhttp_wifi.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            obj = JSON.parse(xhttp_wifi.responseText);
            wifiMap = createMapFromWifiList(obj);
            console.log(wifiMap);
            var capa = document.getElementById("options-wifi");
            for (var i = 0; i < obj.length; i++) {
                var div = document.createElement("div");
                div.setAttribute("class", "d-flex flex-row p-1");

                var button = document.createElement("button");
                button.setAttribute("type", "button");
                button.setAttribute("class", "btn btn-secondary form-control");
                button.setCustomValidity("aria-describedby", "essidHelp");
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
    var wifi_selected;
    var children = document.getElementById("options-wifi").children;
    for (var i = 0; i < children.length; i++) {
        console.log(children[i].id);
        if (children[i].classList.contains("btn-primary")) {
            wifi_selected = wifiMap.get(children[i].id);
        }
    }

    wifi_selected.netPassword = null;
    if (!wifi_selected.isKnown)
        wifi_selected.netPassword = document.getElementById("text").value;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", 'http://localhost:8080/setting/wifi/credentials', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify(wifi_selected));
}

function wifiSelection() {
    var children = document.getElementById("options-wifi").children;
    for (var i = 0; i < children.length; i++) {
        if (this.id === children[i].children[0].id)
            this.classList.contains("btn-secondary") ? this.className = "btn btn-primary form-control" : this.className = "btn btn-secondary form-control";
        else
            children[i].children[0].className = "btn btn-secondary form-control";

    }

    var texto = document.getElementById("text");
    texto.value = "";
    if (wifiMap.get(this.id).isKnown) {
        document.getElementById("cont-text").style.visibility = "hidden";
        texto.value = "1234";
    } else {
        document.getElementById("cont-text").style.visibility = "visible";
        $('#text').trigger('click');
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
                    nest.hvac_state = 'cooling';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("summer");
                } else if (name === "Winter") {
                    if (document.getElementById("AntiFreeze").value == 0) {
                        nest.hvac_state = 'heating';
                        var xhr = new XMLHttpRequest();
                        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                        xhr.setRequestHeader("Content-Type", "application/json");
                        xhr.send("winter");
                    } else {
                        document.getElementById(name).className = "btn btn-secondary";
                        document.getElementById(name).value = 0;
                        document.getElementById(array[x]).className = "btn btn-primary";
                        document.getElementById(array[x]).value = 1;
                    }
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
                    nest.hvac_state = 'cooling';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("summer");
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
    var name = this.id;
    var value = this.value;
    var xhr = new XMLHttpRequest();
    var idRoom = $($('h1').contents()[0]).text();
    var desiredTemperature = nest.target_temperature;
    var obj = {idRoom: idRoom, desiredTemperature: desiredTemperature};
    //alert(obj.idRoom);
    //alert(obj.desiredTemperature);
    //alert(desiredTemperature);
    //alert(as);
    if (value == 0) {
        document.getElementById(name).className = "btn btn-primary";
        document.getElementById(name).value = 1;
        document.getElementById("AntiFreeze").className = "btn btn-secondary";
        document.getElementById("AntiFreeze").value = 0;
        var jsonSend = JSON.stringify(obj);
        xhr.open("POST", 'http://localhost:8080/temperature/manual', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(jsonSend);
        document.getElementById("main-container").style.pointerEvents = "auto";
    } else if (value == 1) {
        document.getElementById(name).className = "btn btn-secondary";
        document.getElementById(name).value = 0;
        xhr.open("POST", 'http://localhost:8080/temperature/programmed', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(idRoom);
        //document.getElementById("thermostat").style.visibility = "hidden";
        document.getElementById("main-container").style.pointerEvents = "none";
        document.getElementById("container-buttons").style.pointerEvents = "auto";
        document.getElementById("left_button").style.pointerEvents = "auto";
        document.getElementById("right_button").style.pointerEvents = "auto";
        //nest.disabled = true;

        /*document.getElementById("main-container").removeEventListener('mousedown', dragStart);
        document.getElementById("main-container").removeEventListener('touchstart', dragStart);

        document.getElementById("main-container").removeEventListener('mouseup', dragEnd);
        document.getElementById("main-container").removeEventListener('mouseleave', dragEnd);
        document.getElementById("main-container").removeEventListener('touchend', dragEnd);*/

        //include('../../imported_component/nest-thermostat-control/js/thermostat.js');
        //document.getElementById("main-container").removeEventListener('mousemove', dragMove);
        //document.getElementById("main-container").removeEventListener('touchmove', dragMove);        

    }
}

function antifreeze() {
    var name = this.id;
    var value = this.value;
    if (value == 0) {
        var idRoom = $($('h1').contents()[0]).text();
        document.getElementById(name).className = "btn btn-primary";
        document.getElementById(name).value = 1;
        document.getElementById("Summer").className = "btn btn-primary";
        document.getElementById("Summer").value = 1;
        document.getElementById("Manual").className = "btn btn-secondary";
        document.getElementById("Manual").value = 0;
        document.getElementById("Winter").className = "btn btn-secondary";
        document.getElementById("Winter").value = 0;
        nest.hvac_state = 'cooling';
        var xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send("antifreeze");
        var xhr = new XMLHttpRequest();
        xhr.open("POST", 'http://localhost:8080/temperature/programmed', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(idRoom);
    } else if (value == 1) {
        document.getElementById(name).className = "btn btn-secondary";
        document.getElementById(name).value = 0;
    }
}


