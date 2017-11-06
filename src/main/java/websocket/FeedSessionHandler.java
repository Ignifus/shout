package websocket;

import core.Database;
import model.Shout;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FeedSessionHandler {

    private Database database = new Database();
    private final Set<Session> sessions = new HashSet<>();

    public void addSession(Session session) {
        sessions.add(session);

        for (Shout shout : database.getShouts()) {
            JsonObject addMessage = createAddMessage(shout);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public List<Shout> getShouts() {
        return new ArrayList<>(database.getShouts());
    }

    public void addShout(Shout shout) {
        database.addShout(shout);

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