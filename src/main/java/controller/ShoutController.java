package controller;

import core.Db;
import model.Shout;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
public class ShoutController {

    @Inject
    private Db db;

    public List<Shout> getShouts() {
        return db.getShouts();
    }

    public void addShout(Shout shout) {
        db.addShout(shout);
    }
}
