var socket;

$('document').ready(function(){
    socket = new WebSocket("ws://localhost:8080/feed/" + $("#userMail").val(), $("#userKey").val());
    socket.onmessage = onMessage;

    $('#searchUser').on('keypress', function(e) {searchUser(e);});
});

function displayNotification(message) {
    $("#notification-text").text(message);
    $(".notification").fadeIn().delay(3000).fadeOut();
}

function onMessage(event) {
    var message = JSON.parse(event.data);
    switch(message.action) {
        case "addShout":
            if (message.isFilter) {
                $("#feed-container").empty();
            }

            for (var i = 0, len = message.shouts.length; i < len; i++) {
                var shout = message.shouts[i];
                if (shouldPrint(shout))
                    printShout(shout);
            }
            break;
        case "addUpvote":
            printUpvote(message);
            break;
        case "addComment":
            printComment(message);
            break;
        case "error":
            displayNotification(message.error);
            break;
    }
}

function shouldPrint(shout) {
    var currentFilter = $("#shout-filter").val();
    var isUserFilter = $("#searchUser").val() !== "";

    if (isUserFilter) {
        return shout.email === $("#searchUser").val();
    }

    switch (currentFilter) {
        case "allShouts":
            return true;
            break;
        case "followingShouts":
            return true;
            break;
        case "userShouts":
            return shout.email === $("#userMail").val();
            break;
    }

    return true;
}

function printShout(shout) {
    var content = $("#feed-container");

    var shoutDiv = $("<div>", {id: shout.id});

    var shoutDivUser = $("<h5>", {text: shout.email + " dijo a las " + shout.date + ":"});
    shoutDiv.append(shoutDivUser);

    var shoutDivContent = $("<p>", {text: shout.content});
    shoutDiv.append(shoutDivContent);

    var shoutDivImage = $("<img>", {src: shout.image});
    shoutDivImage.css("height", "150");
    if (shout.image === "")
        shoutDivImage.css("display", "none");
    else
        shoutDivImage.css("style", "block");
    shoutDiv.append(shoutDivImage);

    var shoutUpvotes = $("<p>", {html: '<span id="' + shout.id + '_upvotes">' + shout.upvotes + '</span> me gusta'});
    shoutDiv.append(shoutUpvotes);

    var shoutCommentsTitle = $("<h5>", {text: "Comentarios"});
    shoutDiv.append(shoutCommentsTitle);

    var shoutCommentsDiv = $("<div>", {id: shout.id + "_comments"});
    shoutDiv.append(shoutCommentsDiv);

    var shoutCommentTextArea = $("<textarea>", {id: shout.id + "_comment", rows: "2", cols: "50", style: "resize:none;"});
    shoutDiv.append(shoutCommentTextArea);

    var shoutCommentButton = $("<button>", {text: "Comentar", onClick: "comment(" + shout.id+ ")"});
    shoutDiv.append(shoutCommentButton);

    if (shout.canUpvote) {
        var shoutUpvoteButton = $("<button>", {id: shout.id + "_upvote_button", text: "Me Gusta", onClick: "upvote(" + shout.id+ ")"});
        shoutDiv.append(shoutUpvoteButton);
    }

    if (shout.email !== $("#userMail").val()) {
        if (shout.canFollow) {
            var shoutFollowButton = $("<button>", {id: shout.id + "_follow_button", text: "Seguir", onClick: "follow(" + shout.id + ", '" + shout.email + "')"});
            shoutDiv.append(shoutFollowButton);
        }
        else {
            var shoutUnfollowButton = $("<button>", {id: shout.id + "_unfollow_button", text: "Dejar de seguir", onClick: "unfollow(" + shout.id + ", '" + shout.email + "')"});
            shoutDiv.append(shoutUnfollowButton);
        }
    }

    content.append(shoutDiv);

    for (var i = 0, len = shout.comments.length; i < len; i++) {
        printComment(shout.comments[i]);
    }
}

function printUpvote(upvote) {
    var element = $("#" + upvote.shout_id + "_upvotes");
    var value = parseInt(element.text(), 10) + 1;
    element.text(value);
}

function printComment(comment) {
    var commentDiv = $("#" + comment.shout_id + "_comments");

    var commentUser = $("<h5>", {text: comment.email + " comento a las " + comment.date + ":"});
    commentDiv.append(commentUser);

    var commentContent = $("<p>", {text: comment.content});
    commentDiv.append(commentContent);
}

function shout() {
    var content = $("#shout_content");
    var image = $("#shout-image-b64");
    addShout(content.val(), image.html());
}

function upvote(shoutId) {
    addUpvote(shoutId);
}

function comment(shoutId) {
    var content = $("#" + shoutId + "_comment");
    addComment(content.val(), shoutId);
}

function addShout(content, image) {
    var ShoutAction = {
        action: "addShout",
        content: content,
        image: image
    };
    socket.send(JSON.stringify(ShoutAction));

    $("#shout-image-input").val("");
    $("#shout_content").val("");
    $('#shout-image').removeAttr('src');

    $("#shout-filter").val('userShouts').trigger('change');
}

function addUpvote(shoutId) {
    var UpvoteAction = {
        action: "addUpvote",
        shout_id: shoutId
    };
    socket.send(JSON.stringify(UpvoteAction));

    $('#' + shoutId + "_upvote_button").remove();
}

function addComment(content, shoutId) {
    var CommentAction = {
        action: "addComment",
        content: content,
        shout_id: shoutId
    };
    socket.send(JSON.stringify(CommentAction));
    $("#" + shoutId + "_comment").val("");
}

function searchUser(e) {
    if(e.which === 13){
        $("#shout-filter").val("Por usuario");

        var searchInput = $("#searchUser");

        $(this).attr("disabled", "disabled");
        search(searchInput.val());
        $(this).removeAttr("disabled");
    }
}

function search(user) {
    var SearchAction = {
        action: "searchUser",
        user: user
    };
    socket.send(JSON.stringify(SearchAction));
}

function filterShouts(select) {
    var FilterAction = {
        action: "filterShouts",
        filter: select.value
    };
    $("#searchUser").val("");
    socket.send(JSON.stringify(FilterAction));
}

function follow(shoutId, email) {
    var FollowAction = {
        action: "follow",
        user: email
    };
    $("#" + shoutId + "_follow_button").remove();
    $("#" + shoutId).append($("<button>", {id: shoutId + "_unfollow_button", text: "Dejar de seguir", onClick: "unfollow(" + shoutId + ", '" + email + "')"}));
    socket.send(JSON.stringify(FollowAction));
}

function unfollow(shoutId, email) {
    var UnfollowAction = {
        action: "unfollow",
        user: email
    };
    $("#" + shoutId + "_unfollow_button").remove();
    $("#" + shoutId).append($("<button>", {id: shoutId + "_follow_button", text: "Seguir", onClick: "follow(" + shoutId + ", '" + email + "')"}));
    socket.send(JSON.stringify(UnfollowAction));
}
