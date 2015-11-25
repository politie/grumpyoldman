$(function () {
    var that = this;
    this.count = 0;
    this.frame = 100 / 25;


    var sock = new SockJS('/tweets');
    sock.onmessage = function (e) {
        that.count++;
    };

    var ctx = $("#chart").get(0).getContext("2d");

    var data = {
        labels: [],
        datasets: [
            {
                label: "Polarity index",
                fillColor: "rgba(220,220,220,0)",
                strokeColor: "#003354",
                pointColor: "#003354",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(220,220,220,1)",
                data: []
            }
        ]
    };
    var lineChart = new Chart(ctx).Line(data,{animationSteps: 10});
    loadData();

    function loadData() {
        var epoch = new Date();
        var label = epoch.getHours()+":"+epoch.getMinutes()+":00";
        lineChart.addData([Math.random() * 100],label);
        if(lineChart.datasets[0].points.length >40){
            lineChart.removeData();
            lineChart.l
        }

    }

    window.setInterval(function () {
        loadData();
        $("#counter").text(that.count);
    }, this.frame);


});
