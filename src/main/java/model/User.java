package model;

public class User {
    private int id;
    private String hash;
    private String salt;
    private String email;

    public User(String hash, String salt, String email) {
        this.hash = hash;
        this.salt = salt;
        this.email = email;
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
}
