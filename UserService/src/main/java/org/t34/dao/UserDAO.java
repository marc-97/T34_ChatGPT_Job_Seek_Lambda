package org.t34.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.t34.dto.UserContextDTO;
import org.t34.entity.User;

import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private final SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();
        session.close();
    }

    public Optional<User> findByEmail(String email) {
        Session session = sessionFactory.openSession();
        TypedQuery<User> query = session.createNativeQuery("SELECT * FROM user WHERE email = ?", User.class);
        query.setParameter(1, email);
        List<User> result = query.getResultList();
        session.close();
        if (result.isEmpty()) {
            return Optional.empty();
        } else if (result.size() == 1){
            return Optional.of(result.get(0));
        } else {
            throw new NonUniqueResultException();
        }
    }
}
