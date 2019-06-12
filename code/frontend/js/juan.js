window.onload = function(){
    var summer =document.getElementById("Summer").onclick = change_color;
    var summer =document.getElementById("Winter").onclick = change_color;
    var summer =document.getElementById("Manual").onclick = change_color;
    var week = document.getElementById("week").onclick = color;

    var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;

    var minus = document.getElementById("minus").onclick = hours;
    var plus = document.getElementById("plus").onclick = hours;

    var minus_temp = document.getElementById("minus_temp").onclick = temp;
    var plus_temp = document.getElementById("plus_temp").onclick = temp;

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            var obj = JSON.parse(xhttp.responseText);
            document.getElementById("demo").innerHTML = obj;/*String separado por comas*/

        }
    };
    xhttp.open("GET", "http://192.168.43.225:8080/setting/esp/free", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp.send();

    var xhttp_wifi = new XMLHttpRequest();
    xhttp_wifi.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            var obj = JSON.parse(xhttp_wifi.responseText);
            document.getElementById("list_wifi").innerHTML = obj;/*String separado por comas*/

        }
    };
    xhttp.open("GET", "http://192.168.43.225:8080/setting/wifi/list", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp.send();
}
