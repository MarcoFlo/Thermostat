var obj;
window.onload = function(){
    document.getElementById("Summer").onclick = change_color;
    document.getElementById("Winter").onclick = change_color;
    document.getElementById("Manual").onclick = change_color;

    document.getElementById("connect").onclick = connect;

   /* document.getElementById("list-wifi").onclick = function(){
        alert(this.id);
    }  */

    document.getElementById("wifi-list-1").onclick = function(){
        var xhttp_wifi = new XMLHttpRequest();
        xhttp_wifi.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            var texto = document.getElementById("text");
            texto.value = "";
            obj = JSON.parse(xhttp_wifi.responseText);
            var key = obj[0].isKnown; 
            if( key == 1){
                document.getElementById("cont-text").style.visibility = "hidden";
                texto.value= "1234"; 
            }else{
                document.getElementById("cont-text").style.visibility = "visible";
            }
        }
        };
        xhttp_wifi.open("GET", "http://localhost:8080/setting/wifi/list", true); /*filename='localhost:8080/setting/esp/free';*/
        xhttp_wifi.send();
    };


    document.getElementById("right_arrow").addEventListener('click', changeRoom);
    document.getElementById("left_arrow").addEventListener('click', changeRoom);

    //to disable long press -> right click in chromium
    window.oncontextmenu = function() { return false; };

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
