var sliceArr = ["Wake", "Leave", "Return", "Sleep"];
var sliceMap = new Map();

$('#start-time-input').change(function () {
    // TODO: time changed
    console.log("start time -> " + this.value);
    var start_time = new Date("2019-01-01T" + this.value);
    console.log(start_time);
    var start_time_help_block = document.getElementById("start-time-help-block");

    var time_slice_select = document.getElementById("time-slice-select").value;
    var time_slice_select_index = sliceArr.indexOf(time_slice_select);


    if (checkTimeBoundary(time_slice_select_index, start_time)) {
        console.log("Selected time okay");
        sliceMap.set(time_slice_select, start_time);

        this.classList.remove('border-danger');
        start_time_help_block.innerHTML = "";
        start_time_help_block.className = "d-none";

    } else {
        console.log("Selected time not okay");
        this.classList.add('border-danger');

        start_time_help_block.innerHTML = getSliceErrorString(time_slice_select, time_slice_select_index);
        start_time_help_block.className = "alert alert-warning w-100 p-1";
    }

});

$('.clockpicker').clockpicker({
    align: 'left',
    donetext: 'Done',
    beforeDone: function () {
        console.log("before done");
    }
});

function checkTimeBoundary(current_slice_index, current_start_time) {
    var before = getSliceBefore(current_slice_index);
    var after = getSliceAfter(current_slice_index);
    if (current_slice_index === 0)
        before = sliceMap.get(sliceArr[sliceArr.length]);

    if (current_slice_index === sliceArr.length)
        after = sliceMap.get(sliceArr[0]);

    console.log(current_start_time.getTime());
    if (before !== undefined)
        console.log("before -> " + before);
    if (after !== undefined)
        console.log("after -> " + after);


    if ((before === undefined || (current_start_time.getTime() - before.getTime()) > 0) && (after === undefined || (after.getTime() - current_start_time.getTime()) > 0))
        return true;
    else
        return false;
}

function getSliceBefore(current_slice_index) {

    var before = sliceMap.get(sliceArr[current_slice_index - 1]);
    if (current_slice_index === 0)
        before = sliceMap.get(sliceArr[sliceArr.length]);

    return before;

}

function getSliceAfter(current_slice_index) {
    var after = sliceMap.get(sliceArr[current_slice_index + 1]);
    if (current_slice_index === sliceArr.length)
        after = sliceMap.get(sliceArr[0]);

    return after;
}

function getSliceErrorString(time_slice_select, time_slice_select_index) {
    var before = getSliceBefore(time_slice_select_index);
    var after = getSliceAfter(time_slice_select_index);
    var result = "The " + time_slice_select.toLowerCase() + " time";

    if (before !== undefined) {
        before = before.toTimeString();
        result += " should be after " + before.substring(0, (before.indexOf(" ") - 3));
        if (after !== undefined) {
            after = after.toTimeString();
            result += " and before " + after.substring(0, (after.indexOf(" ") - 3));
        }
    } else {
        if (after !== undefined) {
            after = after.toTimeString();
            result += " should be before " + after.substring(0, (after.indexOf(" ") - 3));
        }
    }
    return result;
}
