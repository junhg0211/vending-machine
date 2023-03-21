package org.shtelo.sch.vending_project.vending_machine.subwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 관리자 콘솔
 */
public class AdminConsole {
    private final JFrame parent;

    AdminConsole(JFrame parent) {
        this.parent = parent;

        makeWindow();
    }

    private void makeWindow() {
        JFrame frame = new JFrame();
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setLocationRelativeTo(parent);
        frame.setTitle("20223519 - 자판기 - 관리자 콘솔");

        // 가장자리 margin을 위한 패널
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 내용 추가
        panel.add(new JLabel("어드민 패널"));

        frame.add(panel);
        frame.setVisible(true);
    }
}
