window.onload = function () {
    document.getElementById("save").addEventListener("click", savePhoneForm);
    document.getElementById("reset").addEventListener("click", resetPhoneForm);

    document.getElementById("weekend").addEventListener("click", toggleButton);

    document.getElementById("time-slice-select").addEventListener("change", saveSliceData);
    document.getElementById("weekend").addEventListener("click", saveSliceData);

    document.getElementById("room-select").addEventListener("change", resetPhoneForm);

    document.getElementById("plus-button").addEventListener("click", addNewRoom);

    setUpRoomSelect();


};
