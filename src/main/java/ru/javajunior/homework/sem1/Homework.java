package ru.javajunior.homework.sem1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.stream.Collectors.*;

/**
 * 0.1. Посмотреть разные статьи на Хабр.ру про Stream API
 * 0.2. Посмотреть видеоролики на YouTube.com Тагира Валеева про Stream API
 * <p>
 * 1. Создать список из 1_000 рандомных чисел от 1 до 1_000_000
 * 1.1 Найти максимальное
 * 2.2 Все числа, большие, чем 500_000, умножить на 5, отнять от них 150 и просуммировать
 * 2.3 Найти количество чисел, квадрат которых меньше, чем 100_000
 * <p>
 * 2. Создать класс Employee (Сотрудник) с полями: String name, int age, double salary, String department
 * 2.1 Создать список из 10-20 сотрудников
 * 2.2 Вывести список всех различных отделов (department) по списку сотрудников
 * 2.3 Всем сотрудникам, чья зарплата меньше 10_000, повысить зарплату на 20%
 * 2.4 * Из списка сотрудников с помощью стрима создать Map<String, List<Employee>> с отделами и сотрудниками внутри отдела
 * 2.5 * Из списока сорудников с помощью стрима создать Map<String, Double> с отделами и средней зарплатой внутри отдела
 */
public class Homework {

    //region Поля константы для списка рандомных чисел
    private static final int LIST_INT_RANDOM_NUMBER_FROM = 1;
    private static final int LIST_INT_RANDOM_NUMBER_TO = 1_000_001;
    private static final int LIST_INT_LIMIT = 1_000;
    private static final int LIST_INT_MORE_THAN = 500_000;
    private static final int LIST_INT_MULTIPLY_BY = 5;
    private static final int LIST_INT_SUBTRACT = 150;
    private static final int LIST_INT_SQUARE_IS_LESS_THAN = 100_000;
    //endregion

    //region Поля константы для списка работников
    private static final int LIST_EMPLOYEE_RANDOM_AGE_FROM = 18;
    private static final int LIST_EMPLOYEE_RANDOM_AGE_TO = 69;
    private static final int LIST_EMPLOYEE_RANDOM_SALARY_FROM = 8000;
    private static final int LIST_EMPLOYEE_RANDOM_SALARY_TO = 30001;
    private static final int LIST_EMPLOYEE_LIMIT = 20;
    private static final int PERCENTAGE_OF_SALARY_INCREASE = 20;
    private static final int LESS_THAN = 10_000;
    //endregion

    public static void main(String[] args) {
        System.out.println("======= 1 =======");
        List<Integer> list = createListRandomNumber();
        findMaxNumber(list);
        findAllNumbersGreaterThan500_000AndMultiplyBy5AndSubtract150AndSum(list);
        findCountNumberSquareWhichIsLessThan100_000(list);
        System.out.println();

        System.out.println("======= 2 =======");
        List<Employee> employees = createListRandomEmployee();
        outputListOfAllDifferentDepartments(employees);
        createMapKeyDepartmentValueListEmployees(employees);
        System.out.print("Зарплату подняли: ");
        employees = raiseSalaryBy20PercentIfSalaryIsLessThan10_000(employees);
        System.out.println();
        createMapKeyDepartmentValueAverageSalaryToDepartment(employees);
    }

    /**
     * 1. Создать список из 1_000 рандомных чисел от 1 до 1_000_000.
     */
    private static List<Integer> createListRandomNumber() {
        return Stream.generate(() -> current().nextInt(LIST_INT_RANDOM_NUMBER_FROM, LIST_INT_RANDOM_NUMBER_TO))
                .limit(LIST_INT_LIMIT)
                .collect(toCollection(ArrayList::new));
    }

    /**
     * 1.1 Найти максимальное число.
     */
    private static void findMaxNumber(List<Integer> integerList) {
        integerList
                .stream()
                .reduce(Math::max)
                .ifPresent(it -> System.out.println("Максимально число в списке: " + it));
    }

