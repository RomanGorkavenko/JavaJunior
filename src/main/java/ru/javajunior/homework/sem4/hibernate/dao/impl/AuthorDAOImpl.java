package ru.javajunior.homework.sem4.hibernate.dao.impl;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.javajunior.homework.sem4.hibernate.HibernateUtil;
import ru.javajunior.homework.sem4.hibernate.dao.interfaces.obj.AuthorDAO;
import ru.javajunior.homework.sem4.hibernate.entity.Author;
import ru.javajunior.homework.sem4.hibernate.entity.Book;

import java.util.List;

public class AuthorDAOImpl implements AuthorDAO {

    @Override
    public Author get(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Author author = session.get(Author.class, id);
        session.close();
        return author;
    }

    @Override
    public void add(Author obj) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(obj);
        }
    }

    @Override
    public List<Author> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Author> query = session.createQuery("FROM Author", Author.class);
        List<Author> authors = query.getResultList();
        session.close();
        return authors;
    }

    @Override
    public List<Book> findAllBooks(Long authorId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Author> query = session.createQuery("FROM Author a WHERE a.id = :id", Author.class);
        query.setParameter("id", authorId);
        List<Book> books = query.getSingleResult().getBooks();
        session.close();
        return books;
    }
}
