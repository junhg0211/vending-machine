package org.shtelo.sch.vending_project;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.shtelo.sch.vending_project.vending_machine.VendingMachine;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Swing 테마 변경
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException ignored) {}

        new VendingMachine();
    }
}