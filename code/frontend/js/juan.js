window.onload = function(){
    var summer =document.getElementById("Summer").onclick = change_color;
    var summer =document.getElementById("Winter").onclick = change_color;
    var summer =document.getElementById("Manual").onclick = change_color;
    /*var week = document.getElementById("week").onclick = color;*/

    /*var anterior = document.getElementById("anterior").onclick = profiles;
    var despues = document.getElementById("despues").onclick = profiles;*/

   /* var minus = document.getElementById("minus").onclick = hours;
    var plus = document.getElementById("plus").onclick = hours;

    var minus_temp = document.getElementById("minus_temp").onclick = temp;
    var plus_temp = document.getElementById("plus_temp").onclick = temp;*/

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            var obj = JSON.parse(xhttp.responseText);
            /*document.getElementById("demo").innerHTML = obj;*//*String separado por comas*/

        }
    };
    xhttp.open("GET", "http://192.168.43.225:8080/setting/esp/free", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp.send();

    var xhttp_wifi = new XMLHttpRequest();
    xhttp_wifi.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            console.log("fd33");
            var obj = JSON.parse(xhttp_wifi.responseText);

            console.log(obj[0].essid);
            /*var array = obj.split("");*/
            console.log(obj.length);
            if(obj.length>3){
                var resta = obj.length - 3;
                var capa = document.getElementById("list-wifi");
                for(i=1;i<=resta;i++){
                    var input = document.createElement("input");
                    input.setAttribute("type", "checkbox");
                    var aux = 3+i;
                    input.setAttribute("id", ""+aux);
                }
            }
            for(i=0;i<obj.length;i++){
                document.getElementById("wifi-1").innerHTML = obj[i].essid;
            }
            

        }
    };
    xhttp_wifi.open("GET", "http://192.168.43.225:8080/setting/wifi/list", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp_wifi.send();
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
                if(name=="Summer"){
                    nest.hvac_state = 'cooling';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'localhost:8080/temperature/wsa', true);

                    //Send the proper header information along with the request
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                    xhr.onreadystatechange = function() { // Call a function when the state changes.
                        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                            // Request finished. Do processing here.
                        }
                    }
                    xhr.send("summer");
                    // xhr.send(new Int8Array()); 
                    // xhr.send(document);
                }
                else if(name=="Winter")
                    nest.hvac_state = 'heating';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'localhost:8080/temperature/wsa', true);

                    //Send the proper header information along with the request
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                    xhr.onreadystatechange = function() { // Call a function when the state changes.
                        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
                            // Request finished. Do processing here.
                        }
                    }
                    xhr.send("winter");
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
