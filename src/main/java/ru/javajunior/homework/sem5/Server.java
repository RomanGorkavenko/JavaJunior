package ru.javajunior.homework.sem5;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Сервер
 */
public class Server {

    /**
     * Порт для подключения
     */
    public static final int PORT = 8181;

    /**
     * Хранилище пользователей
     */
    private static final Map<Long, SocketWrapper> clients = new HashMap<>();

    /**
     * Токен для подтверждения прав администратора
     */
    private static final String TOKEN_ADMIN = "admin";

    /**
     * Подключение сервера
     */
    private static ServerSocket serverSocket;


    public static void main(String[] args) {
        Server.runServer();
    }

    /**
     * Метод запуска сервера и оркестрирования пользователями (клиентами)
     */
    public static void runServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен на порту: " + PORT);
            while (!serverSocket.isClosed()) {

                Socket client = serverSocket.accept();

                SocketWrapper wrapper = new SocketWrapper(client);

                long clientId = wrapper.getId();


                System.out.println("Подключился новый клиент[" + clientId + ", " + wrapper.getSocket() + "]");

                clients.put(clientId, wrapper);


                new Thread(() -> {
                    try (Scanner input = wrapper.getInput();
                         PrintWriter output = wrapper.getOutput()) {

                        Scanner inputName = wrapper.getInput();
                        wrapper.setName(inputName.nextLine());

                        String clientName = wrapper.getName();

                        output.println("Подключение произошло успешно. Список всех клиентов: " + clients);

                        broadcastMessage("подключился.", clientId, clientName);

                        while (true) {
                            // выходит из цикла если админ закрыл доступ
                            String clientInput;
                            try {
                                clientInput = input.nextLine();
                            } catch (Exception e) {
                                break;
                            }
                            // парсим строку для удобства распределения сообщений
                            String[] stringsOfMessage = clientInput.split(" ", 2);
                            if (!checkingAdmin(clientInput, wrapper)) { // проверка на ввод токена администратора,
                                                                        // чтобы он не отправлялся всем пользователям
                                if (clientInput.charAt(0) != '@') {
                                    if (Objects.equals("q", clientInput)) {
                                        clientDisconnected(clientId, wrapper);
                                        break;
                                    } else if (Objects.equals("kick", stringsOfMessage[0]) && wrapper.isAdmin()) {
                                        long destinationId = getDestinationId(stringsOfMessage[1]);
                                        clientDisconnected(destinationId, wrapper);
                                    } else {
                                        broadcastMessage(clientInput, clientId, clientName);
                                    }
                                } else {
                                    sendingMessage(stringsOfMessage, output, clientId, clientName);
                                }
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            close();
        }
    }

    /**
     * Метод получает id клиента из сообщения другого клиента.
     * @param stringsOfMessage распарсеное сообщение другого клиента.
     * @return id клиента для которого сообщение.
     */
    private static long getDestinationId(String stringsOfMessage) {
        long destinationId;
        try {
            destinationId = Long.parseLong(stringsOfMessage);
        } catch (NumberFormatException e) {
            destinationId = -1L;
        }
        return destinationId;
    }

    /**
     * Метод отключает клиента.
     * @param id идентификатор клиента которого нужно отключить.
     * @param wrapper клиент, который отключает.
     */
    private static void clientDisconnected(Long id, SocketWrapper wrapper) {
        // При удалении возвращается удаляемый клиент.
        SocketWrapper destination = clients.remove(id);
        if (destination != null) {
            try {
                if (wrapper.isAdmin()) {
                    destination.getOutput().println("Администратор вас отключил, нажмите ENTER");
                } else {
                    destination.getOutput().println("Соединение прервано");
                }

                if (destination.getSocket() != null) {
                    destination.close();
                }

                if (destination.getInput() != null) {
                    destination.getInput().close();
                }

                if (destination.getOutput() != null) {
                    destination.getOutput().close();
                }


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            broadcastMessage("отключился.", destination.getId(), destination.getName());
            System.out.println("Отключился клиент [" + destination.getId() + ", " + destination.getSocket() + "]");

        } else {
            wrapper.getOutput().println("Ошибка ввода");
        }
    }

    /**
     * Метод проверяет токен администратора и активирует права админа на клиенте,
     * который его ввел.
     * @param clientInput сообщение от пользователя.
     * @param wrapper пользователь
     * @return true если идентификация успешна, false если нет.
     */
    private static boolean checkingAdmin(String clientInput, SocketWrapper wrapper) {
        if (Objects.equals(TOKEN_ADMIN, clientInput)) {
            wrapper.setAdmin(true);
            System.out.println(String.format("Админ = %s, %s", wrapper.getId(), wrapper.getSocket()));
            return true;
        }
        return false;
    }

    /**
     * Метод отправки сообщений конкретному пользователю.
     * @param stringsOfMessage распарсеное сообщение другого клиента.
     * @param output способ отправки ответа сервера.
     * @param id идентификатор клиента который отправляет сообщение.
     * @param name имя клиента который отправляет сообщение.
     */
    private static void sendingMessage(String[] stringsOfMessage, PrintWriter output, Long id, String name) {

        long destinationId = getDestinationId(stringsOfMessage[0].substring(1));

        SocketWrapper destination = clients.get(destinationId);
        if (destination != null) {
            destination.getOutput()
                    .println("@" + id + " [" + name + "]: "
                            + stringsOfMessage[1]);
        } else {
            output.println(stringsOfMessage[0] + " такого пользователя нет.");
        }
    }

    /**
     * Метод закрывает соединение сервера
     */
    public static void close() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод рассылки сообщения всем пользователям.
     * @param message отправляемое сообщение.
     * @param id идентификатор клиента который отправляет сообщение.
     * @param name имя клиента который отправляет сообщение.
     */
    private static void broadcastMessage(String message, Long id, String name) {
        clients.values().stream()
                .filter(it -> !Objects.equals(it.getId(), id))
                .forEach(it -> it
                        .getOutput()
                        .println("@" + id + " [" + name + "]: " + message));
    }

}
