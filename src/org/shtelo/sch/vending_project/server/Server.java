package org.shtelo.sch.vending_project.server;

import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private final VendingMachine machine;
    private final ArrayList<ClientThread> clients;
    private boolean running;

    public Server(VendingMachine machine) {
        this.machine = machine;

        this.clients = new ArrayList<>();
        this.running = true;
    }

    @Override
    public void run() {
        ServerSocket server;
        try {
            server = new ServerSocket(9832);

            while (running) {
                Socket socket = server.accept();
                System.out.printf("클라이언트 접속함: %s:%d\n", socket.getInetAddress(), socket.getPort());
                ClientThread thread = new ClientThread(this, socket, machine);
                clients.add(thread);
                thread.start();
            }

            server.close();
        } catch (IOException e) {
            System.out.println("Server interrupted");
        }
    }

    public void stopServer() {
        this.running = false;
    }

    public void removeClient(ClientThread clientThread) {
        clients.remove(clientThread);
    }
}
