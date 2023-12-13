package ru.javajunior.homework.sem3;

import java.io.Serial;
import java.io.Serializable;

/**
 * Тестовый класс для работы {@link ReadAndWriteObjectFromFile}.
 */
public class Department implements Serializable {

    @Serial
    private static final long serialVersionUID = 86L;

    private final String name;
    private int number;

    public Department(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public Department(String name) {
        this(name, 456);
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
