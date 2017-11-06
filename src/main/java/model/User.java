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

    private boolean isAuthenticated;

    public User(String email, String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
        this.email = email;
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

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean auth) { isAuthenticated = auth; }
}
