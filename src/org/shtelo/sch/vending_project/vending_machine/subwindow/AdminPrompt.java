package org.shtelo.sch.vending_project.vending_machine.subwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;

public class AdminPrompt {
    private final JFrame parent;
    private JDialog dialog;
    public static final String PASSWORD_PATH = "res/password.txt";
    private JPasswordField passwordField;

    public AdminPrompt(JFrame parent) {
        this.parent = parent;

        File file = new File(PASSWORD_PATH);

        // 비밀번호가 초기설정되어있다면 관리자 로그인을 위한 창을 열고
        // 비밀번호가 설정되어있지 않다면 비밀번호 설정을 위한 창을 연다.
        if (file.exists())
            makeLoginDialog();
        else
            makePasswordDialog();
    }

    /**
     * 비밀번호 입력을 위한 창을 엽니다.
     */
    private void makeLoginDialog() {
        dialog = new JDialog(parent);
        dialog.setTitle("20223519 - 자판기 - 관리자 콘솔 로그인");
        dialog.setMinimumSize(new Dimension(400, 150));
        dialog.setLocationRelativeTo(null);

        // 모서리 여백을 위한 서브콘테이너 제시
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 내용
        makeEntry(panel);
        makeConsole(panel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * 비밀번호 초기 설정을 위한 창을 엽니다.
     */
    private void makePasswordDialog() {
        JOptionPane.showMessageDialog(parent, "비밀번호가 설정되어있지 않습니다.\n비밀번호 설정을 위한 창으로 이동합니다.");
        new PasswordDialog(parent);
    }

    /**
     * 비밀번호 입력과 로그인 버튼 등, 기본적인 로그인을 위한 부분을 구현합니다.
     * @param panel 로그인을 위한 엔트리를 구현할 <code>JPanel</code> 객체
     */
    private void makeEntry(JPanel panel) {
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BorderLayout());

        // 비밀번호 라벨
        JLabel passwordLabel = new JLabel("비밀번호");
        passwordLabel.setBorder(new EmptyBorder(0, 0, 0, 8));
        entryPanel.add(passwordLabel, BorderLayout.WEST);

        // 비밀번호 입력 필드
        passwordField = new JPasswordField();
        entryPanel.add(passwordField, BorderLayout.CENTER);

        panel.add(entryPanel, BorderLayout.PAGE_START);
    }

    /**
     * 확인, 취소, 비밀번호 변경 등 메타 작업을 위한 부분
     * @param panel 메타 작업 부분을 추가할 <code>JPanel</code> 객체
     */
    private void makeConsole(JPanel panel) {
        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // 취소 버튼
        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(e -> dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING)));
        metaPanel.add(cancelButton);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> confirmLogin());
        dialog.getRootPane().setDefaultButton(loginButton);
        metaPanel.add(loginButton);

        panel.add(metaPanel, BorderLayout.PAGE_END);
    }

    /**
     * 입력한 비밀번호로 로그인을 진행합니다.
     */
    private void confirmLogin() {
        String password = String.valueOf(passwordField.getPassword());

        File file = new File(PASSWORD_PATH);
        String correct;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            correct = reader.readLine();

            reader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!password.equals(correct)) {
            JOptionPane.showMessageDialog(dialog, "비밀번호가 틀립니다.");
            return;
        }

        JOptionPane.showMessageDialog(dialog, "로그인 성공~");
    }
}
