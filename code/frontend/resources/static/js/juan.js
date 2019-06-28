
function requestEspFree() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            var obj = JSON.parse(xhttp.responseText);
            /*document.getElementById("demo").innerHTML = obj;*//*String separado por comas*/

        }
    };
    xhttp.open("GET", "http://localhost:8080/setting/esp/free", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp.send();

}

function requestWifiList() {
    var xhttp_wifi = new XMLHttpRequest();
    xhttp_wifi.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            // Typical action to be performed when the document is ready:
            /*console.log(xhttp.responseText);*/
            obj = JSON.parse(xhttp_wifi.responseText);

            console.log(obj[0].essid);
            /*var array = obj.split("");*/
            console.log(obj.length);
            if (obj.length > 1) {
                //var resta = obj.length - 1;
                var capa = document.getElementById("options-wifi");
                for (i = 1; i <= obj.length; i++) {
                    var input = document.createElement("input");
                    input.setAttribute("type", "button");
                    input.setAttribute("class", "btn btn-secondary");
                    var aux = i;
                    input.setAttribute("name", "wifi-1");
                    input.setAttribute("value", ""+obj[i-1].essid);
                    input.setAttribute("id", ""+aux);
                    input.onclick= function(){
                        //var xhttp_wifi = new XMLHttpRequest();
                        //xhttp_wifi.onreadystatechange = function () {
                         //   if (this.readyState == 4 && this.status == 200) {
                            // Typical action to be performed when the document is ready:
                            /*console.log(xhttp.responseText);*/
                        //    var obj = JSON.parse(xhttp_wifi.responseText);
                        for (i = 1; i <= obj.length; i++) {
                            document.getElementById(i).className = "btn btn-secondary";
                        }
                        document.getElementById(this.id).className = "btn btn-primary";
                        var texto = document.getElementById("text");
                        texto.value = "";
                        var key = obj[this.id-1].isKnown; 
                        if( key == 1){
                            document.getElementById("cont-text").style.visibility = "hidden";
                            texto.value= "1234";    
                        }else{
                            document.getElementById("cont-text").style.visibility = "visible";
                            $('#text').trigger('click');
                        }
                        //}
                    };
                    //xhttp_wifi.open("GET", "http://localhost:8080/setting/wifi/list", true); /*filename='localhost:8080/setting/esp/free';*/
                    //xhttp_wifi.send();
                    //};
                    //var span = document.createElement("SPAN");
                    //span.setAttribute("id", "wifi-" + aux);
                    //var branch = document.createElement("br");
                    capa.appendChild(input);
                    //capa.appendChild(span);
                    //capa.appendChild(branch);
                }
            }
            /*for (i = 0; i < obj.length; i++) {
                var aux = i + 1;
                document.getElementById("wifi-" + aux).innerHTML = obj[i].essid;
            }*/


        }
    };
    xhttp_wifi.open("GET", "http://localhost:8080/setting/wifi/list", true); /*filename='localhost:8080/setting/esp/free';*/
    xhttp_wifi.send();


}


function change_color() {
    var name = this.id;
    var value = this.value;
    var array = ["Summer", "Winter"];
    for (var x = 0; x < array.length; x++) {
        if (value == 0) {
            if (array[x] != name) {
                document.getElementById(name).className = "btn btn-primary";
                document.getElementById(name).value = 1;
                document.getElementById(array[x]).className = "btn btn-secondary";
                document.getElementById(array[x]).value = 0;
               // var xhr = new XMLHttpRequest();

                if (name === "Summer") {
                    nest.hvac_state = 'cooling';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("summer");
                } else if (name === "Winter") {
                    nest.hvac_state = 'heating';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("winter");
                }
            }
        } else if (value == 1) {
            if (array[x] != name) {
                document.getElementById(name).className = "btn btn-secondary";
                document.getElementById(name).value = 0;
                document.getElementById(array[x]).className = "btn btn-primary";
                document.getElementById(array[x]).value = 1;
                //nest.hvac_state = 'off';
                if (array[x] === "Summer") {
                    nest.hvac_state = 'cooling';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("summer");
                } else if (array[x] === "Winter") {
                    nest.hvac_state = 'heating';
                    var xhr = new XMLHttpRequest();
                    xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
                    xhr.setRequestHeader("Content-Type", "application/json");
                    xhr.send("winter");
                }
            }
        }
    }
}

function connect(){
    var wifi_selected = document.getElementsByName("wifi-1");
    //alert(wifi_selected.length);
    for(i=1;i<=wifi_selected.length;i++){
        if(wifi_selected[i-1].checked){
             var xhr = new XMLHttpRequest();
             //obj[i-1].essid = "nana";
             //alert(obj[i-1].essid);
             if(!obj[i-1].isKnown){
                var texto = document.getElementById("text").value;
                obj[i-1].netPassword = texto;
            }
            //alert(obj[i-1].netPassword);
            var jsonSend = JSON.stringify(obj[i-1]);
            xhr.open("POST", 'http://localhost:8080/setting/wifi/credentials', true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.send(jsonSend);
        }
    }
}

function manual(){
    var name = this.id;
    var value = this.value;
    var xhr = new XMLHttpRequest();
    var idRoom = $($('h1').contents()[0]).text();
    var desiredTemperature = nest.target_temperature;
    var obj = { idRoom: idRoom, desiredTemperature: desiredTemperature };
    //alert(obj.idRoom);
    //alert(obj.desiredTemperature);
    //alert(desiredTemperature);
    //alert(as);
    if (value == 0) {
        document.getElementById(name).className = "btn btn-primary";
        document.getElementById(name).value = 1;
        var jsonSend = JSON.stringify(obj);
        xhr.open("POST", 'http://localhost:8080/temperature/manual', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(jsonSend);
        document.getElementById("main-container").style.pointerEvents = "auto";
    }
    else if (value == 1) {
        document.getElementById(name).className = "btn btn-secondary";
        document.getElementById(name).value = 0;
        xhr.open("POST", 'http://localhost:8080/temperature/programmed', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(idRoom);
        //document.getElementById("thermostat").style.visibility = "hidden";
        document.getElementById("main-container").style.pointerEvents = "none";
        document.getElementById("container-buttons").style.pointerEvents = "auto";
        document.getElementById("left_button").style.pointerEvents = "auto";
        document.getElementById("right_button").style.pointerEvents = "auto";
        //nest.disabled = true;

        /*document.getElementById("main-container").removeEventListener('mousedown', dragStart);
        document.getElementById("main-container").removeEventListener('touchstart', dragStart);

        document.getElementById("main-container").removeEventListener('mouseup', dragEnd);
        document.getElementById("main-container").removeEventListener('mouseleave', dragEnd);
        document.getElementById("main-container").removeEventListener('touchend', dragEnd);*/
        
        //include('../../imported_component/nest-thermostat-control/js/thermostat.js');
        //document.getElementById("main-container").removeEventListener('mousemove', dragMove);
        //document.getElementById("main-container").removeEventListener('touchmove', dragMove);        

    }
}
/*function include(file)
{

  var script  = document.createElement('script');
  script.src  = file;
  script.type = 'text/javascript';
  script.defer = true;

  document.getElementsByTagName('head').item(0).appendChild(script);

}*/



