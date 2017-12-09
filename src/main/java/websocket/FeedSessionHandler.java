package websocket;

import core.Database;
import model.Comment;
import model.Follow;
import model.Upvote;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.persistence.EntityManager;
import javax.websocket.Session;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FeedSessionHandler {

    @Inject
    private Database database;

    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    public WebSocketSession getSession(Session session) {
        return sessions.get(session.getId());
    }

    public void addSession(WebSocketSession session) {
        sessions.put(session.getSession().getId(), session);

        JsonObject addMessage = createAddShoutsMessage(session.getSession(), database.getShouts(session.getConnection()), false);
        sendToSession(session.getSession(), addMessage);
    }

    public void removeSession(Session session) {
        sessions.get(session.getId()).getConnection().close();
        sessions.remove(session.getId());
    }

    public void addShout(Session session, Shout shout) {
        database.addShout(sessions.get(session.getId()).getConnection(), shout);

        List<Shout> shouts = new ArrayList<>();
        shouts.add(shout);
        JsonObject addMessage = createAddShoutsMessage(session, shouts, false);
        sendToAllConnectedSessions(addMessage);
    }

    public void filterShouts(WebSocketSession session, int userId, String filter) {
        switch(filter) {
            case "allShouts":
                JsonObject addShoutsMessage = createAddShoutsMessage(session.getSession(), database.getShouts(session.getConnection()), true);
                sendToSession(session.getSession(), addShoutsMessage);
                break;
            case "followingShouts":
                List<Shout> shouts = new ArrayList<>();
                for(Follow f : database.getFollows(session.getConnection(), session.getUser().getId())) {
                    shouts.addAll(database.getShouts(session.getConnection(), f.getFollowedUser().getId()));
                }
                JsonObject addFollowingShoutsMessage = createAddShoutsMessage(session.getSession(), shouts, true);
                sendToSession(session.getSession(), addFollowingShoutsMessage);
                break;
            case "userShouts":
                JsonObject addUserShoutsMessage = createAddShoutsMessage(session.getSession(), database.getShouts(session.getConnection(), userId), true);
                sendToSession(session.getSession(), addUserShoutsMessage);
                break;
        }
    }

    public void addUpvote(Session session, Upvote upvote) {
        database.addUpvote(sessions.get(session.getId()).getConnection(), upvote);

        JsonObject addUpvoteMessage = createAddUpvoteMessage(session, upvote);
        sendToAllConnectedSessions(addUpvoteMessage);
    }

    public void addComment(Session session, Comment comment) {
        database.addComment(sessions.get(session.getId()).getConnection(), comment);

        JsonObject addMessage = createAddCommentMessage(comment);
        sendToAllConnectedSessions(addMessage);
    }

    public void sendError(Session session, String error) {
        JsonObject addMessage = createErrorMessage(error);
        sendToSession(session, addMessage);
    }

    private JsonObject createErrorMessage(String error) {
        JsonProvider provider = JsonProvider.provider();
        JsonObjectBuilder builder = provider.createObjectBuilder()
                .add("action", "error")
                .add("error", error);
        return builder.build();
    }

    private JsonObject createAddShoutsMessage(Session session, List<Shout> shouts, boolean isFilter) {
        JsonObjectBuilder builder = JsonProvider.provider().createObjectBuilder();
        JsonArrayBuilder shoutArray = Json.createArrayBuilder();
        for (Shout shout : shouts) {
            shoutArray.add(createAddShoutMessage(session, shout, isFilter));
        }
        builder.add("shouts", shoutArray)
                .add("isFilter", isFilter)
                .add("action", "addShout");
        return builder.build();
    }

    private JsonObject createAddShoutMessage(Session session, Shout shout, boolean isFilter) {
        JsonProvider provider = JsonProvider.provider();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        EntityManager conn = sessions.get(session.getId()).getConnection();
        List<Comment> comments = database.getComments(conn, shout.getId());

        JsonObjectBuilder builder = provider.createObjectBuilder()
                .add("email", shout.getUser().getEmail())
                .add("date", df.format(shout.getDate()))
                .add("content", shout.getContent())
                .add("image", shout.getImage())
                .add("id", shout.getId())
                .add("canUpvote", database.canUserUpvote(conn, sessions.get(session.getId()).getUser().getId(), shout.getId()))
                .add("canFollow", database.followCount(conn, sessions.get(session.getId()).getUser().getId(), shout.getUser().getEmail()) == 0)
                .add("upvotes", database.getUpvotes(conn, shout.getId()));

        JsonArrayBuilder commentArray = Json.createArrayBuilder();

        for (Comment comment : comments) {
            commentArray.add(createAddCommentMessage(comment));
        }

        builder.add("comments", commentArray);

        return builder.build();
    }

    private JsonObject createAddUpvoteMessage(Session session, Upvote upvote) {
        return JsonProvider.provider().createObjectBuilder()
                .add("action", "addUpvote")
                .add("shout_id", upvote.getShout().getId())
                .build();
    }

    private JsonObject createAddCommentMessage(Comment comment) {
        JsonProvider provider = JsonProvider.provider();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return provider.createObjectBuilder()
                .add("action", "addComment")
                .add("email", comment.getUser().getEmail())
                .add("date", df.format(comment.getDate()))
                .add("content", comment.getContent())
                .add("shout_id", comment.getShout().getId())
                .build();
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        for (Map.Entry<String, WebSocketSession> session : sessions.entrySet()) {
            sendToSession(session.getValue().getSession(), message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session.getId());
            Logger.getLogger(FeedSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}