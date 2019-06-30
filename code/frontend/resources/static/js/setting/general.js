function toggleButton() {
    if (this.classList.contains("btn-secondary"))
        this.className = "btn btn-primary";
    else
        this.className = "btn btn-secondary";

}

function getTimeFromDate(date) {
    return date.toTimeString().substring(0, 5);
}

function showTime() {
    var date = new Date();
    var h = date.getHours(); // 0 - 23
    var m = date.getMinutes(); // 0 - 59
    var s = date.getSeconds(); // 0 - 59

    h = (h < 10) ? "0" + h : h;
    m = (m < 10) ? "0" + m : m;
    s = (s < 10) ? "0" + s : s;

    var time = h + ":" + m + ":" + s + " ";
    document.getElementById("clock").innerText = time;
    document.getElementById("clock").textContent = time;

    setTimeout(showTime, 1000);
}

function createMapFromJson(toBeMapped) {
    var result = new Map();
    result.set("Wake", new HourlyProgram(toBeMapped.dailyMap.wake.time, toBeMapped.dailyMap.wake.temperature));
    result.set("Leave", new HourlyProgram(toBeMapped.dailyMap.leave.time, toBeMapped.dailyMap.leave.temperature));
    result.set("Back", new HourlyProgram(toBeMapped.dailyMap.back.time, toBeMapped.dailyMap.back.temperature));
    result.set("Sleep", new HourlyProgram(toBeMapped.dailyMap.sleep.time, toBeMapped.dailyMap.sleep.temperature));
    return result;

}

function resetPhoneForm() {
    console.log("resetting the form");


    var select_room = document.getElementById("room-select");

    if (select_room.value !== "New Room") {
        var xhttp_select_room = new XMLHttpRequest();
        xhttp_select_room.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                var room_resource = JSON.parse(xhttp_select_room.responseText);
                console.log(room_resource);
                console.log(room_resource.program.weeklyList[1]);

                sliceList = [createMapFromJson(room_resource.program.weeklyList[0]), createMapFromJson(room_resource.program.weeklyList[1])];
                document.getElementById("roomName").value = room_resource.idRoom;

                requestListFreeEsp(room_resource.esp8266List);


                document.getElementById("start-time-input").value = sliceList[0].get(sliceArr[0]).time;
                document.getElementById("weekend").className = "btn btn-secondary";
                document.getElementById("time-slice-select").value = sliceArr[0];

                document.getElementById("start-time-input").classList.remove('border-danger');
                var start_time_help_block = document.getElementById("start-time-help-block");
                start_time_help_block.innerHTML = "";
                start_time_help_block.className = "d-none";
                $('#temperature').val(sliceList[0].get(sliceArr[0]).temperature);

            }
        };
        xhttp_select_room.open("GET", "http://localhost:8080/setting/room/resource/" + select_room.value, true);
        xhttp_select_room.send();
    } else {
        requestListFreeEsp(null);
        sliceList = [new Map(), new Map()];
        document.getElementById("roomName").value = "";
        document.getElementById("start-time-input").value = "";
        document.getElementById("weekend").className = "btn btn-secondary";
        document.getElementById("time-slice-select").value = sliceArr[0];

        document.getElementById("start-time-input").classList.remove('border-danger');
        var start_time_help_block = document.getElementById("start-time-help-block");
        start_time_help_block.innerHTML = "";
        start_time_help_block.className = "d-none";

        $('#temperature').val(20);

    }

}


function savePhoneForm() {
    saveSliceData();
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
        xhttp_room.open("POST", "http://localhost:8080/setting/room/resource", true);
        xhttp_room.setRequestHeader("Content-Type", "application/json");

        xhttp_room.send(produceProgramJson(room_name, espSelected));

        var select_room = document.getElementById("room-select");
        if (select_room.value === "New Room") {
            var option = document.getElementById("New Room");
            option.innerHTML = room_name;
        }

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

function getFormatted(hourlyProgram) {
    return new HourlyProgram(getTimeFromDate(hourlyProgram.time), hourlyProgram.temperature);
}


function HourlyProgram(time, temperature) {
    this.time = time;
    this.temperature = temperature;
}
