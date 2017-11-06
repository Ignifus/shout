package websocket;

import core.Database;
import model.Shout;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
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

    private Database database = new Database();

    private volatile String email;

    @OnOpen
    public void open(@PathParam("email") String email, Session session) {
        this.email = email;

        if (database.getUser(email).isAuthenticated())
            handler.addSession(session);
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

            if ("add".equals(jsonMessage.getString("action"))) {
                Shout shout = new Shout(database.getUser(email), new Date(), jsonMessage.getString("content"));
                handler.addShout(shout);
            }
        }
    }
}