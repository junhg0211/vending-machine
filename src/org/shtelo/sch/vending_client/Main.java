package org.shtelo.sch.vending_client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 자판기 관리 프로그램(클라이언트)의 메인 클래스
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // 자판기 IP 입력
            System.out.println("접속할 서버의 주소를 입력하세요. 종료하려면 'exit' 또는 'quit'를 입력하세요.");
            System.out.println("만약 통신에서 사용할 포트 번호를 임의로 설정하려면 '주소:포트번호' 형식으로 입력하세요.");
            System.out.println("기본 포트 번호는 9832입니다.");
            System.out.print(">> ");
            String ip = scanner.nextLine().trim();

            // 종료 조건
            if (ip.equalsIgnoreCase("exit") || ip.equalsIgnoreCase("quit")) {
                break;
            }

            // host, port parsing
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
            // exception handling for server not online
            System.err.println("자판기에 접속할 수 없습니다. 자판기가 켜져있는지 확인해주세요.");
            return;
        }

        // server io ready
        Scanner reader;
        PrintStream writer;

        try {
            reader = new Scanner(socket.getInputStream());
            writer = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("통신중에 예기치 못한 오류가 발생했습니다.");
            return;
        }

        // server communication main loop
        boolean connected = true;
        while (connected) {
            // print prompts
            System.out.println("다음에서 수행할 작업에 해당하는 수를 입력해주세요.\n\n" +
                    "1. 관리자 로그인\n" +
                    "2. 일 매출 확인\n" +
                    "3. 재고 확인\n" +
                    "4. 자판기 로그 확인\n" +
                    "5. 음료 이름 변경\n" +
                    "8. 관리자 로그인 확인\n" +
                    "9. 연결 종료\n");
            System.out.print(">> ");

            String message = scanner.nextLine();

            // branch for action selection
            if (message.equalsIgnoreCase("9")) {
                System.out.println("서버와의 연결을 종료합니다.");
                writer.println("QUIT");
                connected = false;
            } else if (message.equalsIgnoreCase("8")) {
                System.out.println("관리자 로그인 여부를 확인합니다.");
                writer.println("IATH");
            } else if (message.equalsIgnoreCase("1")) {
                System.out.print("관리자 암호를 입력해주세요: ");
                String password = scanner.nextLine();
                writer.printf("AUTH %s\n", password);
            } else if (message.equalsIgnoreCase("2")) {
                System.out.print("자판기 매출 정보를 확인할 날짜를 yyyymmdd 형식으로 입력해주세요: ");
                String date = scanner.nextLine().trim();
                writer.printf("GSEL %s\n", date);
            } else if (message.equalsIgnoreCase("3")) {
                System.out.println("자판기의 재고를 확인합니다.");
                writer.println("GINV");
            } else if (message.equalsIgnoreCase("4")) {
                System.out.print("확인할 자판기 로그의 개수를 입력해주세요: ");
                String count = scanner.nextLine();
                writer.printf("GLOG %s\n", count);
            } else if (message.equalsIgnoreCase("5")) {
                handleSetProduct(writer, reader, scanner);
            } else {
                continue;
            }
            justReceivePrint(reader);
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

    /**
     * 자판기에게 SPRD 명령을 보내는 명령어 실행을 처리합니다.
     * @param writer 자판기와의 통신을 위한 출력 스트림
     * @param reader 자판기와의 통신을 위한 입력 스트림
     * @param scanner 사용자 입력을 받는 스캐너
     */
    private static void handleSetProduct(PrintStream writer, Scanner reader, Scanner scanner) {
        // 음료 목록 표시
        System.out.println("현재 자판기 음료 목록은 다음과 같습니다.");
        writer.println("GINV");
        justReceivePrint(reader);

        // 음료 인덱스와 재고 입력
        int index, count;
        try {
            System.out.print("정보를 변경할 음료의 번호를 입력해주세요 (번호는 1번부터 시작): ");
            index = Integer.parseInt(scanner.nextLine()) - 1;

            System.out.print("음료의 재고를 설정해주세요 (정수): ");
            count = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("올바른 형태의 수를 입력하세요.");
            return;
        }

        // 음료 이름 입력
        System.out.print("정보를 변경할 음료의 이름을 입력해주세요: ");
        String name = scanner.nextLine().trim();

        // 서버에 정보 전송
        writer.printf("SPRD %d %d %s\n", index, count, name);
    }

    /**
     * 서버에서 받은 응답을 출력합니다.
     * @param reader 서버와의 통신을 위한 입력 스트림
     */
    private static void justReceivePrint(Scanner reader) {
        // 서버에서 응답 받기
        String response = reader.nextLine();

        // 만약 서버에서 주는 응답이 여러개라면 여러번 응답 받아서 출력하기
        if (response.startsWith("3")) {
            String[] tokens = response.split(" ");
            int count = Integer.parseInt(tokens[1]);
            for (int i = 0; i < count; i++) {
                System.out.print("SERVER >> ");
                System.out.println(reader.nextLine());
            }
        // 아님 말구
        } else {
            System.out.printf("SERVER >> %s\n", response);
        }
    }
}
