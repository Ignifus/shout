package websocket;

import core.Db;
import core.LoginManager;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
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
    private Db db;

    private volatile String email;

    @OnOpen
    public void open(@PathParam("email") String email, Session session) {
        this.email = email;

        if (db.isUserAuthenticated(email))
            handler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        handler.removeSession(session);

        db.deauthUser(email);
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
                Shout shout = new Shout(db.getUser(email), new Date(), jsonMessage.getString("content"));
                handler.addShout(shout);
            }
        }
    }
}