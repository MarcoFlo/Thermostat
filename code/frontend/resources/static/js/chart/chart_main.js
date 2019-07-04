window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};


var barChartData = {
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
    datasets: [{
        label: 'Dataset 1',
        backgroundColor: "rgba(255,99,132,0.2)",
        borderColor: "rgba(255,99,132,0.2)",
        borderWidth: 1,
        data: [6, 5, 2, 8, 5, 5, 4]
    }, {
        label: 'Dataset 2',
        backgroundColor: "rgba(0,255,152,0.2)",
        borderColor: "rgba(0,255,152,0.2)",
        borderWidth: 1,
        data: [6, 5, 2, 8, 5, 5, 4]
    }]

};
window.onload = function () {
    var ctx = document.getElementById('myChart').getContext('2d');
    window.myBar = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
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
                    stacked: true,
                    gridLines: {
                        display: false
                    }
                }]
            }
        }
    });
};




