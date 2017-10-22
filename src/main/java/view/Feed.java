package view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("feed")
@RequestScoped
public class Feed implements Serializable {

    public String getViewName() {
        return "Feed";
    }

}
