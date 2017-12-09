package model;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String hash;
    private String salt;
    private String email;
    private String avatar;

    private String wskey;

    public User(String email, String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
        this.email = email;
        this.wskey = "";
    }

    public User() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    public String getEmail() {
        return email;
    }

    public String getWskey() {
        return wskey;
    }

    public void setWskey(String key) { this.wskey = key; }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
