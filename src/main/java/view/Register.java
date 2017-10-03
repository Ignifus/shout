package view;

import controller.UserController;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("register")
@RequestScoped
public class Register implements Serializable {

    @Inject
    private UserController controller;

    private String username;
    private String email;
    private String password;

    public void register() {
        controller.createUser(username, password, email);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return "Registrarse";
    }
}