package websocket;

import model.User;

import javax.persistence.EntityManager;
import javax.websocket.Session;

public class WebSocketSession {
    private User user;
    private EntityManager connection;
    private Session session;

    public WebSocketSession(User user, EntityManager connection, Session session) {
        this.user = user;
        this.connection = connection;
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public EntityManager getConnection() {
        return connection;
    }

    public Session getSession() {
        return session;
    }
}
