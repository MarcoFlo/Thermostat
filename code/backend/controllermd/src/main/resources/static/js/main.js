var obj;
window.onload = function () {
    //to disable long press -> right click in chromium
    window.oncontextmenu = function () {
        return false;
    };

    document.getElementById("Summer").addEventListener("click", change_color);
    document.getElementById("Winter").onclick = change_color;
    document.getElementById("Manual").onclick = manual;
    document.getElementById("AntiFreeze").onclick = antifreeze;


    document.getElementById("connect").addEventListener("click", connectWifi);
    document.getElementById("reload").addEventListener("click", requestWifiList);


    document.getElementById("right_arrow").addEventListener('click', changeRoom);
    document.getElementById("left_arrow").addEventListener('click', changeRoom);






    showTime();
    mqttLoad();
    requestWifiList();


    //Setting/room
    document.getElementById("save").addEventListener("click", savePhoneForm);
    document.getElementById("reset").addEventListener("click", resetPhoneForm);
    document.getElementById("weekend").addEventListener("click", toggleButton);
    document.getElementById("time-slice-select").addEventListener("change", saveSliceData);
    document.getElementById("weekend").addEventListener("click", saveSliceData);
    document.getElementById("room-select").addEventListener("change", resetPhoneForm);
    document.getElementById("plus-button").addEventListener("click", addNewRoom);
    setUpRoomSelect();

    //stats
    document.getElementById("room_name").innerText = "MainRoom";
    document.getElementById("room-stats").innerText = "MainRoom";
    getStats("MainRoom");
    document.getElementById("right-stats").addEventListener("click", changeStatsRoom);
    document.getElementById("left-stats").addEventListener("click", changeStatsRoom);


};

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
