package org.shtelo.sch.vending_project;

import org.shtelo.sch.vending_project.server.Server;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

public class Main {
    public static void main(String[] args) {
        // 자판기와 자판기 서버를 제시합니다.
        VendingMachine machine = new VendingMachine();
        Server server = new Server(machine);
        machine.setServer(server);

        // 자판기와 자판기 서버를 실행합니다.
        server.start();
        machine.start();
    }
}