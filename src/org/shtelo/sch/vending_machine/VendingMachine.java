package org.shtelo.sch.vending_machine;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;

public class VendingMachine {
    private final NumberFormat numberFormat;

    public VendingMachine() {
        numberFormat = NumberFormat.getInstance();

        buildWindow(); // 화면 띄우기
    }

    /**
     * 자판기 화면을 만들고 띄웁니다.
     */
    private void buildWindow() {
        // Swing 테마 변경
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException ignored) {}

        // 자판기 화면 프레임 제작
        JFrame frame = new JFrame();
        frame.setTitle("20223519 - 자판기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setLocationRelativeTo(null);

        // 페이지 프레임 여백
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setLayout(new BorderLayout());

        // 페이지 내용 추가
        makeTitle(panel);
        makeMenu(panel);
        makeConsole(panel);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 자판기 상단에 타이틀을 추가합니다.
     */
    private void makeTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("20223519 Java 자판기");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        panel.add(titleLabel, BorderLayout.PAGE_START);
    }

    /**
     * 자판기의 메뉴 선택 부분을 추가합니다.
     */
    private void makeMenu(JPanel panel) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(5, 3));

        EmptyBorder nameLabelBorder = new EmptyBorder(0, 8, 0, 0);
        for (int i = 0; i < 5; i++) {
            JButton button = new JButton("구매");
            menuPanel.add(button);

            String name = String.format("%d번 음료", i+1);
            JLabel nameLabel = new JLabel(name);
            nameLabel.setBorder(nameLabelBorder);
            menuPanel.add(nameLabel);

            String price = numberFormat.format(1000);
            JLabel priceLabel = new JLabel(price, SwingConstants.RIGHT);
            menuPanel.add(priceLabel);
        }

        panel.add(menuPanel, BorderLayout.CENTER);
    }

    /**
     * 자판기의 사용자 조작 메뉴를 추가합니다.
     */
    private void makeConsole(JPanel panel) {
        JPanel consolePanel = new JPanel();
        consolePanel.setBorder(new EmptyBorder(0, 100, 0, 0));
        consolePanel.setLayout(new BorderLayout());

        // 구매자 메뉴
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());

        { // 현금 투입 버튼
            JButton cashButton = new JButton("현금 투입");
            customerPanel.add(cashButton, BorderLayout.PAGE_START);
        }
        { // 거스름돈 버튼
            JButton changeButton = new JButton("거스름");
            customerPanel.add(changeButton, BorderLayout.CENTER);
        }
        { // 투입금 표시 창
            JPanel cashPanel = new JPanel();
            cashPanel.setLayout(new GridLayout());

            JLabel cashLabel = new JLabel("투입금: ", SwingConstants.LEFT);
            cashPanel.add(cashLabel);

            JLabel cashAmountLabel = new JLabel("0", SwingConstants.RIGHT);
            cashPanel.add(cashAmountLabel);

            customerPanel.add(cashPanel, BorderLayout.PAGE_END);
        }

        consolePanel.add(customerPanel, BorderLayout.PAGE_START);

        // 메타 패널

        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());
        { // 관리자 콘솔 버튼
            JButton adminButton = new JButton("관리자 콘솔");
            metaPanel.add(adminButton, BorderLayout.PAGE_START);
        }
        { // 프로그램 종료 버튼
            JButton quitButton = new JButton("종료");
            metaPanel.add(quitButton, BorderLayout.PAGE_END);
        }

        consolePanel.add(metaPanel, BorderLayout.PAGE_END);

        panel.add(consolePanel, BorderLayout.EAST);
    }
}
