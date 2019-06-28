window.onload = function () {
    document.getElementById("save").addEventListener("click", savePhoneForm);
    document.getElementById("reset").addEventListener("click", resetPhoneForm);

    document.getElementById("weekend").addEventListener("click", toggleButton);

    document.getElementById("time-slice-select").addEventListener("change", saveHourlyData);
    document.getElementById("weekend").addEventListener("click", saveHourlyData);


    requestListFreeEsp();


};


function toggleButton() {
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
                button.onclick = toggleButton;
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
    sliceList = [new Map(), new Map()];
    document.getElementById("roomName").value = "";
    document.getElementById("start-time-input").value = "";
    document.getElementById("weekend").className = "btn btn-secondary";
    document.getElementById("time-slice-select").value = sliceArr[0];

    document.getElementById("start-time-input").classList.remove('border-danger');
    var start_time_help_block = document.getElementById("start-time-help-block");
    start_time_help_block.innerHTML = "";
    start_time_help_block.className = "d-none";

    var espButtonList = document.getElementById("esp-container").children;
    for (var i = 0; i < espButtonList.length; i++)
        espButtonList[i].children[0].className = "btn btn-secondary";
}

function produceProgramJson(room_name, espSelected) {
    var program = {
        'idProgram': room_name,
        'weeklyList': []
    };

    for (var i = 0; i < 2; i++) {
        var day = {
            'wake': getFormatted(sliceList[i].get('Wake')),
            'leave': getFormatted(sliceList[i].get('Leave')),
            'return': getFormatted(sliceList[i].get('Return')),
            'sleep': getFormatted(sliceList[i].get('Sleep'))
        };
        var dailyMap = {
            dailyMap: day
        };
        program.weeklyList.push(dailyMap);
    }

    var result = {
        'idRoom': room_name,
        'esp8266List': espSelected,
        'program': program
    };
    return JSON.stringify(result);
}

function savePhoneForm() {
    saveHourlyData();
    var room_name = document.getElementById("roomName").value;

    var espSelected = [];
    var espButtonList = document.getElementById("esp-container").children;
    for (var i = 0; i < espButtonList.length; i++) {
        if (espButtonList[i].children[0].classList.contains("btn-primary")) {
            espSelected.push(espButtonList[i].children[0].id);
        }
    }

    if (room_name !== "" && espSelected.length !== 0 && sliceList[0].size === 4 && sliceList[1].size === 4) {

        var xhttp_room = new XMLHttpRequest();
        xhttp_room.open("POST", "http://localhost:8080/setting/room", true);
        xhttp_room.setRequestHeader("Content-Type", "application/json");

        xhttp_room.send(produceProgramJson(room_name, espSelected));


    } else {
        var roomFormError = document.createElement("h6");
        roomFormError.setAttribute("class", "alert alert-warning p-0 px-2 m-0 text-center");
        roomFormError.setAttribute("id", "room-form-error");
        roomFormError.innerHTML = "Fill all the field";
        document.getElementById("save-div").append(roomFormError);
        setTimeout(function () {
            $("#room-form-error").delay(3000).remove()
        }, 3000);
    }
}

function HourlyProgram(time, temperature) {
    this.time = time;
    this.temperature = temperature;


}

function getFormatted(hourlyProgram) {
    return new HourlyProgram(getTimeFromDate(hourlyProgram.time), hourlyProgram.temperature);
}
