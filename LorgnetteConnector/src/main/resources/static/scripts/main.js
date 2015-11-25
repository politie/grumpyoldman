$(function () {
    var that = this;
    this.count = 0;

    var sock = new SockJS('http://localhost:8080/tweets');
    sock.onmessage = function (e) {
         $("#tweets").prepend($('<span></span>').addClass("bh-tweet-"+that.count).text(e.data));
        $("#counter").text(that.count);
        that.count++;
    };

    window.setInterval(function(){
        var c =that.count-15;
        var i = 0;
        while(c-i<0){
            $("bh-tweet-"+i).remove();
            i++;
        }
    }, 1000);

});
