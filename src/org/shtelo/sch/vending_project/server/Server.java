package org.shtelo.sch.vending_project.server;

import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private final VendingMachine machine;
    private boolean running;

    public Server(VendingMachine machine) {
        this.machine = machine;

        this.running = true;
    }

    @Override
    public void run() {
        ServerSocket server;
        try {
            server = new ServerSocket(9832);
            System.out.println("Server started");

            while (running) {
                Socket socket = server.accept();
                System.out.printf("클라이언트 접속함: %s:%d\n", socket.getInetAddress(), socket.getPort());
                ClientThread thread = new ClientThread(this, socket, machine);
                thread.start();
            }

            server.close();
        } catch (IOException e) {
            System.out.println("Server interrupted");
        }
    }

    public void stopServer() {
        this.running = false;
        System.out.println("Server stopped");
    }
}
