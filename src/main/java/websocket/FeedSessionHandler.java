package websocket;

import controller.ShoutController;
import core.Db;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class FeedSessionHandler {
    @Inject
    private Db db;

    private final Set<Session> sessions = new HashSet<>();

    public void addSession(Session session) {
        sessions.add(session);

        for (Shout shout : db.getShouts()) {
            JsonObject addMessage = createAddMessage(shout);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<Shout> getShouts() {
        return new ArrayList<>(db.getShouts());
    }

    public void addShout(Shout shout) {
        db.addShout(shout);

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
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(FeedSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}