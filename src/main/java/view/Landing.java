package view;

import controller.UserController;
import model.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("landing")
@SessionScoped
public class Landing implements Serializable {

    @Inject
    private UserController controller;

    public List<User> getUsers() {
        return controller.getUsers();
    }

    public String getViewName() {
        return "Inicio";
    }

}
