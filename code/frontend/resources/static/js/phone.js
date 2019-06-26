window.onload = function () {
    var xhttp_esp = new XMLHttpRequest();
    xhttp_esp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            espList = JSON.parse(xhttp_esp.responseText);

            console.log(espList[0]);
            console.log(espList.length);
            var espcontainer = document.getElementById("esp-container");
                for (i = 1; i < espList.length; i++) {
                    var div = document.createElement("div");
                    div.setAttribute("class","col-auto p-1");

                    var button = document.createElement("button");
                    button.setAttribute("type", "button");
                    button.setAttribute("class", "btn btn-secondary");
                    button.innerHTML = espList[i];
                    button.setAttribute("id", espList[i]);

                    div.appendChild(button);
                    espcontainer.appendChild(div);
                }

        }
    };
    xhttp_esp.open("GET", "http://localhost:8080/setting/esp/free", true);
    xhttp_esp.send();

};
