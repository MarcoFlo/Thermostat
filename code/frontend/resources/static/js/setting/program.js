var sliceArr = ["Wake", "Leave", "Back", "Sleep"];
var sliceList = [new Map(), new Map()];
var time_slice_select = sliceArr[0];
var week_pos = 0;


function getWeekPos() {
    var weekPos = 0;
    if (document.getElementById("weekend").classList.contains("btn-primary"))
        weekPos = 1;

    return weekPos;
}

function checkClockValidity() {

    var start_time_help_block = document.getElementById("start-time-help-block");
    var clock = document.getElementById("start-time-input");

    if (clock.value.length === 5 && clock.value.indexOf(":") === 2) {
        var start_time = new Date("2019-01-01T" + clock.value);

        var time_slice_select_index = sliceArr.indexOf(time_slice_select);
        var weekPos = getWeekPos();


        if (checkTimeBoundary(time_slice_select_index, start_time, weekPos)) {
            console.log("Selected time okay");

            clock.classList.remove('border-danger');
            start_time_help_block.innerHTML = "";
            start_time_help_block.className = "d-none";
            return true;

        } else {
            console.log("Selected time not okay");

            clock.classList.add('border-danger');
            start_time_help_block.innerHTML = getSliceErrorString(time_slice_select, time_slice_select_index);
            start_time_help_block.className = "alert alert-warning p-1 w-100";
            return false;
        }
    } else if (clock.value.length > 0) {
        console.log("invalid hour");
        clock.classList.add('border-danger');
        start_time_help_block.innerHTML = "Insert a valid time: HH:mm";
        start_time_help_block.className = "alert alert-warning p-1 w-100";
        return false;
    }

}

function saveSliceData() {
    var clock = document.getElementById("start-time-input");
    if (checkClockValidity() === true) {
        var start_time = new Date("2019-01-01T" + clock.value);

        var val = new HourlyProgram(start_time, document.getElementById("temperature").value);
        sliceList[week_pos].set(time_slice_select, val);
        console.log("Saved -> " + JSON.stringify(val));
    }
    time_slice_select = document.getElementById("time-slice-select").value;
    week_pos = getWeekPos();

    resetClockTemperature();

}

function resetClockTemperature() {
    console.log("reset clock & temperature");
    var weekPos = getWeekPos();
    console.log(time_slice_select);

    var resetValue = sliceList[weekPos].get(time_slice_select);
    var clock = document.getElementById("start-time-input");
    clock.classList.remove('border-danger');

    var start_time_help_block = document.getElementById("start-time-help-block");
    start_time_help_block.innerHTML = "";
    start_time_help_block.className = "d-none";

    if (resetValue !== undefined) {
        clock.value = getTimeFromDate(resetValue.time);
        $('#temperature').val(resetValue.temperature);
    } else {
        clock.value = "";
        $('#temperature').val(20);
    }

}

function checkTimeBoundary(current_slice_index, current_start_time, weekPos) {
    var before = getSliceBefore(current_slice_index, weekPos);
    var after = getSliceAfter(current_slice_index, weekPos);
    if (current_slice_index === 0)
        before = sliceList[weekPos].get(sliceArr[sliceArr.length]);

    if (current_slice_index === sliceArr.length)
        after = sliceList[weekPos].get(sliceArr[0]);

    if (before !== undefined)
        console.log("before -> " + before);
    if (after !== undefined)
        console.log("after -> " + after);

    return (before === undefined || (current_start_time.getTime() - before.getTime()) > 0) && (after === undefined || (after.getTime() - current_start_time.getTime()) > 0);
}

function getSliceBefore(current_slice_index, weekPos) {

    var before = sliceList[weekPos].get(sliceArr[current_slice_index - 1]);
    if (current_slice_index === 0)
        before = sliceList[weekPos].get(sliceArr[sliceArr.length]);

    return before !== undefined ? before.time : undefined;

}

function getSliceAfter(current_slice_index, weekPos) {
    var after = sliceList[weekPos].get(sliceArr[current_slice_index + 1]);
    if (current_slice_index === sliceArr.length)
        after = sliceList[weekPos].get(sliceArr[0]);

    return after !== undefined ? after.time : undefined;
}

function getSliceErrorString(time_slice_select, time_slice_select_index) {
    var weekPos = 0;
    if (document.getElementById("weekend").classList.contains("btn-primary"))
        weekPos = 1;

    console.log(weekPos);
    var before = getSliceBefore(time_slice_select_index, weekPos);
    var after = getSliceAfter(time_slice_select_index, weekPos);
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


