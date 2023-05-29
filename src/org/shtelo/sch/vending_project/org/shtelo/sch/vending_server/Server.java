package org.shtelo.sch.vending_project.org.shtelo.sch.vending_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        // open server
        ServerSocket server = new ServerSocket(8888);
        System.out.printf("Server is listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());

        // accept connection
        Socket socket = server.accept();
        System.out.printf("Accepted connection from %s:%d\n", socket.getInetAddress(), socket.getPort());

        // ready for reading from client
        Scanner reader = new Scanner(socket.getInputStream());
        // ready for writing to client
        PrintStream writer = new PrintStream(socket.getOutputStream());

        // read a line and calculate the result
        String line = reader.nextLine();
        line = line.toUpperCase();
        System.out.printf("Got %s from %s:%d\n", line, socket.getInetAddress(), socket.getPort());

        // send calculated string to client
        writer.println(line);

        // free resources
        socket.close();
        server.close();
    }
}
