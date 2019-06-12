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
function change_color(){
    var name= new String(this.id);
    var value = this.value;
    var array = ["Summer", "Winter", "Manual"];
    for(x=0;x<array.length;x++){
        if(value==0){
            if(array[x] != name){
                document.getElementById(name).className = "btn btn-primary";
                document.getElementById(name).value = 1;
                document.getElementById(array[x]).disabled = true;
                if(name=="Summer")
                    nest.hvac_state = 'cooling';
                else if(name=="Winter")
                    nest.hvac_state = 'heating';


            }
        }else if(value==1){
            if(array[x] != name){
                document.getElementById(name).className = "btn btn-secondary";
                document.getElementById(name).value = 0;
                document.getElementById(array[x]).disabled = false;
                nest.hvac_state = 'off';
            }
        }
    }
}
