package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("login")
@RequestScoped
public class Login implements Serializable {

    @Inject
    private UserController controller;

    @NotNull(message = "{Requerido}")
    @Size(min = 2, max = 320)
    private String email;

    @NotNull(message = "{Requerido}")
    @Size(min = 8, max = 20)
    private String password;

    private String wrongLogin;

    public String getWrongLogin() {
        return wrongLogin;
    }

    public void setWrongLogin(String wrongPassword) {
        this.wrongLogin = wrongPassword;
    }

    public String login() {
        this.wrongLogin = null;

        try{
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            User u = controller.login(email, password);

            if(u != null) {
                context.redirect(context.getRequestContextPath() + "feed.xhtml?email=" + u.getEmail());
            }
            else {
                this.wrongLogin = "Contraseña o email incorrecto";
                return "/login.xhtml";
            }
        }
        catch (IOException e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
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
