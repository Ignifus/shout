package websocket;

import core.Database;
import model.*;

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
            EntityManager conn = webSocketSession.getConnection();
            User user = webSocketSession.getUser();

            switch(jsonMessage.getString("action")) {
                case "addShout":
                    String shoutContent = jsonMessage.getString("content");

                    if (shoutContent == null || shoutContent.isEmpty())
                    {
                        handler.sendError(session, "Por favor ingrese contenido.");
                        break;
                    }

                    Shout shout = new Shout(
                            user,
                            new Date(),
                            shoutContent,
                            jsonMessage.getString("image"));
                    handler.addShout(session, shout);
                    break;
                case "filterShouts":
                    handler.filterShouts(webSocketSession, user.getId(), jsonMessage.getString("filter"));
                    break;
                case "searchUser":
                    try {
                        handler.filterShouts(webSocketSession, database.getUser(conn, jsonMessage.getString("user")).getId(), "userShouts");
                    }
                    catch (Exception e) {
                        handler.sendError(session, "Usuario no encontrado!");
                    }
                    break;
                case "follow":
                    try {
                        database.follow(conn, new Follow(user, database.getUser(conn, jsonMessage.getString("user"))));
                    }
                    catch (Exception e) {
                        handler.sendError(session, "Ya esta siguiendo a este usuario!");
                    }
                    break;
                case "unfollow":
                    try {
                        database.unfollow(conn, new Follow(user, database.getUser(conn, jsonMessage.getString("user"))));
                    }
                    catch (Exception e) {
                        handler.sendError(session, "No esta siguiendo a este usuario!");
                    }
                    break;
                case "addUpvote":
                    Upvote upvote = new Upvote(
                            user,
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

                    Comment comment = new Comment(
                            user,
                            database.getShout(webSocketSession.getConnection(), jsonMessage.getInt("shout_id")),
                            new Date(),
                            commentContent);
                    handler.addComment(session, comment);
                    break;
            }
        }
    }
}