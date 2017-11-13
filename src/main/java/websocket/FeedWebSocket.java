package websocket;

import core.Database;
import model.Comment;
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

@ServerEndpoint("/feed/{email}")
public class FeedWebSocket {

    @Inject
    private FeedSessionHandler handler;

    @Inject
    private Database database;

    @OnOpen
    public void open(@PathParam("email") String email, Session session) {
        EntityManager connection = database.getConnection();

        User user = database.getUser(connection, email);

        if (user != null) {
            if (user.isAuthenticated())
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
                    Shout shout = new Shout(
                            database.getUser(webSocketSession.getConnection(), webSocketSession.getUser().getEmail()),
                            new Date(),
                            jsonMessage.getString("content"));
                    handler.addShout(session, shout);
                    break;
                case "addComment":
                    Comment comment = new Comment(database.getUser(
                            webSocketSession.getConnection(), webSocketSession.getUser().getEmail()),
                            database.getShout(webSocketSession.getConnection(), jsonMessage.getInt("shout_id")),
                            new Date(),
                            jsonMessage.getString("content"));
                    handler.addComment(session, comment);
                    break;
            }
        }
    }
}