package ru.javajunior.homework.sem4.hibernate.dao.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.javajunior.homework.sem4.hibernate.HibernateUtil;
import ru.javajunior.homework.sem4.hibernate.dao.interfaces.obj.BookDAO;
import ru.javajunior.homework.sem4.hibernate.entity.Book;


import java.util.List;

public class BookDAOImpl implements BookDAO {

    @Override
    public Book get(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Book book = session.get(Book.class, id);
        session.close();
        return book;
    }

    @Override
    public void add(Book obj) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(obj);
        }
    }

    @Override
    public List<Book> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Book> query = session.createQuery("FROM Book", Book.class);
        List<Book> books = query.getResultList();
        session.close();
        return books;
    }
}
