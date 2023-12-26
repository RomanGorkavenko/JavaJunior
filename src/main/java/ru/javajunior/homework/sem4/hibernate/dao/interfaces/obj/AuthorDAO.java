package ru.javajunior.homework.sem4.hibernate.dao.interfaces.obj;

import ru.javajunior.homework.sem4.hibernate.dao.interfaces.DAO;
import ru.javajunior.homework.sem4.hibernate.entity.Author;
import ru.javajunior.homework.sem4.hibernate.entity.Book;

import java.util.List;

public interface AuthorDAO extends DAO<Author> {

    /**
     * Получить все книги автора по его id
     * @param authorId уникальный идентификатор
     * @return список объектов
     */
    List<Book> findAllBooks(Long authorId);

}