    /**
     * 1.2 Все числа, большие, чем 500_000, умножить на 5, отнять от них 150 и просуммировать
     */
    private static void findAllNumbersGreaterThan500_000AndMultiplyBy5AndSubtract150AndSum(List<Integer> integerList) {
        integerList
                .stream()
                .filter(it -> it > LIST_INT_MORE_THAN)
                .map(it -> it * LIST_INT_MULTIPLY_BY)
                .map(it -> it - LIST_INT_SUBTRACT)
                .reduce(Integer::sum)
                .ifPresent(it -> System.out.println("Сумма чисел из списка, которые > 500_000, " +
                        "умноженные на 5 и вычтено из каждого 150: " + it));
    }

    /**
     * 1.3 Найти количество чисел, квадрат которых меньше, чем 100_000.
     */
    private static void findCountNumberSquareWhichIsLessThan100_000(List<Integer> integerList) {
        long count = integerList
                .stream()
                .filter(it -> it * it < LIST_INT_SQUARE_IS_LESS_THAN)
                .count();
        System.out.println("Количество чисел в списке, квадрат которых < 100_000: " + count);
    }

    /**
     * 2.1 Создать список из 10-20 сотрудников
     */
    private static List<Employee> createListRandomEmployee() {
        String[] name = new String[]{"Маша", "Петр", "Роман", "Иван", "Оля", "Лера", "Илья"};
        String[] department = new String[]{"ИТ", "Бухгалтерия", "Кадры", "Реклама"};
        return Stream.generate(() -> new Employee(name[current().nextInt(name.length)],
                        current().nextInt(LIST_EMPLOYEE_RANDOM_AGE_FROM, LIST_EMPLOYEE_RANDOM_AGE_TO),
                        current().nextInt(LIST_EMPLOYEE_RANDOM_SALARY_FROM, LIST_EMPLOYEE_RANDOM_SALARY_TO),
                        department[current().nextInt(department.length)]))
                .limit(LIST_EMPLOYEE_LIMIT)
                .collect(toCollection(ArrayList::new));
    }

    /**
     * 2.2 Вывести список всех различных отделов (department) по списку сотрудников.
     */
    private static void outputListOfAllDifferentDepartments(List<Employee> employees) {
        System.out.print("Департаменты в организации: ");
        employees.stream()
                .map(employee -> String.format("[%s] ", employee.getDepartment()))
                .collect(toSet()).forEach(System.out::print);
        System.out.println();
    }

    /**
     * 2.3 Всем сотрудникам, чья зарплата меньше 10_000, повысить зарплату на 20%
     */
    private static List<Employee> raiseSalaryBy20PercentIfSalaryIsLessThan10_000(List<Employee> employees) {
        return employees.stream()
                .peek(it -> {
                    if (it.getSalary() < LESS_THAN) {
                        it.setSalary(it.getSalary() * (1 + ((double) PERCENTAGE_OF_SALARY_INCREASE / 100)));
                        System.out.printf("[%s(%s)(%.2f)] ", it.getName(), it.getDepartment(), it.getSalary());
                    }})
                .toList();
    }

    /**
     * 2.4 * Из списка сотрудников с помощью стрима создать Map<String, List<Employee>>
     *     с отделами и сотрудниками внутри отдела
     */
    private static void createMapKeyDepartmentValueListEmployees(List<Employee> employees) {
        Map<String, List<Employee>> employeesDepartment = employees.stream()
                .collect(groupingBy(Employee::getDepartment, toList()));

        //region Печать вынес отдельно для лучшей читаемости
        employeesDepartment.forEach((department, employee) -> System.out.println(
                "[" + department + "] " +
                        employee.stream()
                                .map(s -> String.format("%s(%s)(%.2f)", s.getName(), s.getDepartment(), s.getSalary()))
                                .collect(joining(", ", "{", "}")
                                )));
        //endregion
    }

    /**
     * 2.5 * Из списка сотрудников с помощью стрима создать Map<String, Double>
     *     с отделами и средней зарплатой внутри отдела
     */
    private static void createMapKeyDepartmentValueAverageSalaryToDepartment(List<Employee> employees) {
        Map<String, Double> averageSalaryToDepartment = employees.stream()
                .collect(groupingBy(Employee::getDepartment, averagingDouble(Employee::getSalary)));

        //region Печать вынес отдельно для лучшей читаемости
        averageSalaryToDepartment
                .forEach((d, s) -> System.out.printf("[%s] средняя зарплата по отделу = %.2f%n", d, s));
        //endregion
    }
}
