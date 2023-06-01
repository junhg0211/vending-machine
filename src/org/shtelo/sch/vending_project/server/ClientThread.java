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

            if (message.length() < 4) {
                sendMessage("5 잘못된 명렁어");
                continue;
            }

            String address = String.format("%s:%d", socket.getInetAddress(), socket.getPort());
            String command = message.substring(0, 4);

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

        server.getAuthorizeds().remove(String.format("%s:%d", socket.getInetAddress(), socket.getPort()));
    }

    private void handleQuit() {
        connected = false;
        sendMessage("4 통신 종료");
    }

    private void handleGsel(String address, String message) {
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        SellLogger logger = machine.getSellLogger();
        String date = message.substring(5);
        if (!logger.isLogOn(date)) {
            sendMessage("1 로그 없음");
            return;
        }

        DailyLog log = logger.getLog(date);
        HashMap<String, Integer> sells = log.getSells();
        sendMessage("2 " + sells.size());
        sells.forEach((kind, amount) -> sendMessage(String.format("%s %d", kind, amount)));
    }

    private void handleGinv(String address) {
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        sendMessage("3 5");
        List<Product> juices = machine.getInventory().getJuices();
        for (Product juice : juices) {
            Kind kind = juice.getKind();
            sendMessage(String.format("%s %d %d", kind.getName(), kind.getPrice(), juice.getAmount()));
        }
    }

    private void handleGlog(String address, String message) {
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        if (message.length() < 6) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        int preferredCount = Integer.parseInt(message.substring(5));
        LogFetcher fetcher = new LogFetcher();
        ArrayList<String> logs = new ArrayList<>();
        for (int i = 0; i < preferredCount && !fetcher.isEnd(); i++) {
            logs.add(fetcher.getCurrent());
            fetcher.updateLast();
        }

        sendMessage("2 " + logs.size());
        for (String log : logs) {
            sendMessage(log);
        }
    }

    private void handleSprd(String address, String message) {
        if (!server.getAuthorizeds().contains(address)) {
            sendMessage("1 인증 실패");
            return;
        }

        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        String[] tokens = message.substring(5).split(" ", 3);
        if (tokens.length < 3) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        int index = Integer.parseInt(tokens[0]);
        int amount = Integer.parseInt(tokens[1]);
        String name = tokens[2];

        machine.updateProductName(index, name);
        machine.updateLeftProductAmount(index, amount);

        sendMessage("0 OK");
    }

    private void handleIath(String address) {
        boolean contained = server.getAuthorizeds().contains(address);

        if (contained) {
            sendMessage("0 OK");
        } else {
            sendMessage("1 인증 실패");
        }
    }

    private void handleAuth(String message, String address) {
        if (message.length() < 5) {
            sendMessage("5 잘못된 명령어");
            return;
        }

        String password = message.substring(5);
        String errorMessage = Util.confirmLogin(password);

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

    public void sendMessage(String message) {
        writer.println(message);
        String log = String.format("%s:%d << %s", socket.getInetAddress(), socket.getPort(), message);
        Log.writeLog(Log.COMMUNICATION, log);
    }

    public String getMessage() {
        String message;
        try {
            message = reader.nextLine();
        } catch (Exception e) {
            connected = false;
            return null;
        }

        String log = String.format("%s:%d >> %s", socket.getInetAddress(), socket.getPort(), message);
        Log.writeLog(Log.COMMUNICATION, log);

        return message;
    }
}
