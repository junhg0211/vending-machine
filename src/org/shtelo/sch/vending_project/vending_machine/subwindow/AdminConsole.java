package org.shtelo.sch.vending_project.vending_machine.subwindow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * 관리자 콘솔
 */
public class AdminConsole {
    private final JFrame parent;
    private JFrame frame;

    AdminConsole(JFrame parent) {
        this.parent = parent;

        makeWindow();
    }

    private void makeWindow() {
        frame = new JFrame();
        frame.setMinimumSize(new Dimension(500, 600));
        frame.setLocationRelativeTo(parent);
        frame.setTitle("20223519 - 자판기 - 관리자 콘솔");

        // 가장자리 margin을 위한 패널
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // 탭으로 작업 구분
        JTabbedPane pane = new JTabbedPane();

        // 내용 추가
        makeSalesInfo(pane);
        makeInventoryManager(pane);
        makeCashInventoryManager(pane);
        makeLogInfo(pane);

        panel.add(pane);

        // 화면 닫기 버튼 추가
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 매출 정보 패널을 제작합니다.
     * @param pane 매출 정보 패널이 제작될 <code>JTabbedPane</code> 객체
     */
    private void makeSalesInfo(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("매출 정보 패널"));

        pane.addTab("매출", panel);
    }

    /**
     * 재고·상품 관리 패널을 제작합니다.
     * @param pane 재고관리 패널이 제작될 <code>JTabbedPane</code> 객체
     */
    private void makeInventoryManager(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("상품 관리 패널"));

        pane.addTab("상품", panel);
    }

    /**
     * 현금 통 관리 패널을 제작합니다.
     * @param pane 현금 통 관리 패널이 만들어질 <code>JTabbedPane</code> 객체
     */
    private void makeCashInventoryManager(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("현금"));

        pane.addTab("현금", panel);
    }

    /**
     * 관리자 로그를 확인하는 패널을 제작합니다.
     * @param pane 관리자 로그 패널이 만들어질 <code>JTabbedPane</code> 객체
     */
    private void makeLogInfo(JTabbedPane pane) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(new JLabel("로그"));

        pane.addTab("로그", panel);
    }

    /**
     * 관리자 패널 메타 조작을 위한 조작판을 제작합니다.
     * @param panel 메타 조작 패널을 제작할 <code>JPanel</code> 객체
     */
    private void makeConsole(JPanel panel) {
        JPanel consolePanel = new JPanel();
        consolePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        { // 닫기 버튼
            JButton closeButton = new JButton("닫기");
            closeButton.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
            consolePanel.add(closeButton);
        }

        panel.add(consolePanel, BorderLayout.PAGE_END);
    }
}
