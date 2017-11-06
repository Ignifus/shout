package core;

import model.Shout;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class Database implements Serializable{
    private static final EntityManagerFactory emFactory;
    private EntityManager connection;

    static {
        emFactory = Persistence.createEntityManagerFactory("shout");
    }

    public Database() {
        connection = emFactory.createEntityManager();
    }

    @Override
    public void finalize() {
        connection.close();
    }

    public void addUser(User u) {
        connection.getTransaction().begin();
        connection.persist(u);
        connection.getTransaction().commit();
    }

    public List<User> getUsers() {
        return connection.createQuery(
                "SELECT u FROM User u", User.class).getResultList();
    }

    public User getUser(String email) {
        TypedQuery<User> query = connection.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);

        return query.setParameter("email",email).getSingleResult();
    }

    public void removeUser(String email) {
        connection.getTransaction().begin();
        connection.remove(getUser(email));
        connection.getTransaction().commit();
    }

    public void authUser(String email, boolean auth) {
        User u = getUser(email);

        connection.getTransaction().begin();
        u.setAuthenticated(auth);
        connection.getTransaction().commit();
    }

    public List<Shout> getShouts() {
        return connection.createQuery(
                "SELECT s FROM Shout s", Shout.class).getResultList();
    }

    public void addShout(Shout s) {
        connection.getTransaction().begin();
        connection.persist(s);
        connection.getTransaction().commit();
    }

    public void removeShout(int id) {
        connection.getTransaction().begin();
        connection.remove(connection.find(Shout.class, id));
        connection.getTransaction().commit();
    }
}
