package ru.javajunior.homework.sem5;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Обертка для {@link Client}
 */
@Getter
public class SocketWrapper implements AutoCloseable {

    private final Long id;
    private final Socket socket;
    private final Scanner input;
    private final PrintWriter output;
    private static long clientCounter = 1L;
    @Setter
    private String name;
    @Setter
    private boolean admin = false;

    public SocketWrapper(Socket socket) {
        try {
            this.id = clientCounter++;
            this.socket = socket;
            this.input = new Scanner(socket.getInputStream());
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        socket.close();
    }

    @Override
    public String toString() {
        return String.format("@%s-[%s]", id, name);
    }

}