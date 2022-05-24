package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> index() {
        List<User> users = em.createQuery("SELECT a FROM User a", User.class)
                .getResultList();
        return users;
    }

    @Override
    public User show(long id) {
        User user = em.find(User.class, id);
        em.detach(user);

        return user;
    }

    @Override
    public void save(User user) {

        em.persist(user);
    }

    @Override
    public void update(long id, User updateUser) {
        em.merge(updateUser);
    }

    @Override
    public void delete(long id) {
        Query query = em.createQuery("delete from User where id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public User findByUsername(String username) {
        Query query = em.createQuery("Select user FROM User user JOIN FETCH user.roles WHERE user.email  = :username");
        query.setParameter("username", username);
        return (User)query.getSingleResult();
    }
}
