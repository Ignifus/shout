package controller;

import core.Database;
import core.Security;
import model.User;

import javax.enterprise.context.RequestScoped;
import java.util.List;

@RequestScoped
public class UserController {

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
            return u;
        }
        else {
            return null;
        }
    }

    public List<User> getUsers() {
        return database.getUsers();
    }
}
