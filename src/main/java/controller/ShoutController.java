package controller;

import core.Database;
import model.Shout;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;

@SessionScoped
public class ShoutController implements Serializable {
    private Database database = new Database();

    public List<Shout> getShouts() {
        return database.getShouts();
    }

    public void addShout(Shout shout) {
        database.addShout(shout);
    }
}
