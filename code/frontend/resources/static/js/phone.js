window.onload = function () {
    document.getElementById("save").onclick = savePhoneForm;
    document.getElementById("reset").onclick = resetPhoneForm;
    requestListFreeEsp();


};


function toggleEspButton() {
    if (this.classList.contains("btn-secondary"))
        this.className = "btn btn-primary";
    else
        this.className = "btn btn-secondary";

}

function requestListFreeEsp() {
    var xhttp_esp = new XMLHttpRequest();
    xhttp_esp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            espList = JSON.parse(xhttp_esp.responseText);
            var espcontainer = document.getElementById("esp-container");

            for (i = 1; i < espList.length; i++) {
                var div = document.createElement("div");
                div.setAttribute("class", "col-auto p-1");

                var button = document.createElement("button");
                button.setAttribute("type", "button");
                button.setAttribute("class", "btn btn-secondary");
                button.setAttribute("name", "esp-button");
                button.onclick = toggleEspButton;
                button.innerHTML = espList[i];
                button.setAttribute("id", espList[i]);

                div.appendChild(button);
                espcontainer.appendChild(div);
            }

        }
    };
    xhttp_esp.open("GET", "http://localhost:8080/setting/esp/free", true);
    xhttp_esp.send();
}

function resetPhoneForm() {
    console.log("resetting the form");
    sliceMap = new Map();
    document.getElementById("roomName").value = "";
    document.getElementById("start-time-input").value = "";


    var espButtonList = document.getElementById("esp-container").children;
    for (var i = 0; i < espButtonList.length; i++)
        espButtonList[i].children[0].className = "btn btn-secondary";
}

function savePhoneForm() {
    var room_name = document.getElementById("roomName").value;

    var espSelected = [];
    var espButtonList = document.getElementById("esp-container").children;
    for (var i = 0; i < espButtonList.length; i++) {
        if (espButtonList[i].children[0].classList.contains("btn-primary")) {
            espSelected.push(espButtonList[i].children[0].id);
        }
    }
    console.log(espSelected[0]);
    console.log(JSON.stringify({idRoom: room_name, esp8266List: espSelected}));

    if (room_name !== "" && espSelected.length !== 0 && sliceMap.size === 4) {

        var xhttp_room = new XMLHttpRequest();
        xhttp_room.open("POST", "http://localhost:8080/setting/room", true);
        xhttp_room.setRequestHeader("Content-Type", "application/json");

        xhttp_room.send();


    }

}
