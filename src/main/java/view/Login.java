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

@Named("login")
@RequestScoped
public class Login implements Serializable {

    @Inject
    private UserController controller;

    @NotNull(message = "{Requerido}")
    @Size(min = 2, max = 320)
    @Email
    private String email;

    @NotNull(message = "{Requerido}")
    @Size(min = 8, max = 20)
    private String password;

    public String login() {
        if (controller.getCurrentUser() != null)
            return "feed.xhtml?faces-redirect=true&email=" + controller.getCurrentUser().getEmail();

        if (email == null && password == null)
            return "login.xhtml";

        User u = controller.login(email, password);

        if (u != null) {
            return "feed.xhtml?faces-redirect=true&email=" + u.getEmail();
        }
        else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contraseña o email incorrecto", null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "login.xhtml";
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
        return "Entrar";
    }
}
