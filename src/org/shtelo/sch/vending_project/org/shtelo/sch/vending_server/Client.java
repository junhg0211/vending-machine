package org.shtelo.sch.vending_project.org.shtelo.sch.vending_server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        // create connection to the server
        Socket socket = new Socket("127.0.0.1", 8888);
        System.out.printf("Connected to %s:%d\n", socket.getInetAddress(), socket.getPort());

        // ready for reading from server
        Scanner reader = new Scanner(socket.getInputStream());
        // ready for writing to server
        PrintStream writer = new PrintStream(socket.getOutputStream());

        // send string to server
        writer.println("Hello, world!");
        System.out.println("Sent Hello, world!");

        // get the result and print
        String result = reader.nextLine();
        System.out.println(result);

        // free resources
        socket.close();
    }
}
