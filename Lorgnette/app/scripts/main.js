$(function(){
    var sock = new SockJS('http://localhost:8080/tweets');
    sock.onopen = function () {
 //       document.getElementById('tweets').innerHTML = 'Connecting...';
    };
    sock.onmessage = function (e) {
  //      document.getElementById('tweets').innerHTML = e.data;
        cosole.log(e.data);
        var tweets = $(".bh-tweet");
        var n = tweets.length;
        if(n>15) {
            tweets.pop();
        }

        var tweetElem = document.createElement("span");
        tweetElem.addStyleClass("bh-tweet");
        tweetElem.innerHTML = e.data;
        $("#tweets").prepend(tweetElem);
    };
    sock.onclose = function () {
 //       document.getElementById('tweets').innerHTML = "Server closed connection or hasn't been started";
    };

});
