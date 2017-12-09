package view;

import controller.UserController;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("profile")
@RequestScoped
public class Profile implements Serializable {

    @Inject
    private UserController controller;

    private Part avatar;

    @Size(min = 8, max = 20)
    private String password;

    public void enter() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        try {
            if (controller.getCurrentUser() == null)
                context.redirect("/login.xhtml");
            context.redirect("/profile.xhtml");
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "ERROR", e);
        }
    }

    public void upload() {
        try (InputStream input = avatar.getInputStream()) {
            if (controller.getCurrentUser() == null)
                FacesContext.getCurrentInstance().getExternalContext().redirect("/login.xhtml");

            Path path = new File("./", controller.getCurrentUser().getId() + "_avatar").toPath();
            Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
            controller.setUserAvatar(path.toAbsolutePath().toString());

            RequestContext.getCurrentInstance().update("profile-picture");
        }
        catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "ERROR", e);
        }
    }

    public StreamedContent getImage() throws IOException {
        if (controller.getUserAvatar() == null)
            return new DefaultStreamedContent();

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }
        else {
            return new DefaultStreamedContent(new ByteArrayInputStream(Files.readAllBytes(new File(controller.getUserAvatar()).toPath())));
        }
    }

    public void changePassword() {
        if (controller.getCurrentUser() == null)
            return;

        controller.setUserPassword(this.password);
    }

    public Part getAvatar() {
        return avatar;
    }

    public void setAvatar(Part avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getViewName() {
        return "Perfil";
    }

}
