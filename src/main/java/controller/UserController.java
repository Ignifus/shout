package controller;

import core.Db;
import core.Security;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;

@RequestScoped
public class UserController {

    @Inject
    private Db db;

    private Security security = new Security();

    public void createUser(String email, String password) {
        Security.Password sp = security.generatePassword(password, null);

        db.addUser(new User(email, sp.hash, sp.salt));
    }

    public User login(String email, String password) {
        User u = db.getUser(email);

        if (u == null)
            return null;

        if(security.verifyPassword(password, new Security.Password(u.getHash(), u.getSalt())))
        {
            db.authUser(email);
            return u;
        }
        else
            return null;
    }

    public List<User> getUsers() {
        return db.getUsers();
    }
}
