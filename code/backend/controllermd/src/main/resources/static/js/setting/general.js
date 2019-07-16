function rotateRoom(currentRoom, id) {
    var desired_room = currentRoom;
    if (id.indexOf("right") !== -1) {
        desired_room = (currentRoom + 1) % room_list.length;
    } else if (id.indexOf("left") !== -1) {
        if (currentRoom === 0)
            desired_room = room_list.length - 1;
        else
            desired_room = currentRoom - 1;
        desired_room = desired_room % room_list.length;
    }

    return desired_room;
}

function toggleButton() {
    console.log("touchstart event");
    if (this.classList.contains("btn-secondary"))
        this.className = "btn btn-primary";
    else
        this.className = "btn btn-secondary";

}

function getTimeFromDate(date) {
    return date.toTimeString().substring(0, 5);
}


function createMapFromJson(toBeMapped) {
    var result = new Map();
    result.set("Wake", new HourlyProgram(new Date("2019-01-01T" + toBeMapped.dailyMap.wake.time), toBeMapped.dailyMap.wake.temperature));
    result.set("Leave", new HourlyProgram(new Date("2019-01-01T" + toBeMapped.dailyMap.leave.time), toBeMapped.dailyMap.leave.temperature));
    result.set("Back", new HourlyProgram(new Date("2019-01-01T" + toBeMapped.dailyMap.back.time), toBeMapped.dailyMap.back.temperature));
    result.set("Sleep", new HourlyProgram(new Date("2019-01-01T" + toBeMapped.dailyMap.sleep.time), toBeMapped.dailyMap.sleep.temperature));
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

                sliceList = [createMapFromJson(room_resource.program.weeklyList[0]), createMapFromJson(room_resource.program.weeklyList[1])];
                document.getElementById("roomName").value = room_resource.idRoom;

                requestListFreeEsp(room_resource.esp8266List);


                document.getElementById("start-time-input").value = getTimeFromDate(sliceList[0].get(sliceArr[0]).time);
                document.getElementById("weekend").className = "btn btn-secondary";
                document.getElementById("time-slice-select").value = sliceArr[0];

                document.getElementById("start-time-input").classList.remove('border-danger');
                var start_time_help_block = document.getElementById("start-time-help-block");
                start_time_help_block.innerHTML = "";
                start_time_help_block.className = "d-none";
                $('#temperature').val(sliceList[0].get(sliceArr[0]).temperature);

            }
        };
        xhttp_select_room.open("GET", window.location.origin + "/setting/room/resource/" + select_room.value, true);
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
        xhttp_room.open("POST", window.location.origin + "/setting/room/resource", true);
        xhttp_room.setRequestHeader("Content-Type", "application/json");

        xhttp_room.send(produceProgramJson(room_name, espSelected));

        var select_room = document.getElementById("room-select");
        if (select_room.value === "New Room") {
            var option = document.getElementById("New Room");
            option.innerHTML = room_name;
        }
        var roomFormError = document.createElement("h6");
        roomFormError.setAttribute("class", "alert alert-success p-0 px-2 m-0 text-center");
        roomFormError.setAttribute("id", "room-form-error");
        roomFormError.innerHTML = "Saved succesfully";
        document.getElementById("save-div").append(roomFormError);
        setTimeout(function () {
            $("#room-form-error").delay(3000).remove()
        }, 3000);

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
            'back': getFormatted(sliceList[i].get('Back')),
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

