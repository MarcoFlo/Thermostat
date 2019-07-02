var obj;
window.onload = function () {
    document.getElementById("Summer").onclick = change_color;
    document.getElementById("Winter").onclick = change_color;
    document.getElementById("Manual").onclick = manual;

    document.getElementById("connect").addEventListener("click",connectWifi);
    document.getElementById("AntiFreeze").onclick = antifreeze;


    document.getElementById("right_arrow").addEventListener('click', changeRoom);
    document.getElementById("left_arrow").addEventListener('click', changeRoom);

    //to disable long press -> right click in chromium
    window.oncontextmenu = function () {
        return false;
    };

    showTime();
    mqttLoad();
    requestWifiList();
// requestSettingPage();


    /*var week = document.getElementById("week").onclick = color;*/

    /*var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;*/

    /* var minus = document.getElementById("minus").onclick = hours;
     var plus = document.getElementById("plus").onclick = hours;

     var minus_temp = document.getElementById("minus_temp").onclick = temp;
     var plus_temp = document.getElementById("plus_temp").onclick = temp;*/


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
