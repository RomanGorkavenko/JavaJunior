package ru.javajunior.homework.sem2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

// Импортируем статические методы, чтобы уменьшить код визуально.
import static java.util.stream.Collectors.*;

/**
 * Тест процессор обрабатывает аннотации в переданном ему классе.
 */
public class TestProcessor {

    /**
     * Константа указывающая количество повторений строки.
     */
    private static final int REPEAT = 20;

    /**
     * Метод с помощью Reflection Api запускает тесты c аннотацией {@link Test}.
     * Проверяет есть ли аннотации {@link BeforeEach}, {@link AfterEach},
     * если есть запускает методы помеченные {@link BeforeEach} до каждого теста,
     * методы помеченные {@link AfterEach} после каждого теста.
     * Также делается проверка, что все методы помеченные этими аннотациями
     * являются void без аргументов.
     * {@link TestProcessor} не запускает методы помеченные аннотацией {@link Skip}.
     * Запускает методы в порядке (по возрастанию) используя параметр {@link Test#order()}.
     * @param testClass класс с аннотациями.
     */
    public static void runTest(Class<?> testClass) {
        final Object testObj = getObject(testClass);

        ArrayList<Method> beforeEach = Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.isAnnotationPresent(BeforeEach.class))
                .filter(TestProcessor::checkTestMethod)
                .collect(toCollection(ArrayList::new));

        ArrayList<Method> afterEach = Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.isAnnotationPresent(AfterEach.class))
                .filter(TestProcessor::checkTestMethod)
                .collect(toCollection(ArrayList::new));


        Arrays.stream(testClass.getDeclaredMethods())
                .filter(it -> it.isAnnotationPresent(Test.class))
                .filter(TestProcessor::checkTestMethod)
                .sorted(Comparator.comparingInt(a -> a.getAnnotation(Test.class).order()))
                .forEach(it -> {
                    if (!it.isAnnotationPresent(Skip.class)) {
                        beforeEach.forEach(before -> runTest(before, testObj));
                        runTest(it, testObj);
                        afterEach.forEach(after -> runTest(after, testObj));
                        System.out.println("=".repeat(REPEAT));
                    }
                });
    }

    /**
     * Создает объект переданного класса.
     * Обрабатывает исключения {@link NoSuchMethodException}, {@link InstantiationException}, {@link IllegalAccessException},
     * {@link InvocationTargetException}.
     * Бросает исключения {@link IllegalStateException}.
     * @param testClass класс объект которого необходимо создать.
     * @return возвращает объект переданного класса.
     */
    private static Object getObject(Class<?> testClass) {
        final Constructor<?> declaredConstructor;
        try {
            declaredConstructor = testClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Для класса \"" + testClass.getName() + "\" не найден конструктор без аргументов");
        }

        final Object testObj;
        try {
            testObj = declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Не удалось создать объект класса \"" + testClass.getName() + "\"");
        }
        return testObj;
    }

    /**
     * Метод проверяет, что метод с аннотацией соответствует условиям.
     * Он должен быть void без аргументов.
     * Бросает исключения {@link IllegalArgumentException} если метод не соответствует условиям.
     * @param method метод с аннотацией
     * @return {@link Boolean}
     */
    private static boolean checkTestMethod(Method method) {
        if (!method.getReturnType().isAssignableFrom(void.class) || method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Метод \"" + method.getName() + "\" должен быть void и не иметь аргументов");
        }
        return true;
    }

    /**
     * Запускает метод объекта переданного в него.
     * Обрабатывает исключения {@link IllegalAccessException}, {@link InvocationTargetException}.
     * Бросает исключения {@link RuntimeException} если метод запустить не удалось.
     * @param method метод
     * @param testObj объект метод которого нужно запустить
     */
    private static void runTest(Method method, Object testObj) {
        try {
            method.invoke(testObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Не удалось запустить тестовый метод \"" + method.getName() + "\"");
        }
    }
}
