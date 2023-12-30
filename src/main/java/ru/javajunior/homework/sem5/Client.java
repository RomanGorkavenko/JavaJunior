package ru.javajunior.homework.sem5;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Клиент.
 */
public class Client {

    /**
     * Адрес хоста к которому подключаемся.
     */
    private static final String HOST = "localhost";

    /**
     * Порт для подключения к серверу.
     */
    public static final int PORT = Server.PORT;

    /**
     * Подключение клиента.
     */
    private static Socket client;

    public static void main(String[] args) {
        Client.run();
    }

    /**
     * Метод запуска клиента.
     */
    public static void run() {
        try {
            client = new Socket(HOST, PORT);
            System.out.println("Подключение успешно: " + client);
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ex) {
                System.out.println("Подключение не удалось");
                return;
            }
        }

        serverIsListening();

        sendsMessagesToServer();
    }

    /**
     * Метод отправляет сообщение на сервер для оркестрации.
     */
    private static void sendsMessagesToServer() {
        new Thread(() -> {
            try (PrintWriter output = new PrintWriter(client.getOutputStream(), true)) {
                System.out.print("Введите Ваше имя: ");
                Scanner consoleScanner = new Scanner(System.in);
                String name = consoleScanner.nextLine();
                output.println(name);
                while (!client.isClosed()) {
                    String consoleInput = consoleScanner.nextLine();
                    output.println(consoleInput);
                    // при вводе 'q' ставим задержку чтобы успел закрыться сокет и не начался цикл.
                    Thread.sleep(50);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Метод слушает сервер и печатает сообщения от него.
     */
    private static void serverIsListening() {
        new Thread(() -> {
            try (Scanner input = new Scanner(client.getInputStream())) {
                while (!client.isClosed()) {
                    System.out.println(input.nextLine());
                }
            } catch (NoSuchElementException e) {
                System.out.println("Сеанс завершен.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
