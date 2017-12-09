package controller;

import core.Database;
import core.LoginManager;
import core.Security;
import model.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@SessionScoped
public class UserController implements Serializable {
    @Inject
    private LoginManager loginManager;

    @Inject
    private Database database;
    private EntityManager connection;

    @Inject
    private Security security;

    @PostConstruct
    public void init() {
        connection = database.getConnection();
    }

    @PreDestroy
    public void destroy() {
        connection.close();
    }

    public User getCurrentUser() {
        return loginManager.getCurrentUser();
    }

    public void createUser(String email, String password) {
        Security.Password sp = security.generatePassword(password, null);

        database.addUser(connection, new User(email, sp.hash, sp.salt));
    }

    public User login(String email, String password) {
        User u = database.getUser(connection, email);

        if (u == null)
            return null;

        if(security.verifyPassword(password, new Security.Password(u.getHash(), u.getSalt())))
        {
            database.authUser(connection, email, security.generateToken());
            loginManager.setCurrentUser(u);
            return u;
        }
        else {
            return null;
        }
    }

    public void logout() {
        if (loginManager.getCurrentUser() == null)
            return;

        loginManager.setCurrentUser(null);
    }

    public void setUserAvatar(String path) {
        database.setUserAvatar(connection, loginManager.getCurrentUser(), path);
    }

    public String getUserAvatar() {
        if (loginManager.getCurrentUser() == null)
            return null;

        return database.getUserAvatar(connection, loginManager.getCurrentUser());
    }

    public void setUserPassword(String password) {
        if (loginManager.getCurrentUser() == null)
            return;

        database.setUserPassword(connection, loginManager.getCurrentUser(), security.generatePassword(password, null));
    }

    public List<User> getUsers() {
        return database.getUsers(connection);
    }
}
