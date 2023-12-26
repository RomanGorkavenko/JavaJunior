package ru.javajunior.homework.sem4;

import java.sql.*;

public class JDBC {

    public static void main(String[] args) throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database");

        prepareTables(connection);
        insertData(connection);
        selectData(connection);

        connection.close();
    }

    private static void prepareTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS BOOK
                    (
                        id BIGINT AUTO_INCREMENT,
                        name VARCHAR(255),
                        author VARCHAR(255),
                        PRIMARY KEY (id)
                    )
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertData(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    TRUNCATE TABLE BOOK;
                    INSERT INTO BOOK(NAME, AUTHOR)
                    VALUES
                    ('Book#1', 'Author#10'),
                    ('Book#2', 'Author#2'),
                    ('Book#3', 'Author#3'),
                    ('Book#4', 'Author#4'),
                    ('Book#5', 'Author#5'),
                    ('Book#6', 'Author#6'),
                    ('Book#7', 'Author#7'),
                    ('Book#8', 'Author#8'),
                    ('Book#9', 'Author#9'),
                    ('Book#10', 'Author#10')
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void selectData(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM BOOK WHERE AUTHOR = 'Author#10';");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                System.out.println("id = " + id + ", name = " + name + ", author = " + author);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}