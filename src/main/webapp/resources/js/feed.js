var url = new URL(window.location);
var email = url.searchParams.get("email");
var socket = new WebSocket("ws://localhost:8080/feed/" + email);
socket.onmessage = onMessage;

function onMessage(event) {
    var message = JSON.parse(event.data);
    if (message.action === "addShout") {
        printShout(message);
    }
    else if (message.action === "addComment") {
        printComment(message);
    }
}

function printShout(shout) {
    var content = document.getElementById("feed-container");

    var shoutDiv = document.createElement("div");

    var shoutDivUser = document.createElement("h4");
    shoutDivUser.textContent = shout.email + " dijo a las " + shout.date + ":";
    shoutDiv.appendChild(shoutDivUser);

    var shoutDivContent = document.createElement("p");
    var node = document.createTextNode(shout.content);
    shoutDivContent.appendChild(node);
    shoutDiv.appendChild(shoutDivContent);

    var shoutDivImage = document.createElement("img");
    shoutDivImage.setAttribute("src", shout.image);
    shoutDivImage.setAttribute("height", "150");
    if (shout.image === "")
        shoutDivImage.setAttribute("style", "display:none;");
    else
        shoutDivImage.setAttribute("style", "display:block;");
    shoutDiv.appendChild(shoutDivImage);

    shoutDiv.setAttribute("id", shout.id);

    var shoutCommentsTitle = document.createElement("h5");
    shoutCommentsTitle.textContent = "Comentarios";
    shoutDiv.appendChild(shoutCommentsTitle);

    var shoutCommentsDiv = document.createElement("div");
    shoutCommentsDiv.setAttribute("id", shout.id + "_comments");
    shoutDiv.appendChild(shoutCommentsDiv);

    var shoutCommentTextArea = document.createElement("textarea");
    shoutCommentTextArea.setAttribute("id", shout.id + "_comment");
    shoutCommentTextArea.setAttribute("rows", "2");
    shoutCommentTextArea.setAttribute("cols", "50");
    shoutCommentTextArea.setAttribute("style", "resize:none;");
    shoutDiv.appendChild(shoutCommentTextArea);

    var shoutCommentButton = document.createElement("button");
    shoutCommentButton.setAttribute("onClick", "comment(" + shout.id+ ")");
    shoutCommentButton.textContent = "Comentar";
    shoutDiv.appendChild(shoutCommentButton);

    content.appendChild(shoutDiv);

    for (var i = 0, len = shout.comments.length; i < len; i++) {
        printComment(shout.comments[i]);
    }
}

function printComment(comment) {
    var commentDiv = document.getElementById(comment.shout_id + "_comments");

    var commentUser = document.createElement("h5");
    commentUser.textContent = comment.email + " comento a las " + comment.date + ":";
    commentDiv.appendChild(commentUser);

    var commentContent = document.createElement("p");
    var node = document.createTextNode(comment.content);
    commentContent.appendChild(node);
    commentDiv.appendChild(commentContent);
}

function shout() {
    var content = document.getElementById("shout_content");
    var image = document.getElementById("shout-image-b64");
    addShout(content.value, image.innerHTML);
}

function comment(shoutId) {
    var content = document.getElementById(shoutId + "_comment");
    addComment(content.value, shoutId);
}

function addShout(content, image) {
    var ShoutAction = {
        action: "addShout",
        content: content,
        image: image
    };
    socket.send(JSON.stringify(ShoutAction));

    document.getElementById("shout-image-input").value = "";
    document.getElementById("shout_content").value = "";
}

function addComment(content, shoutId) {
    var CommentAction = {
        action: "addComment",
        content: content,
        shout_id: shoutId
    };
    socket.send(JSON.stringify(CommentAction));
}