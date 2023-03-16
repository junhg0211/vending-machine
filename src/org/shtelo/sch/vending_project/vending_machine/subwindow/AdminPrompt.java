package org.shtelo.sch.vending_project.vending_machine.subwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;

public class AdminPrompt {
    private JFrame frame;

    public AdminPrompt() {
        makeWindow();
    }

    private void makeWindow() {
        frame = new JFrame();
        frame.setTitle("20223519 - 자판기 - 관리자 콘솔 로그인");
        frame.setMinimumSize(new Dimension(400, 150));
        frame.setLocationRelativeTo(null);

        // 모서리 여백을 위한 서브콘테이너 제시
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 내용
        makeEntry(panel);
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
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
        JPasswordField passwordField = new JPasswordField();
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
        cancelButton.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
        metaPanel.add(cancelButton);

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        metaPanel.add(loginButton);

        panel.add(metaPanel, BorderLayout.PAGE_END);
    }
}
