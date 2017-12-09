package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("feed")
@RequestScoped
public class Feed implements Serializable {
    @Inject
    private UserController userController;

    private String userKey;
    private String userMail;

    public String getUserMail() {
        if (userController.getCurrentUser() == null)
            return null;

        return userController.getCurrentUser().getEmail();
    }

    public String getUserKey() {
        if (userController.getCurrentUser() == null)
            return null;

        return userController.getCurrentUser().getWskey();
    }

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
            return "feed.xhtml?faces-redirect=true";
        else
            return "login.xhtml";
    }

    public void onload() {
        User u = userController.getCurrentUser();
        try {
            if (u == null)
                FacesContext.getCurrentInstance().getExternalContext().redirect("/login.xhtml");
        }
        catch(Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "ERROR", e);
        }
    }
}
