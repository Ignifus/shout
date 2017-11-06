package view;

import controller.UserController;
import core.LoginManager;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("index")
@RequestScoped
public class Index implements Serializable {

    @Inject
    private UserController controller;

    @Inject
    private LoginManager loginManager;

    @NotNull(message = "{Requerido}")
    @Size(min = 2, max = 320)
    private String email;

    @NotNull(message = "{Requerido}")
    @Size(min = 8, max = 20)
    private String password;

    public List<User> getUsers() {
        return controller.getUsers();
    }

    public void register() {
        controller.createUser(email, password);
        User u = controller.login(email, password);
        loginManager.setCurrentUser(u);

        try{
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            context.redirect(context.getRequestContextPath() + "feed.xhtml?email=" + loginManager.getCurrentUser().getEmail());
        }
        catch (IOException e) {
            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getViewName() {
        return "Bienvenido";
    }

}
