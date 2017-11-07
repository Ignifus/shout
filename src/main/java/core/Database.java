package core;

import model.Shout;
import model.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import java.io.Serializable;
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
            return query.setParameter("email",email).getSingleResult();
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

    public void authUser(EntityManager connection, String email, boolean auth) {
        User u = getUser(connection, email);

        connection.getTransaction().begin();
        u.setAuthenticated(auth);
        connection.getTransaction().commit();
    }

    public List<Shout> getShouts(EntityManager connection) {
        return connection.createQuery(
                "SELECT s FROM Shout s", Shout.class).getResultList();
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
}
