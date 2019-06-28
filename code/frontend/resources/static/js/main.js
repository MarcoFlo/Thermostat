var obj;
window.onload = function () {
    document.getElementById("Summer").onclick = change_color;
    document.getElementById("Winter").onclick = change_color;
    document.getElementById("Manual").onclick = manual;

    document.getElementById("connect").onclick = connect;


    document.getElementById("right_arrow").addEventListener('click', changeRoom);
    document.getElementById("left_arrow").addEventListener('click', changeRoom);

    //to disable long press -> right click in chromium
    window.oncontextmenu = function () {
        return false;
    };

    mqttLoad();
    requestWifiList();
    requestEspFree();
// requestSettingPage();


    /*var week = document.getElementById("week").onclick = color;*/

    /*var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;*/

    /* var minus = document.getElementById("minus").onclick = hours;
     var plus = document.getElementById("plus").onclick = hours;

     var minus_temp = document.getElementById("minus_temp").onclick = temp;
     var plus_temp = document.getElementById("plus_temp").onclick = temp;*/


};
