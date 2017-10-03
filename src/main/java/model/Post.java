package model;

import java.util.Date;

public class Post {
    private int id;
    private User user;
    private Date date;
    private String content;

    public Post(User user, Date date, String content) {
        this.user = user;
        this.date = date;
        this.content = content;
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
}
