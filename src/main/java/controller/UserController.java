package controller;

import core.Database;
import core.LoginManager;
import core.Security;
import model.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@SessionScoped
public class UserController implements Serializable {
    @Inject
    private LoginManager loginManager;

    private Database database = new Database();
    private Security security = new Security();

    public void createUser(String email, String password) {
        Security.Password sp = security.generatePassword(password, null);

        database.addUser(new User(email, sp.hash, sp.salt));
    }

    public User login(String email, String password) {
        User u = database.getUser(email);

        if (u == null)
            return null;

        if(security.verifyPassword(password, new Security.Password(u.getHash(), u.getSalt())))
        {
            database.authUser(email, true);
            loginManager.setCurrentUser(u);
            return u;
        }
        else {
            return null;
        }
    }

    public void logout() {
        database.authUser(loginManager.getCurrentUser().getEmail(), false);
    }

    public List<User> getUsers() {
        return database.getUsers();
    }
}
