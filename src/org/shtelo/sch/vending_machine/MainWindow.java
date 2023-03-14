package org.shtelo.sch.vending_machine;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    MainWindow() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException ignored) {}

        JFrame frame = new JFrame();
        frame.setTitle("20223519 JAVA프로그래밍실습 텀 프로젝트");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
