package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Named("index")
@RequestScoped
public class Index implements Serializable {

    @Inject
    private UserController controller;

    @NotNull(message = "Requerido")
    @Email(message = "Email invalido")
    private String email;

    @NotNull(message = "Requerido")
    private String password;

    public List<User> getUsers() {
        return controller.getUsers();
    }

    public void register() {
        controller.createUser(password, email);
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
