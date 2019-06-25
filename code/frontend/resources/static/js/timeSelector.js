$('#start-time-input').change(function () {
    // TODO: time changed
    console.log("start time -> " + this.value);
});

$('#end-time-input').change(function () {
    // TODO: time changed
    console.log("end time -> " + this.value);
});

$('.clockpicker').clockpicker({
    align: 'left',
    donetext: 'Done',
    beforeDone: function() {
        console.log("before done");
    }
});

