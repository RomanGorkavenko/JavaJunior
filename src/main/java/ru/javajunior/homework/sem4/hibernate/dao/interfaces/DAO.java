package ru.javajunior.homework.sem4.hibernate.dao.interfaces;

import java.util.List;

public interface DAO<T> {

    /**
     * Получить один объект по id
     * @param id уникальный идентификатор
     * @return объект
     */
    T get(Long id);

    /**
     * Добавить объект
     * @param obj объект
     */
    void add(T obj);

    /**
     * Получить все объекты
     */
    List<T> findAll();

}
