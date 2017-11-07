package websocket;

import core.Database;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
            JsonObject addMessage = createAddMessage(shout);
            sendToSession(session.getSession(), addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.get(session.getId()).getConnection().close();
        sessions.remove(session.getId());
    }

    public void addShout(Session session, Shout shout) {
        database.addShout(sessions.get(session.getId()).getConnection(), shout);

        JsonObject addMessage = createAddMessage(shout);
        sendToAllConnectedSessions(addMessage);
    }

    private JsonObject createAddMessage(Shout shout) {
        JsonProvider provider = JsonProvider.provider();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return provider.createObjectBuilder()
                .add("action", "add")
                .add("email", shout.getUser().getEmail())
                .add("date", df.format(shout.getDate()))
                .add("content", shout.getContent())
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