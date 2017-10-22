package core;

import model.User;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@SessionScoped
public class LoginManager implements Serializable {
    private User currentUser;

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
