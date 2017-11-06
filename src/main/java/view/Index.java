package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Named("index")
@RequestScoped
public class Index implements Serializable {

    @Inject
    private UserController controller;

    @NotNull(message = "{Requerido}")
    @Size(min = 2, max = 320)
    private String email;

    @NotNull(message = "{Requerido}")
    @Size(min = 8, max = 20)
    private String password;

    public List<User> getUsers() {
        return controller.getUsers();
    }

    public String register() {
        controller.createUser(email, password);
        User u = controller.login(email, password);

        return "/feed.xhtml?faces-redirect=true&email=" + u.getEmail();
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
