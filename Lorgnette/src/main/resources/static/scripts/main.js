$(function () {
    var that = this;
    this.count = 0;
    this.frame = 100 / 25;
    this.sentiment = [];


    var sock = new SockJS('/tweets');
    sock.onmessage = function (e) {
        $("#tweets").prepend($('<div class="bubble"><span class="tweet">' + e.data + '</span></div>'));
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
    var lineChart = new Chart(ctx, {"animationSteps": 5}).Line(data);

    function loadData() {
        $.getJSON('/analysis/dummy', function (data) {
            $.each(data, function () {
                var date = new Date(this.timestamp);
                var label = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                lineChart.addData([this.sentiment], label);
            });
            lineChart.removeData();

        });
    }

    window.setInterval(function () {
        $("#counter").text(that.count);
    }, 1000 / 25);

    window.setInterval(function () {
        loadData();
    }, 2000);
});
