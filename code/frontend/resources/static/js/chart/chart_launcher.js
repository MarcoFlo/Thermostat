function loadChart(labelArr, dataArr) {
    var ctx = document.getElementById('myChart').getContext('2d');
    window.myBar = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labelArr,
            datasets: [{
                label: 'On time',
                backgroundColor: "rgba(0,255,92,0.2)",
                borderColor: "rgba(0,255,89,0.2)",
                borderWidth: 1,
                data: dataArr[0]
            }, {
                label: 'Off time',
                backgroundColor: "rgba(255,0,20,0.2)",
                borderColor: "rgba(255,0,21,0.2)",
                borderWidth: 1,
                data: dataArr[1]
            }]

        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            legend: {
                display: 'top',
            },
            title: {
                display: false,
            },
            scales: {
                xAxes: [{
                    stacked: true,
                    gridLines: {
                        display: true,
                        color: "rgba(255,99,132,0.2)"
                    }
                }],
                yAxes: [{
                    display: false,
                    stacked: true,
                    gridLines: {
                        display: false
                    }
                }]
            }
        }
    });
}

function getStats() {
    var xhttp_stats = new XMLHttpRequest();
    xhttp_stats.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var response = JSON.parse(xhttp_stats.responseText);
            // console.log(response.dayList + "\n" + response.dataList[0] + "\n" + response.dataList[1]);
            if (response.dayList.length !== 0)
                loadChart(response.dayList, response.dataList);
            else
                console.log("No stats"); //TODO setup
        }
    };
    xhttp_stats.open("GET", "http://localhost:8080/setting/stats/" + "Kitchen", true);
    xhttp_stats.send();
}
