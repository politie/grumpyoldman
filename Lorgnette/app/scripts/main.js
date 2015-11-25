var sock = new SockJS('http://localhost:8080/tweets');
sock.onopen = function () {
    document.getElementById('tweets').innerHTML = 'Connecting...';
};
sock.onmessage = function (e) {
    document.getElementById('tweets').innerHTML = e.data;
};
sock.onclose = function () {
    document.getElementById('tweets').innerHTML = "Server closed connection or hasn't been started";
};