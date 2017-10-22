package core;

import model.Shout;
import model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import java.util.ArrayList;
import java.util.List;


@Singleton
public class Db {
    private List<User> users = new ArrayList<>();
    private int usersCounter;

    private List<Shout> shouts = new ArrayList<>();
    private int shoutsCounter;

    private Security security = new Security();

    @PostConstruct
    public void init() {
        Security.Password sp = security.generatePassword("shout2017", null);

        addUser(new User("shout@shout.com", sp.hash, sp.salt));
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User u) {
        u.setId(++usersCounter);
        users.add(u);
    }

    public User getUser(String email) {
        for(User u : users)
        {
            if(u.getEmail().equalsIgnoreCase(email))
                return u;
        }

        return null;
    }

    public boolean isUserAuthenticated(String email) {
        return getUser(email).isAuthenticated();
    }

    public void authUser(String email) {
        getUser(email).setAuthenticated(true);
    }

    public void deauthUser(String email) {
        getUser(email).setAuthenticated(false);
    }

    public void removeUser(User u) {
        users.remove(u);
    }

    public List<Shout> getShouts() {
        return shouts;
    }

    public void addShout(Shout p) {
        p.setId(++shoutsCounter);
        shouts.add(p);
    }

    public void removeShout(int id) {
        shouts.remove(id);
    }
}
