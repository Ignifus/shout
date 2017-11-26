package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("feed")
@RequestScoped
public class Feed implements Serializable {
    @Inject
    private UserController userController;

    public String getViewName() {
        return "Feed";
    }

    public String logout() {
        userController.logout();
        return "login.xhtml";
    }

    public String feed() {
        User u = userController.getCurrentUser();

        if (u != null)
            return "feed.xhtml?faces-redirect=true&email=" + u.getEmail();
        else
            return "login.xhtml";
    }
}
