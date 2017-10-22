package view;

import controller.UserController;
import core.LoginManager;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
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

    @Inject
    private LoginManager loginManager;

    @NotNull(message = "{Requerido}")
    @Email
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

    public void login() {
        this.wrongLogin = null;

        try{

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            if(loginManager.getCurrentUser() != null)
                context.redirect(context.getRequestContextPath() + "feed.xhtml?email=" + loginManager.getCurrentUser().getEmail());

            User u = controller.login(email, password);

            if(u != null) {
                loginManager.setCurrentUser(u);
                context.redirect(context.getRequestContextPath() + "feed.xhtml?email=" + loginManager.getCurrentUser().getEmail());
            }
            else {
                this.wrongLogin = "Contraseña o email incorrecto";
                context.redirect(context.getRequestContextPath() + "login.xhtml");
            }

        }
        catch (IOException e) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
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
