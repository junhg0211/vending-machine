package org.shtelo.sch.vending_project.vending_machine.subwindow;

import org.shtelo.sch.vending_project.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * 비밀번호 초기 설정을 위한 다이얼로그
 */
public class PasswordDialog {
    private final JFrame parent;
    private JDialog dialog;
    private JPasswordField passwordField, rePasswordField;
    private JLabel conditionLabel;
    private JButton confirmButton;

    PasswordDialog(JFrame parent) {
        this.parent = parent;
        makeDialog();
    }

    private void makeDialog() {
        dialog = new JDialog(parent);
        dialog.setTitle("20223519 - 자판기 - 관리자 콘솔 로그인");
        dialog.setMinimumSize(new Dimension(500, 150));
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parent);

        // 화면 여백을 위한 패널
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        // 내용 채우기
        makeField(panel);
        makeConsole(panel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * 비밀번호 입력을 위한 텍스트 필드
     */
    private void makeField(JPanel panel) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(2, 2));

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

        passwordField = new JPasswordField();
        passwordField.getDocument().addDocumentListener(documentListener);
        fieldPanel.add(new JLabel("비밀번호 입력"));
        fieldPanel.add(passwordField);

        rePasswordField = new JPasswordField();
        rePasswordField.getDocument().addDocumentListener(documentListener);
        fieldPanel.add(new JLabel("비밀번호 재입력"));
        fieldPanel.add(rePasswordField);

        panel.add(fieldPanel, BorderLayout.PAGE_START);
    }

    /**
     * 메타 버튼과 비밀번호 조건 부분
     */
    private void makeConsole(JPanel panel) {
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BorderLayout());

        // 비밀번호 조건 표시를 위한 라벨
        conditionLabel = new JLabel();
        lowerPanel.add(conditionLabel, BorderLayout.WEST);

        // 메타 버튼
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        { // 비밀번호 입력 취소 버튼
            JButton cancelButton = new JButton("취소");
            cancelButton.addActionListener(
                e -> dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)));
            consolePanel.add(cancelButton);
        }
        { // 비밀번호 확정 버튼
            confirmButton = new JButton("확인");
            confirmButton.setEnabled(false);
            confirmButton.addActionListener(e -> confirmPassword());
            dialog.getRootPane().setDefaultButton(confirmButton);
            consolePanel.add(confirmButton);
        }

        lowerPanel.add(consolePanel, BorderLayout.EAST);

        panel.add(lowerPanel, BorderLayout.PAGE_END);
    }

    /**
     * 비밀번호를 확정하고 창을 닫습니다.
     */
    private void confirmPassword() {
        String password = String.valueOf(passwordField.getPassword());

        Util.setPassword(password);

        JOptionPane.showMessageDialog(dialog, "비밀번호가 설정되었습니다.\n설정된 비밀번호를 입력해서 관리자 콘솔에 접속할 수 있습니다.");
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
    }
}
