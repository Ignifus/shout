package core;

import model.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Database implements Serializable{
    private EntityManagerFactory emFactory;

    @PostConstruct
    public void init() {
        emFactory = Persistence.createEntityManagerFactory("shout");
    }

    @PreDestroy
    public void finalize() {
        emFactory.close();
    }

    public EntityManager getConnection() {
        return emFactory.createEntityManager();
    }

    public void addUser(EntityManager connection, User u) {
        connection.getTransaction().begin();

        if (getUser(connection, u.getEmail()) != null) {
            connection.getTransaction().commit();
            throw new IllegalArgumentException("User already exists.");
        }

        connection.persist(u);
        connection.getTransaction().commit();
    }

    public List<User> getUsers(EntityManager connection) {
        return connection.createQuery(
                "SELECT u FROM User u", User.class).getResultList();
    }

    public User getUser(EntityManager connection, String email) {
        TypedQuery<User> query = connection.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);

        try {
            return query.setParameter("email", email).getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public void removeUser(EntityManager connection, String email) {
        connection.getTransaction().begin();
        connection.remove(getUser(connection, email));
        connection.getTransaction().commit();
    }

    public void authUser(EntityManager connection, String email, String key) {
        User u = getUser(connection, email);

        connection.getTransaction().begin();
        u.setWskey(key);
        connection.getTransaction().commit();
    }

    public void setUserAvatar(EntityManager connection, User u, String path) {
        connection.getTransaction().begin();
        u.setAvatar(path);
        connection.getTransaction().commit();
    }

    public String getUserAvatar(EntityManager connection, User u) {
        return connection.find(User.class, u.getId()).getAvatar();
    }

    public List<Shout> getShouts(EntityManager connection) {
        return connection.createQuery(
                "SELECT s FROM Shout s", Shout.class).getResultList();
    }

    public List<Shout> getShouts(EntityManager connection, int userId) {
        TypedQuery<Shout> query = connection.createQuery(
                "SELECT s FROM Shout s WHERE s.user.id = :userId", Shout.class);

        try {
            return query.setParameter("userId", userId).getResultList();
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void addShout(EntityManager connection, Shout s) {
        connection.getTransaction().begin();
        connection.persist(s);
        connection.getTransaction().commit();
    }

    public void removeShout(EntityManager connection, int id) {
        connection.getTransaction().begin();
        connection.remove(connection.find(Shout.class, id));
        connection.getTransaction().commit();
    }

    public Shout getShout(EntityManager connection, int id) {
        return connection.find(Shout.class, id);
    }

    public void addComment(EntityManager connection, Comment c) {
        connection.getTransaction().begin();
        connection.persist(c);
        connection.getTransaction().commit();
    }

    public List<Comment> getComments(EntityManager connection, int shoutId) {
        TypedQuery<Comment> query = connection.createQuery(
                "SELECT u FROM Comment u WHERE u.shout.id = :shoutId", Comment.class);

        try {
            return query.setParameter("shoutId", shoutId).getResultList();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public void addUpvote(EntityManager connection, Upvote upvote) {
        connection.getTransaction().begin();
        connection.persist(upvote);
        connection.getTransaction().commit();
    }

    public int getUpvotes(EntityManager connection, int shoutId) {
        TypedQuery<Upvote> query = connection.createQuery(
                "SELECT u FROM Upvote u WHERE u.shout.id = :shoutId", Upvote.class);

        try {
            return query.setParameter("shoutId", shoutId).getResultList().size();
        }
        catch (NoResultException e) {
            return 0;
        }
    }

    public boolean canUserUpvote(EntityManager connection, int userId, int shoutId) {
        TypedQuery<Upvote> query = connection.createQuery(
                "SELECT u FROM Upvote u WHERE u.user.id = :userId AND u.shout.id = :shoutId", Upvote.class);

        try {
            return query.setParameter("userId", userId).setParameter("shoutId", shoutId).getResultList().size() == 0;
        }
        catch (NoResultException e) {
            return true;
        }
    }

    public void setUserPassword(EntityManager connection, User u, Security.Password password) {
        connection.getTransaction().begin();
        u.setHash(password.hash);
        u.setSalt(password.salt);
        connection.getTransaction().commit();
    }

    public int followCount(EntityManager connection, int followerId, String followedEmail) {
        int followedId = getUser(connection, followedEmail).getId();

        TypedQuery<Follow> query = connection.createQuery(
                "SELECT f FROM Follow f WHERE f.followerUser.id = :followerId AND f.followedUser.id = :followedId", Follow.class);

        try {
            return query.setParameter("followerId", followerId).setParameter("followedId", followedId).getResultList().size();
        }
        catch (NoResultException e) {
            return 0;
        }
    }

    public void follow(EntityManager connection, Follow follow) {
        connection.getTransaction().begin();
        connection.persist(follow);
        connection.getTransaction().commit();
    }

    public void unfollow(EntityManager connection, Follow follow) {
        connection.getTransaction().begin();
        if (!connection.contains(follow)) {
            follow = connection.merge(follow);
        }
        connection.remove(follow);
        connection.getTransaction().commit();
    }

    public List<Follow> getFollows(EntityManager connection, int userId) {
        TypedQuery<Follow> query = connection.createQuery(
                "SELECT f FROM Follow f WHERE f.followerUser.id = :userId", Follow.class);

        try {
            return query.setParameter("userId", userId).getResultList();
        }
        catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
}
