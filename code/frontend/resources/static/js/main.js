window.onload = function(){
    document.getElementById("Summer").onclick = change_color;
    document.getElementById("Winter").onclick = change_color;
    document.getElementById("Manual").onclick = change_color;

    window.oncontextmenu = function() { return false; };
    // window.addEventListener("contextmenu", function(e) { e.preventDefault(); })


    mqttLoad();
    requestWifiList();
    requestEspFree();

    nest.ambient_temperature = 18;
    /*var week = document.getElementById("week").onclick = color;*/

    /*var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;*/

    /* var minus = document.getElementById("minus").onclick = hours;
     var plus = document.getElementById("plus").onclick = hours;

     var minus_temp = document.getElementById("minus_temp").onclick = temp;
     var plus_temp = document.getElementById("plus_temp").onclick = temp;*/


};
