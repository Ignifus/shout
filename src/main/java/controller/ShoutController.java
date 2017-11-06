package controller;

import core.Database;
import model.Shout;

import javax.enterprise.context.RequestScoped;
import java.util.List;

@RequestScoped
public class ShoutController {
    private Database database = new Database();

    public List<Shout> getShouts() {
        return database.getShouts();
    }

    public void addShout(Shout shout) {
        database.addShout(shout);
    }
}
