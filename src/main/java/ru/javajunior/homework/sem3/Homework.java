package ru.javajunior.homework.sem3;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Написать класс с двумя методами:
 * 1. принимает объекты, имплементирующие интерфейс serializable, и сохраняющие их в файл.
 * Название файл - class.getName() + "_" + UUID.randomUUID().toString()
 * 2. принимает строку вида class.getName() + "_" + UUID.randomUUID().toString()
 * и загружает объект из файла и удаляет этот файл.
 *
 * Что делать в ситуациях, когда файла нет или в нем лежит некорректные данные - подумать самостоятельно.
 */
public class Homework {

    public static void main(String[] args) {
        System.out.println("Работу класса с методами, можно посмотреть в тестах.");
    }

}
