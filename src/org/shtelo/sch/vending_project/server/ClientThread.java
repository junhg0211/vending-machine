package org.shtelo.sch.vending_project.server;

import org.shtelo.sch.vending_project.util.Log;
import org.shtelo.sch.vending_project.util.LogFetcher;
import org.shtelo.sch.vending_project.util.Util;
import org.shtelo.sch.vending_project.util.sell_log.DailyLog;
import org.shtelo.sch.vending_project.util.sell_log.SellLogger;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;
import org.shtelo.sch.vending_project.vending_machine.data_type.Kind;
import org.shtelo.sch.vending_project.vending_machine.data_type.Product;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * 자판기에서 클라이언트와의 정보 교환을 처리하는 스레드
 */
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
            // 만약 클라이언트와 연결이 종료되었다면 스레드 종료
            if (message == null) {
                connected = false;
                continue;
            }

            // 명령어가 프로토콜로 시작하지 않으면 잘못된 명령어로 간주
            if (message.length() < 4) {
                sendMessage("5 잘못된 명렁어");
                continue;
            }

            // 클라이언트의 주소와 명령어 프로토콜 분석
            String address = String.format("%s:%d", socket.getInetAddress(), socket.getPort());
            String command = message.substring(0, 4);

            // 명령어 프로토콜에 따라 분기
            if (command.equalsIgnoreCase("AUTH")) {
                handleAuth(message, address);
            } else if (command.equalsIgnoreCase("GSEL")) {
                handleGsel(address, message);
            } else if (command.equalsIgnoreCase("GINV")) {
                handleGinv(address);
            } else if (command.equalsIgnoreCase("GLOG")) {
                handleGlog(address, message);
            } else if (command.equalsIgnoreCase("SPRD")) {
                handleSprd(address, message);
            } else if (command.equalsIgnoreCase("IATH")) {
                handleIath(address);
            } else if (command.equalsIgnoreCase("QUIT")) {
                handleQuit();
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

        // 클라이언트가 접속을 종료하면 서버의 인증 목록에서 제거
        server.getAuthorizeds().remove(String.format("%s:%d", socket.getInetAddress(), socket.getPort()));
    }

    /**
     * QUIT 명령어를 처리합니다.
     */
    private void handleQuit() {
        connected = false;
        sendMessage("4 통신 종료");
    }

    /**
     * GSEL 명령어를 처리합니다.
     * @param address 클라이언트 주소
     * @param message 명령어가 포함된 메시지
     */
    private void handleGsel(String address, String message) {
        // 인증 확인
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        // 명령어 유효성 검사
        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        // 자판기에서 판매 정보를 가져옴
        SellLogger logger = machine.getSellLogger();
        String date = message.substring(5);

        // 만약 로그 정보가 없다면 에러 메시지 전송
        if (!logger.isLogOn(date)) {
            sendMessage("1 로그 없음");
            return;
        }

        // 로그 정보 전송
        DailyLog log = logger.getLog(date);
        HashMap<String, Integer> sells = log.getSells();
        sendMessage("2 " + sells.size());
        sells.forEach((kind, amount) -> sendMessage(String.format("%s %d", kind, amount)));
    }

    /**
     * GINV 명령어를 처리합니다.
     * @param address 클라이언트 주소
     */
    private void handleGinv(String address) {
        // 인증 확인
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        // 자판기에서 재고 정보를 가져와 전송
        sendMessage("3 5");
        List<Product> juices = machine.getInventory().getJuices();
        for (Product juice : juices) {
            Kind kind = juice.getKind();
            sendMessage(String.format("%s %d %d", kind.getName(), kind.getPrice(), juice.getAmount()));
        }
    }

    /**
     * GLOG 명령어를 처리합니다.
     * @param address 클라이언트 주소
     * @param message 명령어가 포함된 메시지
     */
    private void handleGlog(String address, String message) {
        // 인증 확인
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        // 명령어 유효성 검사
        if (message.length() < 6) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        // 명령어에서 원하는 로그 개수를 가져옴
        int preferredCount;
        try {
            preferredCount = Integer.parseInt(message.substring(5));
        } catch (NumberFormatException e) {
            sendMessage("5 잘못된 수 형식");
            return;
        }

        // 요청받은 로그 개수에 따라 리스트에 로그 추가
        LogFetcher fetcher = new LogFetcher();
        ArrayList<String> logs = new ArrayList<>();
        for (int i = 0; i < preferredCount && !fetcher.isEnd(); i++) {
            logs.add(fetcher.getCurrent());
            fetcher.updateLast();
        }

        // 리스트에 추가한 로그를 모두 전송
        sendMessage("2 " + logs.size());
        for (String log : logs) {
            sendMessage(log);
        }
    }

    /**
     * SPRD 명령어를 처리합니다.
     * @param address 클라이언트 주소
     * @param message 명령어가 포함된 메시지
     */
    private void handleSprd(String address, String message) {
        // 인증 확인
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        // 명령어 유효성 검사
        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        // 명령어 파싱
        String[] tokens = message.substring(5).split(" ", 3);
        if (tokens.length < 3) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        int index = Integer.parseInt(tokens[0]);
        int amount = Integer.parseInt(tokens[1]);
        String name = tokens[2];

        // index 유효성 검사
        if (index < 0 || index >= 5) {
            sendMessage("5 잘못된 인덱스 번호");
            return;
        }

        // amount 유효성 검사
        if (amount < 0) {
            sendMessage("5 잘못된 수량");
            return;
        }

        // 명령어 처리
        machine.updateProductName(index, name);
        machine.updateLeftProductAmount(index, amount);
        sendMessage("0 OK");
    }

    /**
     * IATH 명령어를 처리합니다.
     * @param address 클라이언트 주소
     */
    private void handleIath(String address) {
        // 인증 여부 확인
        boolean contained = server.getAuthorizeds().contains(address);

        // 인증 여부에 따라 메시지 전송
        if (contained) {
            sendMessage("0 OK");
        } else {
            sendMessage("1 인증 실패");
        }
    }

    /**
     * AUTH 명령어를 처리합니다.
     * @param message 명령어가 포함된 메시지
     * @param address 클라이언트 주소
     */
    private void handleAuth(String message, String address) {
        // 명령어 유효성 검사
        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        // 인증 여부 확인
        String password = message.substring(5);
        String errorMessage = Util.confirmLogin(password);

        // 인증 여부에 따라 메시지 전송
        if (!errorMessage.isEmpty()) {
            sendMessage("1 인증 실패: " + errorMessage);
            return;
        }

        if (server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패: 이미 인증됨");
            return;
        }

        sendMessage("0 OK");
        server.getAuthorizeds().add(address);
    }

    /**
     * 자판기에서 클라이언트로 메시지를 보내는 함수입니다.
     * @param message 보낼 메시지
     */
    public void sendMessage(String message) {
        // 메시지 전송
        writer.println(message);

        // 메시지 전송 로그 작성
        String log = String.format("%s:%d << %s", socket.getInetAddress(), socket.getPort(), message);
        Log.writeLog(Log.COMMUNICATION, log);
    }

    /**
     * 클라이언트에서 자판기로 메시지를 받는 함수입니다.
     * @return 받은 메시지
     */
    public String getMessage() {
        // 메시지 수신
        String message;
        try {
            message = reader.nextLine();
        } catch (Exception e) {
            // 만약 수신에 실패하면 연결이 끊긴 것으로 간주
            connected = false;
            return null;
        }

        // 메시지 수신 로그 작성
        String log = String.format("%s:%d >> %s", socket.getInetAddress(), socket.getPort(), message);
        Log.writeLog(Log.COMMUNICATION, log);

        return message;
    }
}
