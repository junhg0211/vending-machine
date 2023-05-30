package org.shtelo.sch.vending_project.server;

import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread extends Thread {
    private final Socket socket;
    private final VendingMachine machine;
    private final Scanner reader;
    private final PrintStream writer;
    private final Server server;
    private boolean connected;

    public ClientThread(Server server, Socket socket, VendingMachine machine) throws IOException {
        this.server = server;
        this.socket = socket;
        this.machine = machine;

        this.connected = true;
        this.reader = new Scanner(socket.getInputStream());
        this.writer = new PrintStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        String message;
        while (connected) {
            message = getMessage();
            if (message == null) {
                connected = false;
                continue;
            }

            String command = message.substring(0, 4);

            if (command.equalsIgnoreCase("QUIT")) {
                connected = false;
                sendMessage("4 통신 종료");
            } else {
                sendMessage("5 잘못된 명령어");
            }
        }

        System.out.printf("클라이언트 접속 종료: %s:%d\n", socket.getInetAddress(), socket.getPort());

        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("ClientThread interrupted");
        }

        server.removeClient(this);
    }

    public void sendMessage(String message) {
        writer.println(message);
        System.out.printf("%s:%d << %s\n", socket.getInetAddress(), socket.getPort(), message);
    }

    public String getMessage() {
        String message;
        try {
            message = reader.nextLine();
        } catch (Exception e) {
            connected = false;
            return null;
        }

        System.out.printf("%s:%d >> %s\n", socket.getInetAddress().getHostAddress(), socket.getPort(), message);

        return message;
    }
}
