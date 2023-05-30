package org.shtelo.sch.vending_project;

import org.shtelo.sch.vending_project.server.Server;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

public class Main {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        Server server = new Server(machine);

        server.start();
        machine.start();
    }
}