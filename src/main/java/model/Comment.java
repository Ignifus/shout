package model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="comments")
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Shout shout;

    private Date date;
    private String content;

    public Comment(User user, Shout shout, Date date, String content) {
        this.user = user;
        this.shout = shout;
        this.date = date;
        this.content = content;
    }

    public Comment() {
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

    public Shout getShout() {return shout;}

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
