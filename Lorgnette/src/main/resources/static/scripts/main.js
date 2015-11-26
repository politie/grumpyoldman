$(function () {
    var that = this;
    this.count = 0;
    this.frame = 100 / 25;
    this.sentiment = [];
    this.labels = [];


    var sock = new SockJS('/tweets');
    sock.onmessage = function (e) {
        that.count++;
    };

    var ctx = $("#chart").get(0).getContext("2d");

    this.data = {
        labels: this.labels,
        datasets: [
            {
                label: "Polarity index",
                fillColor: "rgba(220,220,220,0)",
                strokeColor: "#003354",
                pointColor: "#003354",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(220,220,220,1)",
                data: this.sentiment
            }
        ]
    };
    var lineChart = new Chart(ctx);

    function loadData() {
        $.getJSON('/analysis/sentiment-time-all', function (data) {
            $.each(data, function () {
                var date = new Date(this.timestamp);
                var label = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                that.labels.push(label);
                that.sentiment.push(this.sentiment);
                if (that.sentiment.length > 40) {
                    that.sentiment.shift();
                    that.labels.shift();
                }
            });
            lineChart.Line(that.data);
        });


    }


    window.setInterval(function () {
        loadData();
        $("#counter").text(that.count);
    }, 1000 / 25);

    window.setInterval(function () {
        loadData();
    }, 5000);
});
