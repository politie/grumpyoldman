$(function () {
    var that = this;
    this.count = 0;

    var ctx = $("#chart").get(0).getContext("2d");

    var data = {
        labels: ["January", "February", "March", "April", "May", "June", "July"],
        datasets: [
            {
                label: "Polarity index",
                fillColor: "rgba(220,220,220,0)",
                strokeColor: "#003354",
                pointColor: "#003354",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(220,220,220,1)",
                data: [65, 59, 80, 81, 56, 55, 40]
            }
        ]
    };
    var lineChart = new Chart(ctx).Line(data);

    var sock = new SockJS('/tweets');
    sock.onmessage = function (e) {
        that.count++;
    };

    window.setInterval(function () {
        $("#counter").text(that.count);
    }, 1000 / 25);


});
