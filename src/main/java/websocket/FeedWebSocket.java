package websocket;

import core.Database;
import model.Comment;
import model.Upvote;
import model.Shout;
import model.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/feed/{email}", configurator = EndpointConfigurator.class)
public class FeedWebSocket {

    @Inject
    private FeedSessionHandler handler;

    @Inject
    private Database database;

    @OnOpen
    public void open(@PathParam("email") String email, Session session, EndpointConfig ec) {
        EntityManager connection = database.getConnection();

        User user = database.getUser(connection, email);
        String wskey = (String)ec.getUserProperties().get("wskey");

        if (user != null) {
            if (wskey.equals(user.getWskey()))
                handler.addSession(new WebSocketSession(user, connection, session));
            else {
                connection.close();
            }
        }
        else {
            connection.close();
        }

        // TODO: Else, redirect to http context
    }

    @OnClose
    public void close(Session session) {
        handler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(FeedWebSocket.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            WebSocketSession webSocketSession = handler.getSession(session);

            switch(jsonMessage.getString("action")) {
                case "addShout":
                    String shoutContent = jsonMessage.getString("content");

                    if (shoutContent == null || shoutContent.isEmpty())
                    {
                        handler.sendError(session, "Por favor ingrese contenido.");
                        break;
                    }

                    Shout shout = new Shout(
                            database.getUser(webSocketSession.getConnection(), webSocketSession.getUser().getEmail()),
                            new Date(),
                            shoutContent,
                            jsonMessage.getString("image"));
                    handler.addShout(session, shout);
                    break;
                case "addUpvote":
                    Upvote upvote = new Upvote(
                            database.getUser(webSocketSession.getConnection(), webSocketSession.getUser().getEmail()),
                            database.getShout(webSocketSession.getConnection(), jsonMessage.getInt("shout_id"))
                    );
                    handler.addUpvote(session, upvote);
                    break;
                case "addComment":
                    String commentContent = jsonMessage.getString("content");

                    if (commentContent == null || commentContent.isEmpty())
                    {
                        handler.sendError(session, "Por favor ingrese contenido.");
                        break;
                    }

                    Comment comment = new Comment(database.getUser(
                            webSocketSession.getConnection(), webSocketSession.getUser().getEmail()),
                            database.getShout(webSocketSession.getConnection(), jsonMessage.getInt("shout_id")),
                            new Date(),
                            commentContent);
                    handler.addComment(session, comment);
                    break;
            }
        }
    }
}