package org.shtelo.sch.vending_project.server;

import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 자판기에서 클라이언트와의 정보 교환을 처리하는 스레드
 */
public class Server extends Thread {
    private final VendingMachine machine;
    private boolean running;
    private final ArrayList<String> authorizeds;

    public Server(VendingMachine machine) {
        this.machine = machine;

        this.running = true;
        this.authorizeds = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket server;
        try {
            // 서버 열기
            server = new ServerSocket(9832);
            System.out.println("Server started");

            while (running) {
                // 클라이언트 접속 대기
                Socket socket = server.accept();
                System.out.printf("클라이언트 접속함: %s:%d\n", socket.getInetAddress(), socket.getPort());

                // 클라이언트 스레드 생성 및 실행
                ClientThread thread = new ClientThread(this, socket, machine);
                thread.start();
            }

            server.close();
        } catch (IOException e) {
            System.out.println("Server interrupted");
        }
    }

    /**
     * 서버를 종료합니다.
     */
    public void stopServer() {
        this.running = false;
        System.out.println("Server stopped");
    }

    public ArrayList<String> getAuthorizeds() {
        return authorizeds;
    }
}
