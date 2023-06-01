package org.shtelo.sch.vending_client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("접속할 서버의 주소를 입력하세요. 종료하려면 'exit' 또는 'quit'를 입력하세요.");
            System.out.println("만약 통신에서 사용할 포트 번호를 임의로 설정하려면 '주소:포트번호' 형식으로 입력하세요.");
            System.out.println("기본 포트 번호는 9832입니다.");
            System.out.print(">> ");
            String ip = scanner.nextLine().trim();

            if (ip.equalsIgnoreCase("exit") || ip.equalsIgnoreCase("quit")) {
                break;
            }

            // parse host and port
            String host;
            int port = 9832;

            if (ip.contains(":")) {
                String[] tokens = ip.split(":");
                host = tokens[0];
                port = Integer.parseInt(tokens[1]);
            } else {
                host = ip;
            }

            handleConnection(host, port, scanner);
        }

        scanner.close();
    }

    /**
     * 서버와의 통신을 처리합니다.
     * @param host 서버의 주소
     * @param scanner 사용자 입력을 받는 스캐너
     */
    private static void handleConnection(String host, int port, Scanner scanner) {
        Socket socket;
        try {
            socket = new Socket(host, port);
            System.out.println("서버에 접속했습니다.");
        } catch (IOException e) {
            System.err.println("자판기에 접속할 수 없습니다. 자판기가 켜져있는지 확인해주세요.");
            return;
        }

        Scanner reader;
        PrintStream writer;

        try {
            reader = new Scanner(socket.getInputStream());
            writer = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("통신중에 예기치 못한 오류가 발생했습니다.");
            return;
        }

        boolean connected = true;
        while (connected) {
            System.out.print("서버 << ");
            String message = scanner.nextLine();
            writer.println(message);

            System.out.print("서버 >> ");
            String response = reader.nextLine();
            System.out.println(response);

            String[] tokens = response.split(" ");

            if (tokens[0].equalsIgnoreCase("4")) {
                connected = false;
            } else if (tokens[0].equalsIgnoreCase("2")) {
                int count = Integer.parseInt(tokens[1]);
                for (int i = 0; i < count; i++) {
                    System.out.print("SERVER >> ");
                    System.out.println(reader.nextLine());
                }
            }
        }

        System.out.println("서버와 접속을 종료합니다.");

        reader.close();
        writer.close();

        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("통신중에 예기치 못한 오류가 발생했습니다.");
        }
    }
}
