package controller;

import core.Database;
import model.Shout;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@SessionScoped
public class ShoutController implements Serializable {

    @Inject
    private Database database;
    private EntityManager connection;

    @PostConstruct
    public void init() {
        connection = database.getConnection();
    }

    @PreDestroy
    public void destroy() {
        connection.close();
    }

    public List<Shout> getShouts() {
        return database.getShouts(connection);
    }

    public void addShout(Shout shout) {
        database.addShout(connection, shout);
    }
}
