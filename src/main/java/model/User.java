package model;

public class User {
    private int id;
    private String userName;
    private String hash;
    private String salt;
    private String email;

    public User(String userName, String hash, String salt, String email) {
        this.userName = userName;
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

    public String getUserName() {
        return userName;
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
