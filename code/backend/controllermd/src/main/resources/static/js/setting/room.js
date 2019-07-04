function setUpRoomSelect() {
    var xhttp_room = new XMLHttpRequest();
    xhttp_room.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var room_list = JSON.parse(xhttp_room.responseText);
            var select_room = document.getElementById("room-select");
            for (var i = 0; i < room_list.length; i++) {
                var option = document.createElement("option");
                option.innerHTML = room_list[i];
                option.setAttribute("id", room_list[i]);
                select_room.append(option);
            }
            resetPhoneForm();


        }
    };
    xhttp_room.open("GET", "http://localhost:8080/setting/room/list", true);
    xhttp_room.send();
}



function addNewRoom() {
    var new_room = "New Room";
    var select_room = document.getElementById("room-select");
    console.log(select_room.value);
    if (select_room.value !== new_room)
    {
        console.log("fatto");
        var option = document.createElement("option");
        option.innerHTML = new_room;
        option.setAttribute("id", new_room);
        select_room.append(option);
        select_room.value = new_room;
    }
    resetPhoneForm();

}
