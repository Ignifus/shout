package controller;

import core.Db;
import core.Security;
import model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Stateless
public class UserController implements Serializable {

    @Inject
    private Db db;

    private Security security = new Security();

    public void createUser(String username, String password, String email) {
        Security.Password sp = security.generatePassword(password);

        db.addUser(new User(username, sp.hash, sp.salt, email));
    }

    public List<User> getUsers() {
        return db.getUsers();
    }
}
