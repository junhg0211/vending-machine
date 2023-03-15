package org.shtelo.sch.vending_project.vending_machine;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.shtelo.sch.vending_project.vending_machine.data_type.Inventory;
import org.shtelo.sch.vending_project.vending_machine.data_type.Product;
import org.shtelo.sch.vending_project.vending_machine.data_type.Wallet;
import org.shtelo.sch.vending_project.vending_machine.data_type.Kind;
import org.shtelo.sch.vending_project.vending_machine.prompt.cash_input.CashInputPrompt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;

public class VendingMachine {
    private JFrame frame;
    private final NumberFormat numberFormat;
    private final Wallet wallet;
    private int cash;
    private JLabel cashAmountLabel;

    public VendingMachine() {
        numberFormat = NumberFormat.getInstance();

        wallet = Wallet.getWallet();
        cash = 0;

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
        frame = new JFrame();
        frame.setTitle("20223519 - 자판기");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 400));
        frame.setMinimumSize(new Dimension(500, 300));
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
        menuPanel.setLayout(new GridLayout(5, 4));

        Inventory inventory = Inventory.getInventory();

        // 메뉴 목록 만들기
        EmptyBorder nameLabelBorder = new EmptyBorder(0, 8, 0, 0);
        for (int i = 0; i < 5; i++) {
            JButton button = new JButton("구매");
            menuPanel.add(button);

            Product juice = inventory.getJuices().get(i);
            Kind kind = juice.getKind();

            // 메뉴 이름
            String name = kind.getName();
            JLabel nameLabel = new JLabel(name);
            nameLabel.setBorder(nameLabelBorder);
            menuPanel.add(nameLabel);

            // 메뉴 남은 수량
            String left = Integer.toString(juice.getAmount());
            JLabel leftLabel = new JLabel(left);
            menuPanel.add(leftLabel);

            // 메뉴 가격
            String price = numberFormat.format(kind.getPrice());
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
            cashButton.addActionListener(e -> new CashInputPrompt(this));
            customerPanel.add(cashButton, BorderLayout.PAGE_START);
        }
        { // 거스름돈 버튼
            JButton changeButton = new JButton("거스름");
            changeButton.addActionListener(e -> changeCash());
            customerPanel.add(changeButton, BorderLayout.CENTER);
        }
        { // 투입금 표시 창
            JPanel cashPanel = new JPanel();
            cashPanel.setLayout(new GridLayout());

            JLabel cashLabel = new JLabel("투입금: ", SwingConstants.LEFT);
            cashPanel.add(cashLabel);

            cashAmountLabel = new JLabel("0", SwingConstants.RIGHT);
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
            quitButton.addActionListener(e -> System.exit(0));
            metaPanel.add(quitButton, BorderLayout.PAGE_END);
        }

        consolePanel.add(metaPanel, BorderLayout.PAGE_END);

        panel.add(consolePanel, BorderLayout.EAST);
    }

    private void changeCash() {
        int[] result = wallet.change(cash);

        String text = String.format(
                "거스름을 처리했습니다.%n" +
                "5,000원: %d장%n" +
                "1,000원: %d장%n" +
                "500원: %d개%n" +
                "100원: %d개%n" +
                "50원: %d개%n" +
                "10원: %d개%n%n" +
                "거스르지 못한 돈: %s원%n" +
                "거스른 돈: %s원",
                result[5], result[4], result[3], result[2], result[1], result[0],
                numberFormat.format(result[6]), numberFormat.format(cash - result[6]));

        JOptionPane.showMessageDialog(frame, text);

        updateCash(result[6]);
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * `amount` 만큼의 현금을 자판기에 투입한 것으로 처리합니다.
     */
    public void insertCash(int amount) {
        updateCash(cash + amount);
    }

    /**
     * 투입한 금액의 표시 액수를 cash 변수에 따라서 변경합니다.
     */
    private void updateCash(int amount) {
        cash = amount;
        String text = numberFormat.format(cash);
        cashAmountLabel.setText(text);
    }

    public Wallet getWallet() {
        return wallet;
    }
}