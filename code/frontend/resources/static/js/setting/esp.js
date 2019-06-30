function requestListFreeEsp(espSelectedList) {
    var espcontainer = document.getElementById("esp-container");
    while (espcontainer.firstChild) {
        espcontainer.removeChild(espcontainer.firstChild);
    }
    var xhttp_esp = new XMLHttpRequest();
    xhttp_esp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            var espList = JSON.parse(xhttp_esp.responseText);
console.log(espList.toString());

            for (var i = 0; i < espList.length; i++) {
                var div = document.createElement("div");
                div.setAttribute("class", "col-auto p-1");

                var button = document.createElement("button");
                button.setAttribute("type", "button");
                button.setAttribute("class", "btn btn-secondary");
                button.setAttribute("name", "esp-button");
                button.onclick = toggleButton;
                button.innerHTML = espList[i];
                button.setAttribute("id", espList[i]);

                div.appendChild(button);
                espcontainer.appendChild(div);
            }

            if (espSelectedList !== null) {
                console.log(espSelectedList.toString());
                for (var i = 0; i < espSelectedList.length; i++) {
                    var div = document.createElement("div");
                    div.setAttribute("class", "col-auto p-1");

                    var button = document.createElement("button");
                    button.setAttribute("type", "button");
                    button.setAttribute("class", "btn btn-primary");
                    button.setAttribute("name", "esp-button");
                    button.onclick = toggleButton;
                    button.innerHTML = espSelectedList[i];
                    button.setAttribute("id", espSelectedList[i]);

                    div.appendChild(button);
                    espcontainer.appendChild(div);
                }
            }

        }
    };
    xhttp_esp.open("GET", "http://localhost:8080/setting/esp/free", true);
    xhttp_esp.send();
}
