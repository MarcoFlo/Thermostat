// function handleSummerWinter(room, id) {
//     var summer = document.getElementById("Summer");
//     var winter = document.getElementById("Winter");
//     if (id === "Summer") {
//         if (summer.classList.contains("btn-primary")) {
//             nest.hvac_state = 'heating';
//             summer.className = "btn btn-secondary m-1";
//             winter.className = "btn btn-primary m-1";
//         } else {
//             nest.hvac_state = 'cooling';
//             winter.className = "btn btn-secondary m-1";
//             summer.className = "btn btn-primary m-1";
//         }
//     } else if (id === "Winter") {
//         if (winter.classList.contains("btn-primary")) {
//             nest.hvac_state = 'cooling';
//             winter.className = "btn btn-secondary m-1";
//             summer.className = "btn btn-primary m-1";
//         } else {
//             nest.hvac_state = 'heating';
//             summer.className = "btn btn-secondary m-1";
//             winter.className = "btn btn-primary m-1";
//         }
//     }
//     var xhr = new XMLHttpRequest();
//     xhr.open("POST", 'http://localhost:8080/temperature/wsa', true);
//     xhr.setRequestHeader("Content-Type", "application/json");
//     if (winter.classList.contains("btn-primary")) {
//         nest.hvac_state = 'heating';
//         xhr.send("winter");
//
//     } else {
//         nest.hvac_state = 'cooling';
//         xhr.send("summer");
//     }
// }
//
// function handleAntiFreeze(room) {
//     var manual = document.getElementById("Manual");
//     manual.className = "btn btn-secondary m-1";
//
//     var summer = document.getElementById("Summer");
//     summer.className = "btn btn-secondary m-1";
//     var winter = document.getElementById("Winter");
//     winter.className = "btn btn-primary m-1";
//
//     nest.hvac_state = 'heating';
//
//     document.getElementById("main-container").addEventListener('mousedown', dragStart);
//     document.getElementById("main-container").addEventListener('touchstart', dragStart);
//
//     document.getElementById("main-container").addEventListener('mouseup', dragEnd);
//     document.getElementById("main-container").addEventListener('mouseleave', dragEnd);
//     document.getElementById("main-container").addEventListener('touchend', dragEnd);
//
//     document.getElementById("main-container").addEventListener('mousemove', dragMove);
//     document.getElementById("main-container").addEventListener('touchmove', dragMove);
//
// }
//
// function handleManual(room) {
//     var manual = document.getElementById("Manual");
//     var xhr = new XMLHttpRequest();
//
//
//     if (manual.classList.contains("btn-primary")) {
//         //programmed
//         nest.target_temperature = nest.ambient_temperature;
//
//         manual.className = "btn btn-secondary m-1";
//
//         xhr.open("POST", 'http://localhost:8080/temperature/programmed', true);
//         xhr.setRequestHeader("Content-Type", "application/json");
//         xhr.send(room);
//     } else {
//         //manual
//         manual.className = "btn btn-primary m-1";
//
//         var manualResouce = {idRoom: room, desiredTemperature: nest.target_temperature};
//
//         xhr.open("POST", 'http://localhost:8080/temperature/manual', true);
//         xhr.setRequestHeader("Content-Type", "application/json");
//         xhr.send(JSON.stringify(manualResouce));
//     }
// }
//
// function toggleWSAL() {
//     var room = document.getElementById("room_name").innerHTML;
//
//     switch (this.id) {
//         case "Summer":
//         case "Winter":
//             handleSummerWinter(room, this.id);
//             break;
//         case "AntiFreeze":
//             handleAntiFreeze(room);
//             break;
//         case "Manual":
//             handleManual(room);
//             break;
//     }
// }
