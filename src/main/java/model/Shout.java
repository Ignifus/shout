package model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="shouts")
public class Shout {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;
    private Date date;
    private String content;
    private String image;

    public Shout(User user, Date date, String content, String image) {
        this.user = user;
        this.date = date;
        this.content = content;
        this.image = image;
    }

    public Shout() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
