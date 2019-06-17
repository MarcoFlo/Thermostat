window.onload = function(){
    var summer =document.getElementById("Summer").onclick = change_color;
    var summer =document.getElementById("Winter").onclick = change_color;
    var summer =document.getElementById("Manual").onclick = change_color;

    mqttLoad();
    requestWifiList();
    requestEspFree();
    /*var week = document.getElementById("week").onclick = color;*/

    /*var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;*/

    /* var minus = document.getElementById("minus").onclick = hours;
     var plus = document.getElementById("plus").onclick = hours;

     var minus_temp = document.getElementById("minus_temp").onclick = temp;
     var plus_temp = document.getElementById("plus_temp").onclick = temp;*/


};
