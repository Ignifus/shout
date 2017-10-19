package core;

import model.Post;
import model.User;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;


@Singleton
public class Db {
    private List<User> users = new ArrayList<>();
    private int usersCounter;

    private List<Post> posts = new ArrayList<>();
    private int postsCounter;

    private Security security = new Security();

    @PostConstruct
    public void init() {
        Security.Password sp = security.generatePassword("shout");

        addUser(new User(sp.hash, sp.salt, "shout@shout.com"));
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUser(User u) {
        u.setId(++usersCounter);
        users.add(u);
    }

    public void removeUser(User u) {
        users.remove(u);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post p) {
        p.setId(++postsCounter);
        posts.add(p);
    }

    public void removePost(int id) {
        posts.remove(id);
    }
}
