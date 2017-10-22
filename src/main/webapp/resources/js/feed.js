var url = new URL(window.location);
var email = url.searchParams.get("email");
var socket = new WebSocket("ws://localhost:8080/feed/" + email);
socket.onmessage = onMessage;

function onMessage(event) {
    var shout = JSON.parse(event.data);
    if (shout.action === "add") {
        printShout(shout);
    }
}

function printShout(shout) {
    var content = document.getElementById("feed-container");

    var shoutDiv = document.createElement("div");

    var shoutDivUser = document.createElement("h4");
    shoutDivUser.textContent = shout.email + " dijo:";
    shoutDiv.appendChild(shoutDivUser);

    var shoutDivContent = document.createElement("p");
    var node = document.createTextNode(shout.content);
    shoutDivContent.appendChild(node);
    shoutDiv.appendChild(shoutDivContent);

    var shoutDivDate = document.createElement("p");
    var node2 = document.createTextNode(shout.date);
    shoutDivDate.appendChild(node2);
    shoutDiv.appendChild(shoutDivDate);

    content.appendChild(shoutDiv);
}

function shout() {
    var content = document.getElementById("shout_content");
    addShout(content.value);
}

function addShout(content) {
    var ShoutAction = {
        action: "add",
        content: content
    };
    socket.send(JSON.stringify(ShoutAction));
}