package websocket;

import core.Database;
import model.Comment;
import model.Upvote;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        for (Shout shout : database.getShouts(session.getConnection())) {
            JsonObject addMessage = createAddShoutMessage(session.getSession(), shout);
            sendToSession(session.getSession(), addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.get(session.getId()).getConnection().close();
        sessions.remove(session.getId());
    }

    public void addShout(Session session, Shout shout) {
        database.addShout(sessions.get(session.getId()).getConnection(), shout);

        JsonObject addMessage = createAddShoutMessage(session, shout);
        sendToAllConnectedSessions(addMessage);
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

    private JsonObject createAddShoutMessage(Session session, Shout shout) {
        JsonProvider provider = JsonProvider.provider();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        List<Comment> comments = database.getComments(sessions.get(session.getId()).getConnection(), shout.getId());

        JsonObjectBuilder builder = provider.createObjectBuilder()
                .add("action", "addShout")
                .add("email", shout.getUser().getEmail())
                .add("date", df.format(shout.getDate()))
                .add("content", shout.getContent())
                .add("image", shout.getImage())
                .add("id", shout.getId())
                .add("canUpvote", database.canUserUpvote(sessions.get(session.getId()).getConnection(), sessions.get(session.getId()).getUser().getId(), shout.getId()))
                .add("upvotes", database.getUpvotes(sessions.get(session.getId()).getConnection(), shout.getId()));

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