package ru.javajunior.homework.sem4.hibernate;

import ru.javajunior.homework.sem4.hibernate.dao.impl.AuthorDAOImpl;
import ru.javajunior.homework.sem4.hibernate.dao.impl.BookDAOImpl;
import ru.javajunior.homework.sem4.hibernate.entity.Author;
import ru.javajunior.homework.sem4.hibernate.entity.Book;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MainJPA {

    public static void main(String[] args) {

        AuthorDAOImpl authorDAO = new AuthorDAOImpl();
        BookDAOImpl bookDAO = new BookDAOImpl();

        // Создаем таблицу АВТОРЫ и добавляем авторов.
        for (int i = 0; i < 3; i++) {
            Author author = new Author();
            author.setName("Author#" + (i + 1));
            authorDAO.add(author);
        }

        // Создаем таблицу КНИГИ и добавляем книги.
        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setName("Book#" + (i + 1));
            book.setAuthor(authorDAO.get(ThreadLocalRandom.current().nextLong(1, 4)));
            bookDAO.add(book);
        }

        // Получаем всех авторов
        List<Author> authors = authorDAO.findAll();
        System.out.println("authors = " + authors);

        // Получаем все книги
        List<Book> books = bookDAO.findAll();
        System.out.println("books = " + books);

        // Получаем все книги автора по его id
        List<Book> booksAuthor = authorDAO.findAllBooks(1L);
        System.out.println("booksAuthor = " + booksAuthor);

        HibernateUtil.close();
    }
}
