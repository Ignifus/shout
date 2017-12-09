package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("index")
@RequestScoped
public class Index implements Serializable {

    @Inject
    private UserController controller;

    @NotNull(message = "{Requerido}")
    @Size(min = 6, max = 320)
    @Email
    private String email;

    @NotNull(message = "{Requerido}")
    @Size(min = 8, max = 20)
    private String password;

    public List<User> getUsers() {
        return controller.getUsers();
    }

    public String register() {
        if (controller.getCurrentUser() != null)
            return "feed.xhtml?faces-redirect=true";

        try {
            controller.createUser(email, password);
            controller.login(email, password);
        }
        catch (IllegalArgumentException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ya existe.", null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "index.xhtml";
        }

        return "feed.xhtml?faces-redirect=true";
    }

    public void onload() {
        User u = controller.getCurrentUser();
        try {
            if (u != null)
                FacesContext.getCurrentInstance().getExternalContext().redirect("/feed.xhtml");
        }
        catch(Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "ERROR", e);
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
