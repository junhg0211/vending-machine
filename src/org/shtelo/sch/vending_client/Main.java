package org.shtelo.sch.vending_client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9832);
        System.out.println("Connected to server");

        Scanner reader = new Scanner(socket.getInputStream());
        PrintStream writer = new PrintStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("SERVER << ");
            String message = scanner.nextLine();
            writer.println(message);

            System.out.print("SERVER >> ");
            String response = reader.nextLine();
            System.out.println(response);

            String[] tokens = response.split(" ");

            if (tokens[0].equalsIgnoreCase("4")) {
                break;
            } else if (tokens[0].equalsIgnoreCase("2")) {
                int count = Integer.parseInt(tokens[1]);
                for (int i = 0; i < count; i++) {
                    System.out.print("SERVER >> ");
                    System.out.println(reader.nextLine());
                }
            }
        }

        System.out.println("Closing connection");

        reader.close();
        writer.close();
        socket.close();
    }
}
