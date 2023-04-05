package org.shtelo.sch.vending_project.vending_machine.subwindow;

import com.formdev.flatlaf.FlatClientProperties;
import org.shtelo.sch.vending_project.util.Log;
import org.shtelo.sch.vending_project.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * 비밀번호 변경을 위한 창
 */
public class ChangePasswordPrompt {
    private JDialog dialog;
    private final JFrame parent;
    private JPasswordField originalPasswordField, passwordField, rePasswordField;
    private JButton confirmButton;
    private JLabel conditionLabel;

    ChangePasswordPrompt(JFrame parent) {
        this.parent = parent;

        makeWindow();
    }

    /**
     * Dialog 창 만들기
     */
    private void makeWindow() {
        dialog = new JDialog();
        dialog.setSize(new Dimension(400, 220));
        dialog.setLocationRelativeTo(parent);
        dialog.setTitle("비밀번호 변경...");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        makeTitle(panel);
        makeTable(panel);
        makeConsole(panel);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * 패널에 타이틀을 추가합니다.
     * @param panel 타이틀을 추가할 <code>JPanel</code> 객체
     */
    private void makeTitle(JPanel panel) {
        JLabel title = new JLabel("비밀번호 변경");
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h4");
        panel.add(title, BorderLayout.PAGE_START);
    }

    /**
     * 패널에 비밀번호 입력을 위한 표를 만듭니다.
     * @param panel 표를 추가할 <code>JPanel</code> 객체
     */
    private void makeTable(JPanel panel) {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        // 비밀번호 입력을 위한 표
        JPanel table = new JPanel();
        table.setLayout(new GridLayout(3, 2));

        table.add(new JLabel("현재 비밀번호"));
        originalPasswordField = new JPasswordField();
        table.add(originalPasswordField);

        @SuppressWarnings("DuplicatedCode") DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Util.checkPasswordCondition(passwordField, rePasswordField, conditionLabel, confirmButton);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                Util.checkPasswordCondition(passwordField, rePasswordField, conditionLabel, confirmButton);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                Util.checkPasswordCondition(passwordField, rePasswordField, conditionLabel, confirmButton);
            }
        };

        table.add(new JLabel("새 비밀번호"));
        passwordField = new JPasswordField();
        passwordField.getDocument().addDocumentListener(documentListener);
        table.add(passwordField);

        table.add(new JLabel("새 비밀번호 확인"));
        rePasswordField = new JPasswordField();
        rePasswordField.getDocument().addDocumentListener(documentListener);
        table.add(rePasswordField);

        tablePanel.add(table, BorderLayout.PAGE_START);

        // 비밀번호 유효성 검사를 위한 레이블
        conditionLabel = new JLabel();
        tablePanel.add(conditionLabel, BorderLayout.PAGE_END);

        panel.add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * 확인 버튼 등 메타적인 조작 패널을 추가합니다.
     * @param panel 조작 패널을 추가할 <code>JPanel</code> 객체
     */
    private void makeConsole(JPanel panel) {
        JPanel console = new JPanel();
        console.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)));
        console.add(cancelButton);

        confirmButton = new JButton("확인");
        confirmButton.addActionListener(e -> confirm());
        dialog.getRootPane().setDefaultButton(confirmButton);
        console.add(confirmButton);

        panel.add(console, BorderLayout.PAGE_END);
    }

    /**
     * 입력한 비밀번호를 확인하고 비밀번호를 변경합니다.
     */
    private void confirm() {
        // 현재 비밀번호 확인
        String message = Util.confirmLogin(originalPasswordField);

        if (!message.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, message);
            return;
        }

        // 비밀번호 변경
        String password = String.valueOf(passwordField.getPassword());
        Util.setPassword(password);

        JOptionPane.showMessageDialog(dialog, "비밀번호가 설정되었습니다.\n설정된 비밀번호를 입력해서 관리자 콘솔에 접속할 수 있습니다.");
        Log.writeLog(Log.ADMIN_INFO, "관리자가 비밀번호를 변경했습니다.");
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
        parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
    }
}
